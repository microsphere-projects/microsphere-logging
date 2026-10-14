# microsphere-log4j2

[中文版](./zh/log4j2.md)

> Adapter module for **Apache Log4j 2** (`log4j-api` / `log4j-core`), including appender and layout utilities.

---

## Version Information

| Item | Value |
|------|-------|
| Branch analyzed | `release` (document set `current`) |
| Artifact | `io.github.microsphere-projects:microsphere-log4j2` |
| Version on `release` branch | `0.2.3-SNAPSHOT` |
| Latest published version | `0.2.2` |
| Introduced since | `1.0.0` (0.1.x line) |

## Compatibility

| Requirement | Version |
|-------------|---------|
| Java | 8+ |
| Log4j2 (`log4j-api` + `log4j-core`) | 2.4+ |
| microsphere-logging-commons | same version line (managed by the BOM) |

## Installation

```xml
<dependency>
    <groupId>io.github.microsphere-projects</groupId>
    <artifactId>microsphere-log4j2</artifactId>
</dependency>
```

## SPI Registrations

| Service file | Implementation | Effect |
|--------------|----------------|--------|
| `META-INF/services/io.microsphere.logging.Logging` | `Log4j2Logging` | runtime management of Log4j2 loggers |
| `META-INF/services/io.microsphere.logging.LoggerFactory` | `Log4j2LoggerFactory` | `LoggerFactory.getLogger(...)` yields Log4j2-backed `Logger` instances |

## Key Classes

### `io.microsphere.logging.log4j2.Log4j2Logging`

| Constant / Behavior | Value |
|---------------------|-------|
| Root logger name | `""` (`org.apache.logging.log4j.LogManager.ROOT_LOGGER_NAME`) |
| `PRIORITY` | `NORMAL_PRIORITY - 5` (i.e. `-5`) — the **smallest** value among the shipped adapters, therefore the **highest** priority and the backend `load()` returns |
| `ALL_LEVELS` | resolved from `org.apache.logging.log4j.Level`: `OFF`, `FATAL`, `ERROR`, `WARN`, `INFO`, `DEBUG`, `TRACE`, `ALL` |
| `getName()` | `"Log4j2"` |

### `io.microsphere.logging.log4j2.Log4j2Logger` / `Log4j2LoggerFactory`

Bridge `org.apache.logging.log4j.Logger` into the unified `io.microsphere.logging.Logger` facade:

```java
import io.microsphere.logging.Logger;
import io.microsphere.logging.LoggerFactory;

Logger logger = LoggerFactory.getLogger(MyClass.class); // Log4j2Logger on this classpath
logger.warn("Value: {}", value);

// Unwrap when a Log4j2-specific API is needed
org.apache.logging.log4j.Logger delegate = (org.apache.logging.log4j.Logger) ((DelegatingWrapper) logger).getDelegate();
```

`Log4j2Logger` is package-private and holds the Log4j2 logger created by `Log4j2Utils.getLogger(name)`; every
`is*Enabled` check and every `trace/debug/info/warn/error` method (message and message-plus-`Throwable` forms) is a
direct pass-through — there is no buffering, formatting or level logic in the adapter itself. Obtain it through
`LoggerFactory`, not by constructing it.

`Log4j2LoggerFactory.getPriority()` returns `Log4j2Logging.PRIORITY` (`-5`), so the `Logging` and the
`LoggerFactory` SPI lists agree: Log4j2 wins both whenever it is present.

### `io.microsphere.logging.log4j2.util.Log4j2Utils`

Level management:

```java
import io.microsphere.logging.log4j2.util.Log4j2Utils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Configuration;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;

Configuration config = Log4j2Utils.getConfiguration();
LoggerContext context = Log4j2Utils.getLoggerContext();     // (LoggerContext) LogManager.getContext(false)
Collection<Logger> loggers = Log4j2Utils.getLoggers();      // context.getLoggers()

Logger logger = Log4j2Utils.getLogger("io.microsphere");   // or getLogger(MyClass.class)

Level level = Log4j2Utils.getLevel("io.microsphere");       // declared level — may be null when inherited
String levelString = Log4j2Utils.getLevelString("io.microsphere");

Log4j2Utils.setLoggerLevel("io.microsphere", "DEBUG");   // by name (null/unknown -> DEBUG via Level.toLevel)
Log4j2Utils.setLoggerLevel(logger, Level.TRACE);         // by Logger + Level
Log4j2Utils.setLoggerLevel(logger, (Level) null);        // logger.setLevel(null) -> inherit from the parent config
```

