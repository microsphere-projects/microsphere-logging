# Microsphere Logging — User Guide (release branch / current)

[中文版](./zh/overview.md)

> Branch analyzed: `release` · Version: `0.2.3-SNAPSHOT` (latest published: `0.2.2`)

---

## What is Microsphere Logging?

Microsphere Logging is a **unified abstraction layer for runtime logging management** across multiple Java logging
frameworks. It does **not** replace your logging framework — it adds a framework-agnostic management API on top of
it, so you can:

- query and change log levels **at runtime, without restarting** the service;
- write one management/ops code path that works on **JUL, Logback, Log4j 1.x and Log4j 2**;
- expose logging configuration over **JMX** (`io.microsphere.logging:type=<BackendName>`);
- test application behavior **across a matrix of log levels** with ready-made JUnit 4 / JUnit Jupiter 5 support;
- extend the abstraction via **Java SPI** (`META-INF/services/io.microsphere.logging.Logging`).

## Module Map

| Module | Document | What it gives you |
|--------|----------|-------------------|
| `microsphere-logging-commons` | [logging-commons.md](./logging-commons.md) | `Logging` interface, `LoggingUtils` (SPI loading), `LoggingLevelsResolver`, JMX adapter/registrar |
| `microsphere-java-logging` | [java-logging.md](./java-logging.md) | `JavaLogging` — JUL backend adapter |
| `microsphere-logback` | [logback.md](./logback.md) | `LogbackLogging` + `LogbackUtils` |
| `microsphere-log4j` | [log4j.md](./log4j.md) | `Log4jLogging`, `LoggerFactory` SPI, `Log4jUtils` |
| `microsphere-log4j2` | [log4j2.md](./log4j2.md) | `Log4j2Logging`, `LoggerFactory` SPI, `Log4j2Utils`, `InMemoryAppender`, layouts |
| `microsphere-logging-test` | [logging-test.md](./logging-test.md) | `@LoggingLevelsTest` / `@LoggingLevelsClass` / `LoggingLevelsRule` |
| `microsphere-logging-parent` / `-dependencies` | [parent-and-dependencies.md](./parent-and-dependencies.md) | Parent POM + BOM version management |
| `microsphere-logging-examples` | [examples.md](./examples.md) | Runnable `LoggingExample` |

## Quick Start (5 minutes)

1. Import the BOM:

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
```

2. Add the one backend module matching your framework (e.g. Logback):

```xml
<dependency>
    <groupId>io.github.microsphere-projects</groupId>
    <artifactId>microsphere-logback</artifactId>
</dependency>
```

3. Manage logs at runtime:

```java
import io.microsphere.logging.Logging;
import static io.microsphere.logging.LoggingUtils.load;

Logging logging = load();                            // SPI: smallest priority value wins
logging.setLoggerLevel("io.microsphere", "DEBUG");   // effective immediately
```

> Passing `null` as the level only restores inheritance on the **JUL** adapter. On Logback, Log4j and Log4j2 the
> level string is parsed with `Level.toLevel(...)`, whose documented default is **`DEBUG`** — see
> [Level semantics across backends](#level-semantics-across-backends).

4. (Optional) Open it to operations via JMX:

```java
io.microsphere.logging.jmx.LoggingMXBeanRegistrar.registerAll();
```

## Version & Compatibility Summary

| Item | Value |
|------|-------|
| `release` branch version | `0.2.3-SNAPSHOT` |
| Latest published version | `0.2.2` |
| Java baseline | 8+ |
| microsphere-java dependency line | 0.3.19 (pinned by `microsphere-logging-parent`) |
| JUL | JDK built-in (`LoggingMXBean` since Java 6) |
| Logback | 1.2+ (SLF4J 1.7+) |
| Log4j | 1.2+ (EOL upstream — legacy use only) |
| Log4j2 | 2.4+ |
| JUnit | 4+ / Jupiter 5.13+ (test module) |

The javadoc `@since 1.0.0` tags in the source denote the API generation, not a published `1.x` version line: this
repository publishes `0.1.x` / `0.2.x` versions and has **no `release-1.x` branch**, so only the `current` document
set exists under `docs/`.

## Backend Priority (who wins `load()`)

Priority values are compared **ascending**, and `io.microsphere.lang.Prioritized` defines
`MAX_PRIORITY = Integer.MIN_VALUE` / `MIN_PRIORITY = Integer.MAX_VALUE`. So the *smaller* the number, the *higher*
the priority, and `LoggingUtils.load()` returns the first element of the sorted SPI list.

| Backend | Priority value | Order returned by `load()` |
|---------|----------------|----------------------------|
| Log4j2Logging | `NORMAL_PRIORITY - 5` (`-5`) | **1st** — highest priority among the shipped adapters |
| Log4jLogging | `NORMAL_PRIORITY - 3` (`-3`) | 2nd |
| LogbackLogging | `NORMAL_PRIORITY` (`0`) | 3rd |
| JavaLogging (JUL) | `NORMAL_PRIORITY + 10` (`10`) | 4th — lowest priority |

Practical consequences:

- With all four adapters on the classpath, `load()` hands you **`Log4j2Logging`**, not JUL.
- To address a specific backend, use `loadAll()` (an unmodifiable, priority-sorted list) and filter by `getName()`.
- SPI results are **cached by service type** in `microsphere-java-core` (`LoggingUtils` passes `cached = true`), so
  the first load in a JVM wins for later calls, even with a different `ClassLoader`.

```java
Logging logback = LoggingUtils.loadAll().stream()
        .filter(l -> "Logback".equals(l.getName()))
        .findFirst()
        .orElseThrow();
