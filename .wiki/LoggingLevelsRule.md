# LoggingLevelsRule (JUnit 4)

## Overview

`LoggingLevelsRule` is a JUnit 4 `TestRule` that repeats test execution for each specified logging level. It provides the same multi-level testing capability as the JUnit 5 annotations but for projects using JUnit 4.

## Details

| Property | Value |
|----------|-------|
| **Full Qualified Name** | `io.microsphere.logging.test.junit4.LoggingLevelsRule` |
| **Type** | Class |
| **Module** | `microsphere-logging-test` |
| **Package** | `io.microsphere.logging.test.junit4` |
| **Implements** | `org.junit.rules.TestRule` |
| **Since** | 1.0.0 |
| **Author** | [Mercy](mailto:mercyblitz@gmail.com) |

## Explanation

`LoggingLevelsRule` wraps test execution in a `LoggingLevelsStatement` that iterates through each specified logging level. Before each iteration, the logging level is set, and after each iteration, it is restored.

### How It Works

1. Declare the rule as a field annotated with `@Rule`.
2. Use the factory method `LoggingLevelsRule.levels(...)` to specify the levels.
3. When a test method runs, the rule creates a `LoggingLevelsStatement`.
4. The statement evaluates the base test statement once per level.

### Related Components

| Component | Role |
|-----------|------|
| `LoggingLevelsStatement` | Extends `Statement` to repeat execution with different levels |

### Methods

| Method | Return Type | Description |
|--------|-------------|-------------|
| `levels(String...)` | `LoggingLevelsRule` | Static factory method to create a rule with specified levels |
| `apply(Statement, Description)` | `Statement` | Creates a `LoggingLevelsStatement` wrapping the base statement |

## Example Code

### Basic Usage

```java
import io.microsphere.logging.test.junit4.LoggingLevelsRule;
import org.junit.Rule;
import org.junit.Test;

public class MyServiceTest {

    @Rule
    public LoggingLevelsRule loggingRule = LoggingLevelsRule.levels("DEBUG", "INFO", "WARN", "ERROR");

    @Test
    public void testWithDifferentLogLevels() {
        // This test runs 4 times, once per logging level
        MyService service = new MyService();
        service.doWork();
        // The logging framework is configured to each level before each run
    }
}
```

### Testing Error Handling at Different Levels

```java
import io.microsphere.logging.test.junit4.LoggingLevelsRule;
import org.junit.Rule;
import org.junit.Test;
import static org.junit.Assert.*;

public class ErrorHandlingTest {

    @Rule
    public LoggingLevelsRule loggingRule = LoggingLevelsRule.levels("DEBUG", "INFO", "ERROR");

    @Test
    public void testErrorHandlingAcrossLevels() {
        // Verify that error handling works correctly regardless of log level
        MyService service = new MyService();
        try {
            service.riskyOperation();
        } catch (Exception e) {
            // Verify the exception is properly handled
            assertNotNull(e.getMessage());
        }
    }
}
```

### Comparison with JUnit 5

| Feature | JUnit 4 (`LoggingLevelsRule`) | JUnit 5 (`@LoggingLevelsTest`) |
|---------|------------------------------|-------------------------------|
| Declaration | `@Rule` field | `@LoggingLevelsTest` on method |
| Target | Per-test method | Per-test method |
| Parameter Injection | Not supported | Supports `String level` and `int index` |
| Class-level | Not supported | Use `@LoggingLevelsClass` |
| Display Name | Standard JUnit 4 | `[LEVEL_NAME]` format |

## Version Compatibility

| Version | Java | JUnit 4 Version | Notes |
|---------|------|-----------------|-------|
| 1.0.0+ | 8+ | 4.13.2 | JUnit 4 dependency is optional |

### Maven Dependency

```xml
<dependency>
    <groupId>io.github.microsphere-projects</groupId>
    <artifactId>microsphere-logging-test</artifactId>
    <version>0.1.1-SNAPSHOT</version>
    <scope>test</scope>
</dependency>
```

**Note**: JUnit 4 (`junit:junit`) is declared as an optional dependency. Include it explicitly if you use JUnit 4 tests.

## Since Version

`@since 1.0.0`
