# microsphere-java-logging

[中文版](./zh/java-logging.md)

> Adapter module for **Java Logging (JUL)**, the JDK built-in logging facility.

---

## Version Information

| Item | Value |
|------|-------|
| Branch analyzed | `release` (document set `current`) |
| Artifact | `io.github.microsphere-projects:microsphere-java-logging` |
| Version on `release` branch | `0.2.3-SNAPSHOT` |
| Latest published version | `0.2.2` |
| Introduced since | `1.0.0` (0.1.x line) |

## Compatibility

| Requirement | Version |
|-------------|---------|
| Java | 8+ (uses `java.util.logging.LoggingMXBean`, available since Java 6) |
| microsphere-logging-commons | same version line (managed by the BOM) |

No third-party dependencies — this module only needs the JDK.

## Installation

```xml
<dependency>
    <groupId>io.github.microsphere-projects</groupId>
    <artifactId>microsphere-java-logging</artifactId>
</dependency>
```

(With the `microsphere-logging-dependencies` BOM imported, no `<version>` is required.)

SPI registration: `META-INF/services/io.microsphere.logging.Logging` → `io.microsphere.logging.jdk.JavaLogging`.

## Key Class: `io.microsphere.logging.jdk.JavaLogging`

`JavaLogging` implements `io.microsphere.logging.Logging` on top of JUL's `LogManager` and
`java.util.logging.LoggingMXBean` (`LogManager.getLoggingMXBean()`).

| Constant / Behavior | Value |
|---------------------|-------|
| `ROOT_LOGGER_NAME` | `""` (empty string, per JUL convention) |
| `PRIORITY` | `NORMAL_PRIORITY + 10` (i.e. `10`) — the **lowest** priority among the shipped adapters (the SPI list sorts ascending) |
| `ALL_LEVELS` | resolved from `java.util.logging.Level` via `DefaultLoggingLevelsResolver` |
| `getName()` | `"Java Logging"` |
| `getRootLoggerName()` | `""` |

Supported level names, exactly as resolved: `OFF`, `SEVERE`, `WARNING`, `INFO`, `CONFIG`, `FINE`, `FINER`, `FINEST`,
`ALL`. JUL exposes no `TRACE`/`DEBUG` names here — `FINEST` is the trace-like level and `FINE` is the debug-like
level, so level names are **not** interchangeable with the other backends.

```java
// test-case evidence — JavaLoggingTest.java:58-60
assertEquals("", ROOT_LOGGER_NAME);
assertEquals(10, PRIORITY);
assertEquals(JAVA_LOGGING_LEVELS, ALL_LEVELS);
```

`JavaLoggingTest` casts `load()` straight to `JavaLogging`, which is safe **only** because that module's classpath
holds a single adapter. As soon as `microsphere-log4j2` or `microsphere-log4j` is also present, `load()` returns
that backend instead (priority `-5` / `-3` beats `10`). If you manage JUL while another framework is actually in
use, load all candidates and pick explicitly (see the filter example in
[logging-commons.md](./logging-commons.md)).

## Usage Examples

```java
import io.microsphere.logging.Logging;
import static io.microsphere.logging.LoggingUtils.load;

// When this is the only adapter on the classpath:
Logging logging = load();  // -> JavaLogging

// All logger names currently registered in the JUL LogManager
List<String> names = logging.getLoggerNames();

// Read a level: the level name, or "" when the Level is null (inherited)
// An unknown logger name throws IllegalArgumentException("Logger … does not exist")
String level = logging.getLoggerLevel("io.microsphere");

// Set a level by name — use JUL names (FINE, not DEBUG)
logging.setLoggerLevel("io.microsphere", "FINE");

// Restore inheritance — JUL-specific semantics: the Level is set to null
logging.setLoggerLevel("io.microsphere", null);

// Parent lookup is delegated to the JDK MXBean ("" for the root logger)
String parent = logging.getParentLoggerName("io.microsphere.logging"); // "io.microsphere"
```

JMX support comes from `microsphere-logging-commons` and works out of the box with this adapter:

```java
LoggingMXBeanRegistrar.registerAll();
// MBean ObjectName: io.microsphere.logging:type=Java Logging
```

## Notes

- JUL level names differ from other backends: there is no `TRACE`/`DEBUG` in the Logback sense — `FINEST` maps to
  trace and `FINE` maps to debug.
- `setLoggerLevel` throws `IllegalArgumentException` when the logger is not registered in the `LogManager`, or the
  level name is not a valid JUL level. This is the **only** adapter that honours that part of the `Logging`
  javadoc — the Logback / Log4j / Log4j2 adapters parse through `Level.toLevel(…)` and fall back to DEBUG.
- `getLoggerNames()` only shows loggers that exist in the `LogManager` at call time; JUL creates them lazily on
  first `Logger.getLogger(...)`.
- `getParentLoggerName` is the one method `JavaLogging` overrides; every other adapter inherits the lexical
  interface default, which returns `null` (not `""`) for the root logger name.
- Because `getName()` is `"Java Logging"`, the registered `ObjectName` contains a space —
  `io.microsphere.logging:type=Java Logging`. Quote it in JMX queries if your client is strict.
- Full comparison: [Level semantics across backends](./overview.md#level-semantics-across-backends).
