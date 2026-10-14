# microsphere-logging-examples

[中文版](./zh/examples.md)

> Runnable example module demonstrating the unified `Logging` API with all backends present.

---

## Version Information

| Item | Value |
|------|-------|
| Branch analyzed | `release` (document set `current`) |
| Artifact | `io.github.microsphere-projects:microsphere-logging-examples` |
| Version on `release` branch | `0.2.3-SNAPSHOT` |
| Latest published version | `0.2.2` |
| Introduced since | `1.0.0` (0.1.x line) |

## Compatibility

| Requirement | Version |
|-------------|---------|
| Java | 8+ |

The module depends on **all** backend adapters (`microsphere-java-logging`, `microsphere-logback`,
`microsphere-log4j`, `microsphere-log4j2`) plus `microsphere-java-core`, and imports the
`microsphere-logging-dependencies` BOM — so it also demonstrates multi-backend coexistence and priority-based
resolution.

## Running

```bash
# From the repository root (Windows: use mvnw.cmd)
./mvnw -pl microsphere-logging-examples -am package

# Build the runtime classpath, then launch
CP=microsphere-logging-examples/target/classes:$(./mvnw -q -pl microsphere-logging-examples \
  dependency:build-classpath -Dmdep.outputFile=/dev/stdout)
java -cp "$CP" io.microsphere.logging.examples.LoggingExample
```

The simplest path is running `LoggingExample.main(...)` from your IDE.

## What It Demonstrates

`io.microsphere.logging.examples.LoggingExample`:

```java
public class LoggingExample {

    private static final Logger logger = getLogger(LoggingExample.class);

    public static void main(String[] args) {
        // The highest-priority Logging instance will be loaded
        Logging logging = load();
        logger.info("The name of Logging : {}", logging.getName());

        // All logger names known to that Logging backend
        logger.info("All logger names of Logging : {}", logging.getLoggerNames());
    }
}
```

Key take-aways:

1. `LoggingUtils.load()` picks one `Logging` implementation by `Prioritized` order, ascending by value. With all
   four adapters present, **`Log4j2Logging` wins** (priority `-5`; `MAX_PRIORITY` is `Integer.MIN_VALUE`) — see
   [logging-commons.md](./logging-commons.md#backend-priority-summary). The example therefore prints
   `The name of Logging : Log4j2`.
2. `LoggerFactory.getLogger(...)` (from `microsphere-java-core`, resolved through the `LoggerFactory` SPIs that the
   Log4j and Log4j2 adapters register) gives a framework-agnostic `io.microsphere.logging.Logger`. The same priority
   values apply, so log statements in this example also go to Log4j2.
3. The printed backend name and live logger inventory are the starting point for runtime level management and JMX
   registration.

## Extending the Examples

Variations worth trying in your own `main`:

```java
// Inspect every detected backend, not only the winner
LoggingUtils.loadAll().forEach(l ->
        System.out.printf("%s (priority %d): %d loggers, levels %s%n",
                l.getName(), l.getPriority(), l.getLoggerNames().size(), l.getSupportedLoggingLevels()));

// Flip a level on all backends and watch the output verbosity change
LoggingUtils.loadAll().forEach(l -> l.setLoggerLevel("io.microsphere", "DEBUG"));

// Expose everything over JMX, then attach JConsole
io.microsphere.logging.jmx.LoggingMXBeanRegistrar.registerAll();
```

> When several backends are on the classpath, a level name valid for one may be invalid for another
> (`DEBUG` is not a JUL name). Wrap per-backend calls or filter `loadAll()` by `getName()` in that case.
