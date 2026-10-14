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

`Log4jLoggerFactory.getPriority()` deliberately returns `Log4jLogging.PRIORITY` (`-3`), so both SPI lists are
ordered by the same value and the `Logging`/`LoggerFactory` pair for a backend stay consistent. `microsphere-java-core`
resolves `LoggerFactory` once in a static field, so the winner is fixed for the JVM's lifetime — with Log4j2 also on
the classpath, Log4j2 (`-5`) takes logging calls instead.

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

LoggerRepository repo = Log4jUtils.getLoggerRepository();   // LogManager.getLoggerRepository(), cached in a static field

Logger logger = Log4jUtils.getLogger("io.microsphere");   // or getLogger(MyClass.class)

Level level = Log4jUtils.getLevel("io.microsphere");        // declared level, else effective level
String levelString = Log4jUtils.getLevelString("io.microsphere"); // e.g. "INFO"

Log4jUtils.setLoggerLevel("io.microsphere", "DEBUG");  // by name
Log4jUtils.setLoggerLevel(logger, Level.DEBUG);        // by Logger + Level
Log4jUtils.setLoggerLevel(logger, (String) null);      // Level.toLevel(null) -> DEBUG, NOT inheritance
Log4jUtils.setLoggerLevel(logger, (Level) null);       // the real "inherit from parent" call
```

`ROOT_LOGGER_NAME` is read from the repository at class-initialisation time (`Log4jUtils.java:44`), which is why
Log4j's root is reported as `"root"` — the value Log4j 1.x itself gives the root logger.

## Usage Examples (management API)

```java
Logging logging = LoggingUtils.load();      // Log4jLogging only when Log4j2 is absent (-3 vs -5)

logging.getLoggerNames();                   // all loggers currently in the repository
logging.getLoggerLevel("io.microsphere");   // e.g. "INFO"
logging.setLoggerLevel("io.microsphere", "TRACE");
logging.setLoggerLevel("io.microsphere", null);   // WARNING: Level.toLevel(null) -> DEBUG, not "inherit"
```

Parent names follow the dot hierarchy via the interface default method, e.g.
`getParentLoggerName("io.microsphere.logging")` → `"io.microsphere"`, `getParentLoggerName("root")` → `null`.

## Notes

- `FATAL` exists in Log4j 1.x (and Log4j2) but not in Logback — avoid it in code that must be portable across
  backends.
- Invalid or `null` level names do **not** throw: `Level.toLevel(...)` defaults them to `DEBUG`. An unknown logger
  name is also accepted, because Log4j 1.x creates the logger on lookup.
- `getLoggerNames()` drains `LoggerRepository.getCurrentLoggers()` (an `Enumeration`) at call time; Log4j 1.x
  creates loggers on first lookup, so freshly-created loggers may be missing until something requests them.
- Log4j 1.x `Level` exposes `public static final int TRACE_INT` and siblings; the resolver's type filter rejects
  them, which is why `getSupportedLoggingLevels()` returns exactly the eight `Level`-typed constants.
