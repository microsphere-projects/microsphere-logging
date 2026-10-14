# microsphere-logging-commons

[中文版](./zh/logging-commons.md)

> Core API module: the `Logging` abstraction, SPI loading utilities, logging-level resolution and JMX integration.

---

## Version Information

| Item | Value |
|------|-------|
| Branch analyzed | `release` (document set `current`) |
| Artifact | `io.github.microsphere-projects:microsphere-logging-commons` |
| Version on `release` branch | `0.2.3-SNAPSHOT` |
| Latest published version | `0.2.2` |
| Introduced since | `1.0.0` (0.1.x line, see javadoc `@since`) |

## Compatibility

| Requirement | Version |
|-------------|---------|
| Java | 8+ |
| microsphere-java-core | transitive, version pinned by `microsphere-logging-parent` (`microsphere-java` 0.3.19) |
| SLF4J / Log4j / Log4j2 / Logback | optional (`<optional>true</optional>`) — only needed if you reference their classes yourself |

The module has **no hard runtime dependency on any logging backend**; backend-specific classes live in the
`microsphere-java-logging`, `microsphere-logback`, `microsphere-log4j` and `microsphere-log4j2` modules.

## Installation

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>io.github.microsphere-projects</groupId>
            <artifactId>microsphere-logging-dependencies</artifactId>
            <version>0.2.2</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>

<dependencies>
    <dependency>
        <groupId>io.github.microsphere-projects</groupId>
        <artifactId>microsphere-logging-commons</artifactId>
    </dependency>
</dependencies>
```

> In most applications you do not depend on this module directly — a backend module (Logback / Log4j / Log4j2 / JUL)
> brings it in transitively.

## Core Concepts

### 1. `io.microsphere.logging.Logging`

The management interface for a logging facility, broadly compatible with `java.util.logging.LoggingMXBean`. It
extends `io.microsphere.lang.Prioritized`, so multiple backends can coexist and be resolved by priority.

| Method | Contract |
|--------|----------|
| `String getRootLoggerName()` | Name of the root logger (never null): `""` on JUL/Log4j2, `"ROOT"` on Logback, `"root"` on Log4j |
| `List<String> getLoggerNames()` | Currently registered logger names — a live snapshot; Logback's list does **not** contain `"ROOT"` |
| `Set<String> getSupportedLoggingLevels()` | Level names supported by the backend (unmodifiable, unordered) |
| `String getLoggerLevel(String loggerName)` | Per the javadoc: `null` if the logger does not exist, empty string when the level is inherited. **Only JUL follows this literally** — Logback/Log4j create loggers on demand and return the effective level, Log4j2 may return `null` for an undeclared level |
| `void setLoggerLevel(String loggerName, String levelName)` | Set a level by name; `loggerName` must be non-null. `levelName = null` restores inheritance **on JUL only**; Logback/Log4j/Log4j2 parse with `Level.toLevel(…)`, which silently falls back to **DEBUG** for both `null` and unknown names (see [Level semantics across backends](./overview.md#level-semantics-across-backends)) |
| `String getParentLoggerName(String loggerName)` | Default implementation based on the dot hierarchy — returns `null` for the root logger name, `getRootLoggerName()` for a name without a dot, and throws `NullPointerException` on a `null` argument; only `JavaLogging` overrides it |
| `String getName()` | Backend name, e.g. `Java Logging`, `Logback`, `Log4j`, `Log4j2` — also the JMX `type=` property |
| `int getPriority()` | Priority value; **the smaller the number, the higher the priority** (`Prioritized.MAX_PRIORITY == Integer.MIN_VALUE`) |

```java
// test-case evidence — LogbackLoggingTest.java:106-107
assertEquals(ROOT_LOGGER_NAME, logging.getParentLoggerName("io")); // "ROOT"
assertNull(logging.getParentLoggerName(ROOT_LOGGER_NAME));         // null, not ""
```

### 2. `io.microsphere.logging.LoggingUtils`

SPI-based loader (`META-INF/services/io.microsphere.logging.Logging`).

```java
import io.microsphere.logging.Logging;
import static io.microsphere.logging.LoggingUtils.load;
import static io.microsphere.logging.LoggingUtils.loadAll;

