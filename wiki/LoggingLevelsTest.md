# LoggingLevelsTest (JUnit 5)

## Overview

`@LoggingLevelsTest` is a JUnit 5 annotation that enables a test method to be executed repeatedly — once for each specified logging level. It automatically adjusts the logging configuration before each invocation and restores it afterward.

## Details

| Property | Value |
|----------|-------|
| **Full Qualified Name** | `io.microsphere.logging.test.jupiter.LoggingLevelsTest` |
| **Type** | Annotation |
| **Module** | `microsphere-logging-test` |
| **Package** | `io.microsphere.logging.test.jupiter` |
| **Target** | `METHOD`, `ANNOTATION_TYPE` |
| **Retention** | `RUNTIME` |
| **Since** | 1.0.0 |
| **Author** | [Mercy](mailto:mercyblitz@gmail.com) |

## Explanation

This annotation leverages JUnit 5's `@TestTemplate` mechanism to repeat a test method for each logging level specified in the `levels` attribute. It is powered by the `LoggingLevelsTestExtension`.

### How It Works

1. Annotate a test method with `@LoggingLevelsTest(levels = {"DEBUG", "INFO", "WARN"})`.
2. The extension creates one invocation context per level.
3. Before each invocation, the log level is set for the specified classes.
4. After each invocation, the original log levels are restored.
5. JUnit reports each invocation with a display name like `[DEBUG]`, `[INFO]`, `[WARN]`.

### Annotation Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `loggingClasses` | `Class<?>[]` | `{}` (empty — uses test class package) | Target classes whose loggers will have their levels changed |
| `levels` | `String[]` | *(required)* | The log level names to iterate (e.g., `"DEBUG"`, `"INFO"`, `"WARN"`) |

### Parameter Injection

Test methods can optionally receive:
- **`String level`** — The current log level name
- **`int index`** — The invocation index (0-based)

### Related Components

| Component | Role |
|-----------|------|
| `LoggingLevelsTestExtension` | Concrete extension for `@LoggingLevelsTest` |
| `LoggingLevelsExtension` | Abstract base extension providing invocation context generation |
| `LoggingLevelTemplateInvocationContext` | Creates invocation context with display name and callbacks |
| `LoggingLevelCallback` | `BeforeEachCallback` / `AfterEachCallback` that sets/restores levels |
| `LoggingLevelParameterResolver` | Resolves `String level` and `int index` parameters |
| `LoggingLevelsAttributes` | Data class for the annotation's attributes |

## Example Code

### Basic Usage

```java
import io.microsphere.logging.test.jupiter.LoggingLevelsTest;
import org.junit.jupiter.api.Assertions;

class MyServiceTest {

    @LoggingLevelsTest(levels = {"DEBUG", "INFO", "WARN", "ERROR"})
    void testWithDifferentLogLevels() {
        // This method runs 4 times, once per logging level
        // The logging level is automatically adjusted before each run
        MyService service = new MyService();
        service.doWork();
        // Assertions here...
    }
}
```

### With Target Classes

```java
import io.microsphere.logging.test.jupiter.LoggingLevelsTest;

class MyServiceTest {

    @LoggingLevelsTest(
        loggingClasses = {MyService.class, MyRepository.class},
        levels = {"DEBUG", "INFO", "WARN"}
    )
    void testWithSpecificLoggerTargets() {
        // The loggers for MyService and MyRepository
        // will have their levels changed
        MyService service = new MyService();
        service.doWork();
    }
}
```

### With Parameter Injection

```java
import io.microsphere.logging.test.jupiter.LoggingLevelsTest;

class MyServiceTest {

    @LoggingLevelsTest(levels = {"DEBUG", "INFO", "WARN"})
    void testWithLevelParameter(String level, int index) {
        System.out.printf("Running test #%d with level: %s%n", index, level);
        // index=0, level="DEBUG"
        // index=1, level="INFO"
        // index=2, level="WARN"
    }
}
```

### Test Output

When running the test, JUnit 5 will display:
```
MyServiceTest
  ├── testWithDifferentLogLevels [DEBUG] ✓
  ├── testWithDifferentLogLevels [INFO] ✓
  ├── testWithDifferentLogLevels [WARN] ✓
  └── testWithDifferentLogLevels [ERROR] ✓
```

## Version Compatibility

| Version | Java | JUnit 5 Version | Notes |
|---------|------|-----------------|-------|
| 1.0.0+ | 11+ | 6.0.3 (Jupiter) | Default profile |
| 1.0.0+ | 8–10 | 5.14.3 (Jupiter) | Java 8 profile |

### Maven Dependency

```xml
<dependency>
    <groupId>io.github.microsphere-projects</groupId>
    <artifactId>microsphere-logging-test</artifactId>
    <version>0.1.1-SNAPSHOT</version>
    <scope>test</scope>
</dependency>
```

## Since Version

`@since 1.0.0`
