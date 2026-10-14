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
| `PRIORITY` | `NORMAL_PRIORITY + 10` (i.e. `10`) — the **highest** among all built-in backends |
| `ALL_LEVELS` | resolved from `java.util.logging.Level` via `DefaultLoggingLevelsResolver` |
| `getName()` | `"Java Logging"` |
| `getRootLoggerName()` | `""` |

Supported level names, exactly as resolved: `OFF`, `SEVERE`, `WARNING`, `INFO`, `CONFIG`, `FINE`, `FINER`, `FINEST`,
`ALL`. JUL exposes no `TRACE`/`DEBUG` names here — `FINEST` is the trace-like level and `FINE` is the debug-like
level, so level names are **not** interchangeable with the other backends.

Because its priority is the highest, `LoggingUtils.load()` selects `JavaLogging` first whenever this module is on
the classpath, even if Logback or Log4j2 is also present. If you manage JUL while another backend is actually in
use, load all candidates and pick explicitly (see the filter example in
[logging-commons.md](./logging-commons.md)).

## Usage Examples

```java
import io.microsphere.logging.Logging;
import static io.microsphere.logging.LoggingUtils.load;

Logging logging = load();  // -> JavaLogging when this module is dominant

// All logger names currently registered in the JUL LogManager
List<String> names = logging.getLoggerNames();

// Read a level: null for an unknown logger, "" when the level is inherited (null Level)
String level = logging.getLoggerLevel("io.microsphere");

// Set a level by name — use JUL names (FINE, not DEBUG)
logging.setLoggerLevel("io.microsphere", "FINE");

// Restore inheritance (JUL: level set to null)
logging.setLoggerLevel("io.microsphere", null);

// Parent lookup follows the dot hierarchy
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
  level name is not a valid JUL level.
- `getLoggerNames()` only shows loggers that exist in the `LogManager` at call time; JUL creates them lazily on
  first `Logger.getLogger(...)`.