// The highest-priority Logging implementation on the classpath
// (smallest getPriority() value — Log4j2Logging among the four shipped adapters)
Logging logging = load();

// All detected implementations, sorted highest priority first (unmodifiable)
List<Logging> all = loadAll();

// With an explicit ClassLoader (common in modular / container environments)
Logging web = load(threadContextClassLoader);
```

`load()` and `loadAll()` throw `IllegalArgumentException` when no `Logging` implementation is declared in
`META-INF/services` — i.e. when no backend module is on the classpath. Both overloads accept an explicit (nullable)
`ClassLoader`, which `microsphere-java-core` resolves through the fallback chain *thread context classloader →
the classloader that loaded `ClassLoaderUtils` → system classloader*, so it is never `null`.

Two caching notes:

- The `ClassLoader` argument may be `null`; it is replaced by the fallback chain above.
- Both methods pass `cached = true` down to the SPI loader, and the cache key is the **service type only**. The
  first `loadAll()` in a JVM wins for every later call, even one made with a different classloader.

### 3. Runtime level management

```java
Logging logging = load();

String level = logging.getLoggerLevel("io.microsphere");   // e.g. "INFO" (null/"" is backend-specific)
logging.setLoggerLevel("io.microsphere", "DEBUG");         // effective immediately, no restart

// Validate before setting: only JUL rejects an unknown level name
if (logging.getSupportedLoggingLevels().contains("TRACE")) {
    logging.setLoggerLevel("io.microsphere", "TRACE");
}
```

> `logging.setLoggerLevel("io.microsphere", null)` means "inherit" **only on JUL**. On Logback, Log4j and Log4j2
> the `null` is fed to `Level.toLevel(…)` and the logger ends up at **DEBUG**.

### 4. `LoggingLevelsResolver` / `DefaultLoggingLevelsResolver`

Resolves the set of level names from a level class by reading its `public static final` fields (e.g.
`ch.qos.logback.classic.Level`, `org.apache.logging.log4j.Level`):

```java
import io.microsphere.logging.DefaultLoggingLevelsResolver;
import ch.qos.logback.classic.Level;

Set<String> levels = DefaultLoggingLevelsResolver.INSTANCE.resolve(Level.class);
// [OFF, ERROR, WARN, INFO, DEBUG, TRACE, ALL] — unmodifiable, no guaranteed order
```

The rule is deliberately narrow: `getFields()` (public fields, inherited included) filtered by *static*, *final*,
and *field type `==` the level class*. That rejects Logback's `int`/`Integer` `*_INT` constants, Log4j 1.x's
`TRACE_INT`, and any private or non-final member — and it accepts enum constants, which is why
`resolve(org.slf4j.event.Level.class)` yields `[ERROR, WARN, INFO, DEBUG, TRACE]`.

| Level class | Resolved names (asserted in `DefaultLoggingLevelsResolverTest`) |
|-------------|-----------------------------------------------------------------|
| `java.util.logging.Level` | OFF, SEVERE, WARNING, INFO, CONFIG, FINE, FINER, FINEST, ALL |
| `ch.qos.logback.classic.Level` | OFF, ERROR, WARN, INFO, DEBUG, TRACE, ALL |
| `org.apache.log4j.Level` | OFF, FATAL, ERROR, WARN, INFO, DEBUG, TRACE, ALL |
| `org.apache.logging.log4j.Level` | OFF, FATAL, ERROR, WARN, INFO, DEBUG, TRACE, ALL |
| `org.slf4j.event.Level` | ERROR, WARN, INFO, DEBUG, TRACE |

Implement `LoggingLevelsResolver` yourself when a backend uses a non-standard level class.

### 5. JMX integration (`io.microsphere.logging.jmx`)

- `LoggingMXBeanAdapter` — adapts a `Logging` instance to the standard `java.util.logging.LoggingMXBean` interface;
  it implements `DelegatingWrapper` and exposes the delegate through `getDelegate()`, with `equals` / `hashCode` /
  `toString` delegating accordingly.
- `LoggingMXBeanRegistrar` — registers each detected `Logging` backend as an MBean under JMX domain
  `io.microsphere.logging`, with `ObjectName` pattern `io.microsphere.logging:type=<BackendName>`.

```java
import io.microsphere.logging.jmx.LoggingMXBeanRegistrar;

