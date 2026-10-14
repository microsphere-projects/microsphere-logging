# microsphere-log4j

[中文版](./zh/log4j.md)

> Adapter module for **Log4j 1.x** (`log4j:log4j`).

---

## Version Information

| Item | Value |
|------|-------|
| Branch analyzed | `release` (document set `current`) |
| Artifact | `io.github.microsphere-projects:microsphere-log4j` |
| Version on `release` branch | `0.2.3-SNAPSHOT` |
| Latest published version | `0.2.2` |
| Introduced since | `1.0.0` (0.1.x line) |

## Compatibility

| Requirement | Version |
|-------------|---------|
| Java | 8+ |
| Log4j | 1.2+ |
| microsphere-logging-commons | same version line (managed by the BOM) |

> Log4j 1.x is end-of-life upstream. This adapter exists for legacy applications; new projects should prefer
> `microsphere-log4j2` or `microsphere-logback`.

## Installation

```xml
<dependency>
    <groupId>io.github.microsphere-projects</groupId>
    <artifactId>microsphere-log4j</artifactId>
</dependency>
```

## SPI Registrations

This module registers **two** services:

| Service file | Implementation | Effect |
|--------------|----------------|--------|
| `META-INF/services/io.microsphere.logging.Logging` | `Log4jLogging` | runtime management of Log4j loggers |
| `META-INF/services/io.microsphere.logging.LoggerFactory` | `Log4jLoggerFactory` | `LoggerFactory.getLogger(...)` yields Log4j-backed `Logger` instances |

## Key Classes

### `io.microsphere.logging.log4j.Log4jLogging`

| Constant / Behavior | Value |
|---------------------|-------|
| Root logger name | `Log4jUtils.ROOT_LOGGER_NAME` — the Log4j 1.x root logger, `"root"` |
| `PRIORITY` | `NORMAL_PRIORITY - 3` (i.e. `-3`) |
| `ALL_LEVELS` | resolved from `org.apache.log4j.Level`: `OFF`, `FATAL`, `ERROR`, `WARN`, `INFO`, `DEBUG`, `TRACE`, `ALL` |
| `getName()` | `"Log4j"` |

### `io.microsphere.logging.log4j.Log4jLogger` / `Log4jLoggerFactory`

Bridge Log4j 1.x `org.apache.log4j.Logger` into the `io.microsphere.logging.Logger` / `LoggerFactory` facade from
`microsphere-java-core`, so application logging and management code share one API:

```java
import io.microsphere.logging.Logger;
import io.microsphere.logging.LoggerFactory;

Logger logger = LoggerFactory.getLogger(MyClass.class); // Log4jLogger on this classpath
logger.info("Logging through Log4j 1.x: {}", value);
```

### `io.microsphere.logging.log4j.util.Log4jUtils`

```java
import io.microsphere.logging.log4j.util.Log4jUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerRepository;

LoggerRepository repo = Log4jUtils.getLoggerRepository();

Logger logger = Log4jUtils.getLogger("io.microsphere");   // or getLogger(MyClass.class)

Level level = Log4jUtils.getLevel("io.microsphere");
String levelString = Log4jUtils.getLevelString("io.microsphere"); // e.g. "INFO"

Log4jUtils.setLoggerLevel("io.microsphere", "DEBUG");  // by name
Log4jUtils.setLoggerLevel(logger, Level.DEBUG);        // by Logger + Level
Log4jUtils.setLoggerLevel(logger, null);               // null => inherit from parent
```

## Usage Examples (management API)

```java
Logging logging = LoggingUtils.load();      // -> Log4jLogging when it wins priority

logging.getLoggerNames();                   // all loggers currently in the repository
logging.getLoggerLevel("io.microsphere");   // e.g. "INFO"
logging.setLoggerLevel("io.microsphere", "TRACE");
logging.setLoggerLevel("io.microsphere", null);   // back to inherited level
```

Parent names follow the dot hierarchy via the interface default method, e.g.
`getParentLoggerName("io.microsphere.logging")` → `"io.microsphere"`.

## Notes

- `FATAL` exists in Log4j 1.x (and Log4j2) but not in Logback — avoid it in code that must be portable across
  backends.
- Invalid level names (e.g. `"VERBOSE"`) cause `IllegalArgumentException`, as does an unknown logger name.
- `getLoggerNames()` reflects the `LoggerRepository` contents; Log4j 1.x creates loggers on first lookup.