`Log4j2Utils.getLevel` reads `logger.getLevel()` without falling back to the effective level, so it can return
`null` for a logger that inherits. Treat `null` as "no level declared on this logger" rather than an error.

Appender manipulation:

```java
// Apply a consumer to every configured logger
Log4j2Utils.doInLogger(l -> l.setAdditivity(true));

// Attach / detach appenders (runtime only — nothing is written back to log4j2.xml)
Log4j2Utils.addAppender(myAppender, Log4j2Utils.getLoggers());
Log4j2Utils.addAppenderForAllLoggers(myAppender);
Log4j2Utils.removeAppender(myAppender, loggers);
Log4j2Utils.removeAppenderForAllLoggers(myAppender);

// Look up a configured appender by name
ConsoleAppender console = Log4j2Utils.findAppender("Console");
```

`addAppender` / `removeAppender` synchronise on the `LoggerContext`, start or stop the appender if needed, mutate
the `Configuration`, attach or detach the appender on each `LoggerConfig`, and finish with
`loggerContext.updateLoggers()`. The change is therefore **immediate and in-memory**; a configuration reload
(`Configurator.initialize(...)` / reconfiguration check) restores whatever the config file declares.

### `io.microsphere.logging.log4j2.appender.InMemoryAppender`

An `Appender` (name `"InMemory"`, extends `AbstractLifeCycle`) that buffers `LogEvent`s in a
`ConcurrentSkipListSet` ordered by `LogEventComparator`, so captured events stay chronologically sorted without
manual sorting.

```java
InMemoryAppender appender = new InMemoryAppender();
appender.initialize();
appender.start();                       // lifecycle: required before recording

Log4j2Utils.addAppenderForAllLoggers(appender);
MyService.doSomething();                // code under test logs

// The buffer has no public read accessor; drain it into another appender to inspect,
// e.g. Log4j2's own ListAppender, then assert on that list.
appender.transfer(listAppender);        // destructive: events are pollFirst()-ed out of the buffer

appender.stop();                        // stop() clears the buffered events

// Find the appender already declared in the Log4j2 configuration (may return null)
InMemoryAppender configured = InMemoryAppender.findInMemoryAppender();
```

```java
// test-case evidence — InMemoryAppenderTest.java:95-100
// findInMemoryAppender() returns null unless the configuration declares an appender named "InMemory"
```

### Layouts and comparator

- `DelegatingLayout<T extends Serializable>` — a mutable holder around a `Layout` whose every method
  (`getFooter`, `getHeader`, `toByteArray`, `toSerializable`, `getContentType`, `getContentFormat`, `encode`) is a
  pure pass-through to the delegate. Construct with `new DelegatingLayout<>(other)`; the no-arg constructor leaves
  the delegate `null`, so you must call `setDelegate(...)` before the layout is used or it will throw an NPE on
  first format.
- `SmartFileAppenderLayout<T extends Serializable>` — constructed with a `LoggerContext` and eager: it walks
  `context.getLoggers()` once and caches one delegate `Layout` per logger name. For each logger it picks the
  first `RollingFileAppender`, falling back to any `FileAppender`, **skipping the appender named `InMemory`**, and
  recurses to the parent logger when the logger owns no non-in-memory appender; a logger whose chain yields no
  layout gets `DEFAULT_LAYOUT` (`PatternLayout` with pattern `%m`). Formatting a `LogEvent` looks the logger name
  up with `getOrDefault(name, DEFAULT_LAYOUT)`, so loggers created *after* construction are formatted with `%m`.
- `LogEventComparator` (singleton `INSTANCE`) — orders `LogEvent`s by `getInstant().getEpochMillisecond()` with a
  `getNanoOfMillisecond()` tie-break; already used internally by `InMemoryAppender`.

## Notes

- Level changes made through this adapter apply immediately to the running `LoggerContext` and are never persisted
  to the configuration file.
- Log4j2 has the **highest** effective priority (`-5`), so `LoggingUtils.load()` returns `Log4j2Logging` whenever
  this module is on the classpath — including when JUL, Log4j or Logback is also present. Use `loadAll()` and
  filter by `getName()` to reach another backend explicitly.
- `FATAL` is available here and in Log4j 1.x, but not in Logback.
- The `log4j-api` version in force for this branch is `2.9.1` (declared by the BOM); the module compiles against
  Log4j2 core APIs (`LoggerContext`, `LoggerConfig`, `AbstractLifeCycle`) that are stable from 2.4 onward.