// Register all backends found on the classpath
List<ObjectInstance> instances = LoggingMXBeanRegistrar.registerAll();

// Or register one explicitly
ObjectInstance instance = LoggingMXBeanRegistrar.register(logging);

// Inspect what has already been registered (query: io.microsphere.logging:type=*)
Set<ObjectInstance> registered = LoggingMXBeanRegistrar.getRegisteredLoggingObjectInstances();
```

```java
// test-case evidence — LoggingMXBeanRegistrarTest.java:59
assertEquals("io.microsphere.logging:type=Testing", objectName.toString());
```

Behaviour to know before wiring this into production:

- `registerAll()` returns an **unmodifiable** list with one entry per successfully registered (or already
  registered) backend; a backend that fails to register contributes **no entry and no exception**. The test suite
  registers a `ThrowableLogging` whose `getName()` throws and still gets exactly one instance back.
- Registration is **idempotent**: an existing `ObjectName` is detected with `isRegistered`, the current
  `ObjectInstance` is returned, and a warning is logged — no `InstanceAlreadyExistsException`.
- Only the four `java.util.logging.LoggingMXBean` operations are reachable over JMX
  (`getLoggerLevel`, `setLoggerLevel`, `getLoggerNames`, `getParentLoggerName`). `getSupportedLoggingLevels()`,
  `getName()`, and `getPriority()` are **not** part of that interface and cannot be called from a JMX client.
- There is **no unregister method**; registration lives for the JVM's lifetime.
- `JavaLogging.getName()` is `"Java Logging"` (with a space), so its `ObjectName` is
  `io.microsphere.logging:type=Java Logging`. Some JMX clients require you to quote such a value in a query.
- `java.util.logging.LoggingMXBean` itself has been deprecated since Java 9; the adapter is still functional, but
  plan long-lived operational tooling accordingly.

## Backend Priority Summary

`io.microsphere.lang.Prioritized` defines `MAX_PRIORITY = Integer.MIN_VALUE` and `MIN_PRIORITY = Integer.MAX_VALUE`,
and the SPI list is sorted **ascending** by `getPriority()` — so the *smaller* the number, the *higher* the priority:

| Order returned by `loadAll()` | Backend implementation | Module | Priority value |
|-------------------------------|------------------------|--------|----------------|
| 1st | `Log4j2Logging` | microsphere-log4j2 | `NORMAL_PRIORITY - 5` (`-5`) |
| 2nd | `Log4jLogging` | microsphere-log4j | `NORMAL_PRIORITY - 3` (`-3`) |
| 3rd | `LogbackLogging` | microsphere-logback | `NORMAL_PRIORITY` (`0`) |
| 4th | `JavaLogging` | microsphere-java-logging | `NORMAL_PRIORITY + 10` (`10`) |

`LoggingUtils.load()` returns the first element of that sorted list, so with all four adapters present
**Log4j2 is selected**, not JUL. To target a specific backend, use `loadAll()` and filter by `getName()`:

```java
Logging logging = LoggingUtils.loadAll().stream()
        .filter(l -> "Logback".equals(l.getName()))
        .findFirst()
        .orElseThrow();
```

## Related Documents

- [Java Logging (JUL) adapter](./java-logging.md)
- [Logback adapter](./logback.md)
- [Log4j adapter](./log4j.md)
- [Log4j2 adapter](./log4j2.md)
- [Testing support](./logging-test.md)
