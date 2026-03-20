# LoggingLevelsClass (JUnit 5)

## Overview

`@LoggingLevelsClass` is a JUnit 5 annotation that enables an entire test class to be executed repeatedly — once for each specified logging level. It applies the logging level configuration to all test methods in the class.

## Details

| Property | Value |
|----------|-------|
| **Full Qualified Name** | `io.microsphere.logging.test.jupiter.LoggingLevelsClass` |
| **Type** | Annotation |
| **Module** | `microsphere-logging-test` |
| **Package** | `io.microsphere.logging.test.jupiter` |
| **Target** | `TYPE`, `ANNOTATION_TYPE` |
| **Retention** | `RUNTIME` |
| **Since** | 1.0.0 |
| **Author** | [Mercy](mailto:mercyblitz@gmail.com) |

## Explanation

This annotation leverages JUnit 5's `@ClassTemplate` mechanism to repeat an entire test class execution for each logging level. Unlike `@LoggingLevelsTest` which repeats individual methods, this annotation repeats the entire class lifecycle (including `@BeforeAll`, `@BeforeEach`, all test methods, `@AfterEach`, `@AfterAll`).

### How It Works

1. Annotate a test class with `@LoggingLevelsClass(levels = {"DEBUG", "INFO", "WARN"})`.
2. The extension creates one class invocation per level.
3. For each invocation, the entire test class is executed with the specified logging level.
4. After each class invocation, original log levels are restored.

### Annotation Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `loggingClasses` | `Class<?>[]` | `{}` (empty — uses test class package) | Target classes whose loggers will have their levels changed |
| `levels` | `String[]` | *(required)* | The log level names to iterate (e.g., `"DEBUG"`, `"INFO"`, `"WARN"`) |

### Related Components

| Component | Role |
|-----------|------|
| `LoggingLevelsClassExtension` | Concrete extension for `@LoggingLevelsClass` |
| `LoggingLevelsExtension` | Abstract base extension providing invocation context generation |
| `LoggingLevelTemplateInvocationContext` | Creates invocation context with display name and callbacks |
| `LoggingLevelCallback` | Sets/restores logging levels |

## Example Code

### Basic Usage

```java
import io.microsphere.logging.test.jupiter.LoggingLevelsClass;
import org.junit.jupiter.api.Test;

@LoggingLevelsClass(levels = {"DEBUG", "INFO", "WARN"})
class MyServiceIntegrationTest {

    @Test
    void testServiceOperation() {
        // This entire class runs 3 times (once per level)
        // All test methods in this class execute under the same log level
        MyService service = new MyService();
        service.doWork();
    }

    @Test
    void testAnotherOperation() {
        // Also runs 3 times, under each log level
        MyService service = new MyService();
        service.doOtherWork();
    }
}
```

### With Target Classes

```java
import io.microsphere.logging.test.jupiter.LoggingLevelsClass;
import org.junit.jupiter.api.Test;

@LoggingLevelsClass(
    loggingClasses = {MyService.class, MyRepository.class},
    levels = {"DEBUG", "INFO", "ERROR"}
)
class FullStackLoggingTest {

    @Test
    void testEndToEnd() {
        // The loggers for MyService and MyRepository are set
        // to DEBUG, then INFO, then ERROR across full class runs
    }
}
```

### Test Output

When running the test, JUnit 5 will display:
```
MyServiceIntegrationTest [DEBUG]
  ├── testServiceOperation ✓
  └── testAnotherOperation ✓
MyServiceIntegrationTest [INFO]
  ├── testServiceOperation ✓
  └── testAnotherOperation ✓
MyServiceIntegrationTest [WARN]
  ├── testServiceOperation ✓
  └── testAnotherOperation ✓
```

## Version Compatibility

| Version | Java | JUnit 5 Version | Notes |
|---------|------|-----------------|-------|
| 1.0.0+ | 11+ | 6.0.3 (Jupiter) | Default profile; uses `@ClassTemplate` |
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
