# microsphere-logback

[中文版](./zh/logback.md)

> Adapter module for **Logback** (built on `logback-classic` / SLF4J).

---

## Version Information

| Item | Value |
|------|-------|
| Branch analyzed | `release` (document set `current`) |
| Artifact | `io.github.microsphere-projects:microsphere-logback` |
| Version on `release` branch | `0.2.3-SNAPSHOT` |
| Latest published version | `0.2.2` |
| Introduced since | `1.0.0` (0.1.x line) |

## Compatibility

| Requirement | Version |
|-------------|---------|
| Java | 8+ |
| Logback (`logback-classic`) | 1.2+ |
| SLF4J API | 1.7+ |
| microsphere-logging-commons | same version line (managed by the BOM) |

## Installation

```xml
<dependency>
    <groupId>io.github.microsphere-projects</groupId>
    <artifactId>microsphere-logback</artifactId>
</dependency>
```

SPI registration: `META-INF/services/io.microsphere.logging.Logging` → `LogbackLogging`.

## Key Class: `io.microsphere.logging.logback.LogbackLogging`

Implements `Logging` on top of Logback's `LoggerContext`.

| Constant / Behavior | Value |
|---------------------|-------|
| Root logger name | `"ROOT"` (`org.slf4j.Logger.ROOT_LOGGER_NAME`) |
| `PRIORITY` | `NORMAL_PRIORITY` (`0`) — third of the four shipped adapters; Log4j (`-3`) and Log4j2 (`-5`) outrank it |
| `ALL_LEVELS` | resolved from `ch.qos.logback.classic.Level`: `OFF`, `ERROR`, `WARN`, `INFO`, `DEBUG`, `TRACE`, `ALL` |
| `getName()` | `"Logback"` |

`LogbackUtils` holds the context in a static field:

```java
// LogbackUtils.java:38
static final LoggerContext loggerContext = (LoggerContext) getILoggerFactory();
```

The cast is unchecked, so if SLF4J is bound to a different provider (Log4j2, JUL, reload4j) on your classpath,
touching `LogbackUtils` raises `ClassCastException` inside a static initialiser — which surfaces as
`ExceptionInInitializerError` / `NoClassDefFoundError` on the next access. Depend on this module only when
`logback-classic` is the actual SLF4J binding.

This module registers **only** the `Logging` service — it does **not** register a `LoggerFactory` SPI. Your logging
calls keep flowing through SLF4J/Logback exactly as before; this module contributes the *management* capability
(level query/change, logger listing, JMX).

## Usage Examples

Through the framework-agnostic `Logging` API:

```java
import io.microsphere.logging.Logging;
import static io.microsphere.logging.LoggingUtils.load;

Logging logging = load();                        // LogbackLogging only when no better-prioritized adapter is present
logging.setLoggerLevel("io.microsphere", "DEBUG");  // effective immediately
logging.setLoggerLevel("io.microsphere", null);     // WARNING: parsed by Level.toLevel -> DEBUG, not inheritance
```

Direct Logback manipulation through `LogbackUtils`:

```java
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import io.microsphere.logging.logback.util.LogbackUtils;

LoggerContext context = LogbackUtils.getLoggerContext();

Logger byClass = LogbackUtils.getLogger(MyService.class);
Logger byName = LogbackUtils.getLogger("io.microsphere");   // created on demand

Level level = LogbackUtils.getLevel("io.microsphere");              // declared level, else effective level
String levelString = LogbackUtils.getLevelString("io.microsphere"); // e.g. "DEBUG"

LogbackUtils.setLoggerLevel("io.microsphere", "TRACE");  // by name + level name (null/unknown -> DEBUG)
LogbackUtils.setLoggerLevel(byName, Level.DEBUG);        // by Logger + Level object
LogbackUtils.setLoggerLevel(byName, (Level) null);       // the real "inherit" escape hatch
```

`LogbackUtils.setLoggerLevel(Logger, Level)` calls `logger.setLevel(...)` directly, so a `null` **`Level`**
restores inheritance — unlike the `String` overloads, which route through `Level.toLevel(...)` and therefore
default to `DEBUG`.

Null-argument behaviour, as pinned by `LogbackUtilsTest`:

```java
// test-case evidence — LogbackUtilsTest.java:56-57,62,80
assertThrows(NullPointerException.class, () -> getLogger((Class<?>) null));
assertThrows(IllegalArgumentException.class, () -> getLogger((String) null)); // Logback: "name argument cannot be null"
assertNull(getLevel((Logger) null));                       // null logger -> null level, no exception
setLoggerLevel((Logger) null, "DEBUG");                    // null logger -> silent no-op
```

JMX exposure (provided by commons):

```java
LoggingMXBeanRegistrar.registerAll();
// MBean ObjectName: io.microsphere.logging:type=Logback
```

## Notes

- The resolved level set is `OFF`, `ERROR`, `WARN`, `INFO`, `DEBUG`, `TRACE`, `ALL`. Logback's rarely-used
  `FINE`/`FINER`/`FINEST` aliases are not exposed by `getSupportedLoggingLevels()` because the resolver reads
  `public static final Level` fields only.
- `setLoggerLevel(loggerName, …)` does **not** throw for an unknown logger name — Logback creates the logger on
  demand — and an invalid or `null` level name silently becomes `DEBUG` via `Level.toLevel(...)`.
- `getLoggerNames()` reflects the `LoggerContext` inventory at call time (name-ascending); Logback creates loggers
  lazily on first `getLogger(...)`. Note that `getLoggerList()` does **not** include the root logger, so
  `"ROOT"` appears in `getRootLoggerName()` but never in `getLoggerNames()`.
- `getParentLoggerName` is inherited from the `Logging` default (pure lexical), so it returns `"ROOT"` for a
  dotless name and `null` for the root itself — see
  [overview comparison](./overview.md#level-semantics-across-backends).
- Prefer uppercase level names for portability when the same management code may run against several backends.