```

## Level semantics across backends

The `Logging` interface promises one behaviour, but the four adapters sit on frameworks with different native
semantics. The table below is the contract you can rely on when you call the API (each claim is asserted by the
module's own tests).

| Behaviour | `JavaLogging` | `LogbackLogging` | `Log4jLogging` | `Log4j2Logging` |
|-----------|---------------|------------------|----------------|-----------------|
| Root logger name | `""` | `"ROOT"` | `"root"` | `""` |
| Supported levels | OFF, SEVERE, WARNING, INFO, CONFIG, FINE, FINER, FINEST, ALL | OFF, ERROR, WARN, INFO, DEBUG, TRACE, ALL | OFF, FATAL, ERROR, WARN, INFO, DEBUG, TRACE, ALL | OFF, FATAL, ERROR, WARN, INFO, DEBUG, TRACE, ALL |
| `getLoggerLevel("not.declared.Anywhere")` | `IllegalArgumentException: Logger … does not exist` | a level string — Logback creates the logger on demand | a level string — Log4j creates the logger on demand | a level string — Log4j2 creates the logger on demand |
| `getLoggerLevel` for a logger with no declared level | inherited value reported by JUL | effective level | effective level | `null` (Log4j2 keeps the declared level, which may be unset) |
| `setLoggerLevel(name, null)` | `Level` set to `null` ⇒ **real inheritance** | parsed by `Level.toLevel(null)` ⇒ **DEBUG** | `Level.toLevel(null)` ⇒ **DEBUG** | `Level.toLevel(null)` ⇒ **DEBUG** |
| `setLoggerLevel(name, "BOGUS")` | `IllegalArgumentException: Unknown level "BOGUS"` | silently **DEBUG** | silently **DEBUG** | silently **DEBUG** |
| `setLoggerLevel(rootName, level)` | works | works | works | works |
| `getParentLoggerName(rootName)` | delegates to the JDK ⇒ `""` | `null` | `null` | `null` |
| `getParentLoggerName("nodots")` | JDK behaviour | `"ROOT"` | `"root"` | `""` |

Three consequences worth memorising:

1. **`null` means "inherit" only on JUL.** Everywhere else it means "set to DEBUG", and so does any level string the
   backend cannot parse. Validate the level name yourself (against `getSupportedLoggingLevels()`) before calling
   `setLoggerLevel` if you need the `IllegalArgumentException` the javadoc advertises.
2. **A level string of `"null"` may come back from Log4j2.** `Log4j2Utils.getLevel` returns
   `logger.getLevel()`, which is the *declared* level and can be unset; the other adapters fall back to the
   effective level. Treat a `null` return as "inherited".
3. **`getLoggerNames()` is a snapshot of live loggers, and it can omit the root logger** — Logback's
   `getLoggerList()` does not contain `"ROOT"` even though `getRootLoggerName()` returns it.

```java
// test-case evidence — LogbackLoggingTest.java:106-107
assertEquals(ROOT_LOGGER_NAME, logging.getParentLoggerName("io")); // "ROOT"
assertNull(logging.getParentLoggerName(ROOT_LOGGER_NAME));         // not ""
```

## Reading Paths

- **New users**: this page → your backend module document → [logging-commons.md](./logging-commons.md).
- **Operations**: JMX registration in [logging-commons.md](./logging-commons.md); runtime level tuning examples in
  each backend document.
- **Test engineers**: multi-level testing in [logging-test.md](./logging-test.md); log capture via `InMemoryAppender`
  in [log4j2.md](./log4j2.md).
