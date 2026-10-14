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
| `String getRootLoggerName()` | Name of the root logger (never null) |
| `List<String> getLoggerNames()` | Currently registered logger names |
| `Set<String> getSupportedLoggingLevels()` | Level names supported by the backend |
| `String getLoggerLevel(String loggerName)` | Level name of the logger; `null` if the logger does not exist; empty string if the level is inherited from the parent |
| `void setLoggerLevel(String loggerName, String levelName)` | Set a level by name; `levelName = null` restores inheritance; throws `IllegalArgumentException` for unknown logger or invalid level |
| `String getParentLoggerName(String loggerName)` | Default implementation based on the dot hierarchy |
| `String getName()` | Backend name, e.g. `Java Logging`, `Logback`, `Log4j`, `Log4j2` |
| `int getPriority()` | Higher priority wins when loading via `LoggingUtils.load()` |

### 2. `io.microsphere.logging.LoggingUtils`

SPI-based loader (`META-INF/services/io.microsphere.logging.Logging`).

```java
import io.microsphere.logging.Logging;
import static io.microsphere.logging.LoggingUtils.load;
import static io.microsphere.logging.LoggingUtils.loadAll;

// The highest-priority Logging implementation on the classpath
Logging logging = load();

// All detected implementations, sorted by priority
List<Logging> all = loadAll();

// With an explicit ClassLoader (common in modular / container environments)
Logging web = load(threadContextClassLoader);
```

`load()` and `loadAll()` throw `IllegalArgumentException` when no `Logging` implementation is declared in
`META-INF/services` — i.e. when no backend module is on the classpath. Both overloads accept an explicit (nullable)
`ClassLoader`, which `microsphere-java-core` resolves via the usual fallbacks.

### 3. Runtime level management

```java
Logging logging = load();

String level = logging.getLoggerLevel("io.microsphere");   // e.g. "INFO"
logging.setLoggerLevel("io.microsphere", "DEBUG");         // effective immediately, no restart
logging.setLoggerLevel("io.microsphere", null);            // inherit from the parent logger
```

### 4. `LoggingLevelsResolver` / `DefaultLoggingLevelsResolver`

Resolves the set of level names from a level class by reading its `public static final` fields (e.g.
`ch.qos.logback.classic.Level`, `org.apache.logging.log4j.Level`):

```java
import io.microsphere.logging.DefaultLoggingLevelsResolver;
import ch.qos.logback.classic.Level;

Set<String> levels = DefaultLoggingLevelsResolver.INSTANCE.resolve(Level.class);
// [TRACE, DEBUG, INFO, WARN, ERROR, ...]
```

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
LoggingMXBeanRegistrar.register(logging);

// Inspect what has already been registered
Set<ObjectInstance> registered = LoggingMXBeanRegistrar.getRegisteredLoggingObjectInstances();
```

After registration you can read and change log levels from JConsole, VisualVM, `jcmd`, or any JMX client.

## Backend Priority Summary

| Backend implementation | Module | Priority |
|------------------------|--------|----------|
| `JavaLogging` | microsphere-java-logging | `NORMAL_PRIORITY + 10` (highest) |
| `LogbackLogging` | microsphere-logback | `NORMAL_PRIORITY` |
| `Log4jLogging` | microsphere-log4j | `NORMAL_PRIORITY - 3` |
| `Log4j2Logging` | microsphere-log4j2 | `NORMAL_PRIORITY - 5` |

`LoggingUtils.load()` returns the highest-priority implementation, so with several backends present JUL is selected
first. To target a specific backend, use `loadAll()` and filter by `getName()`:

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
