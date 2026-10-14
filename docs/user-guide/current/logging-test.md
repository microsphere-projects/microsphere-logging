# microsphere-logging-test

[中文版](./zh/logging-test.md)

> Test-support module: run the same test across multiple logging levels with JUnit 4 rules and JUnit Jupiter 5
> extensions.

---

## Version Information

| Item | Value |
|------|-------|
| Branch analyzed | `release` (document set `current`) |
| Artifact | `io.github.microsphere-projects:microsphere-logging-test` |
| Version on `release` branch | `0.2.3-SNAPSHOT` |
| Latest published version | `0.2.2` |
| Introduced since | `1.0.0` (0.1.x line) |

## Compatibility

| Requirement | Version |
|-------------|---------|
| Java | 8+ |
| JUnit 4 | 4+ (optional — only needed for the `io.microsphere.logging.test.junit4` package) |
| JUnit Jupiter | 5.13+ |
| microsphere-logging-commons | same version line (managed by the BOM) |

At least **one** backend adapter module (logback / log4j / log4j2 / java-logging) must be on the test classpath: the
extensions resolve levels through `LoggingUtils.loadAll()`, so they act on **every** detected backend at once.

## Installation

```xml
<dependency>
    <groupId>io.github.microsphere-projects</groupId>
    <artifactId>microsphere-logging-test</artifactId>
    <scope>test</scope>
</dependency>
```

## How It Works

1. The extension/rule loads all `Logging` implementations visible to the class loader.
2. The target **logger names** are resolved from the annotation attributes:
   - `loggingClasses` non-empty → the fully-qualified names of those classes;
   - `loggingClasses` empty → the **package name of the test class** (so the whole package is switched).
3. One invocation context is created **per entry in `levels`**.
4. Before each invocation the current level of every target logger is recorded on every backend, then the requested
   level is applied; after the invocation the original levels are restored.

## JUnit Jupiter 5

### `@LoggingLevelsTest` — per-method level matrix

`@Target({METHOD, ANNOTATION_TYPE})`, applied together with `@Test`; it auto-registers
`LoggingLevelsTestExtension` via `@ExtendWith`.

```java
import io.microsphere.logging.test.jupiter.LoggingLevelsTest;
import org.junit.jupiter.api.Test;

class MyServiceTest {

    @Test
    @LoggingLevelsTest(loggingClasses = {MyService.class}, levels = {"TRACE", "DEBUG", "INFO"})
    void testBehaviorAcrossLogLevels(String level, int index) {
        // Runs three times: ("TRACE", 0), ("DEBUG", 1), ("INFO", 2)
        assertEquals(new String[]{"TRACE", "DEBUG", "INFO"}[index], level);
    }

    @Test
    @LoggingLevelsTest(levels = {"DEBUG"})   // loggingClasses omitted -> the test class' package is used
    void onlyNeedsDebug(String level) {
        // ...
    }
}
```

Attributes:

| Attribute | Type | Default | Meaning |
|-----------|------|---------|---------|
| `loggingClasses` | `Class<?>[]` | `{}` | classes whose loggers should be switched; empty means the test class package |
| `levels` | `String[]` | required | level names to iterate; applied to every loaded backend |

### `@LoggingLevelsClass` — whole class per level

`@Target({TYPE, ANNOTATION_TYPE})`; registers `LoggingLevelsClassExtension`, which turns each test method of the
annotated class into a per-level repetition. It is designed to be **composed** into your own class-level annotation:

```java
@LoggingLevelsClass(loggingClasses = {MyService.class}, levels = {"DEBUG", "INFO"})
@Retention(RUNTIME)
@Target(TYPE)
public @interface DefaultLoggingLevelsClass {
}
```

```java
@DefaultLoggingLevelsClass          // meta-annotation carrying the level matrix
class MyServiceIT {

    @Test
    void a(String level, int index) {   // runs under DEBUG, then INFO
        // ...
    }
}
```

### Parameter injection

`LoggingLevelParameterResolver` resolves `String` parameters to the active level name and `int` parameters to the
level index, in any parameter order. Only those two types are supported.

Internals you may see in stack traces: `LoggingLevelsExtension` (abstract base), `LoggingLevelsTestExtension`,
`LoggingLevelsClassExtension`, `LoggingLevelCallback` (set + restore), `LoggingLevelTemplateInvocationContext`,
`LoggingLevelsAttributes`.

## JUnit 4

`LoggingLevelsRule` implements `TestRule` / `@ClassRule`:

```java
import io.microsphere.logging.test.junit4.LoggingLevelsRule;
import org.junit.ClassRule;
import org.junit.Test;

public class MyServiceTest {

    @ClassRule
    public static final LoggingLevelsRule loggingLevelsRule =
            LoggingLevelsRule.levels("TRACE", "DEBUG", "INFO");

    @Test
    public void testBehaviorAcrossLogLevels() {
        // Executed for each level declared in the rule
    }
}
```

The rule wraps the test statement with `LoggingLevelsStatement`, which loads all `Logging` implementations for the
test class' class loader and applies/restores the levels around each run.

## Notes

- Level names must be valid for **every** backend on the test classpath, because the extension applies each level to
  all loaded backends. The four backends only share `INFO` and `OFF` by name (JUL uses `SEVERE`/`WARNING`/`FINE`,
  Logback has no `FATAL`, Log4j/Log4j2 do). Safest practice: keep a **single** backend module on the test classpath,
  or limit `levels` to names that backend supports.
- Levels are restored after every invocation, so tests stay order-independent and do not leak into each other.
- Capturing output while iterating levels pairs well with `InMemoryAppender` from
  [microsphere-log4j2](./log4j2.md).
