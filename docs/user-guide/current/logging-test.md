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
`LoggingLevelsTestExtension` via `@ExtendWith`. The annotated method becomes a JUnit **test template** and runs
once per entry in `levels`; the active level is observable through the `Logging` API, not through parameters
(see [Parameter injection](#parameter-injection)).

```java
// test-case evidence — LoggingLevelsTestTest
@Test
@LoggingLevelsTest(levels = {"WARN", "ERROR"})
void test() {
    Logging logging = load();
    String loggerName = LoggingLevelsTestTest.class.getPackage().getName();
    assertNotEquals("INFO", logging.getLoggerLevel(loggerName));
}
```

Attributes:

| Attribute | Type | Default | Meaning |
|-----------|------|---------|---------|
| `loggingClasses` | `Class<?>[]` | `{}` | classes whose loggers should be switched; empty means the test class package |
| `levels` | `String[]` | required | level names to iterate; applied to every loaded backend |

### `@LoggingLevelsClass` — whole class per level

`@Target({TYPE, ANNOTATION_TYPE})`; registers `LoggingLevelsClassExtension`, which repeats the **entire test class**
once per level. It is designed to be **composed** into your own class-level annotation:

```java
// test-case evidence — DefaultLoggingLevelsClass.java
@Target(TYPE)
@Retention(RUNTIME)
@LoggingLevelsClass(levels = {"TRACE", "INFO", "WARN"})
public @interface DefaultLoggingLevelsClass {
}
```

```java
// test-case evidence — LoggingLevelsClassTest
@DefaultLoggingLevelsClass          // meta-annotation carrying the level matrix
class LoggingLevelsClassTest {

    @Test
    void test(String level, int index) {
        LoggingLevelsClass annotation = findMetaAnnotation(LoggingLevelsClassTest.class, LoggingLevelsClass.class);
        String[] levels = annotation.levels();
        assertEquals(levels[index], level);   // runs as TRACE/0, INFO/1, WARN/2
    }
}
```

Note the `@Test` method above is **not** annotated with `@LoggingLevelsClass` directly — the extension finds the
annotation through the meta-annotation on `@DefaultLoggingLevelsClass`. Any attributes you set on the composed
annotation are what the extension reads.

### Parameter injection

`LoggingLevelParameterResolver` resolves a `String` parameter to the active level name and an `int` parameter to
the zero-based level index, in any order. It is registered **only** for class templates
(`LoggingLevelTemplateInvocationContext.getAdditionalExtensions()` adds it when `isClassTemplate` is `true`), so:

- `@LoggingLevelsClass` (or a composed annotation) — `void test(String level, int index)` works;
- `@LoggingLevelsTest` on a method — parameters are **not** injected; declare a no-argument test method and read
  the level back through `Logging.getLoggerLevel(...)`.

Each invocation is displayed as `[LEVEL]` (for example `[TRACE]`, `[INFO]`, `[WARN]`).

Internals you may see in stack traces: `LoggingLevelsExtension` (abstract base), `LoggingLevelsTestExtension`,
`LoggingLevelsClassExtension`, `LoggingLevelCallback` (set + restore), `LoggingLevelTemplateInvocationContext`,
`LoggingLevelsAttributes`.

## JUnit 4

`LoggingLevelsRule` implements `TestRule` and is usable as either `@Rule` or `@ClassRule`. Its factory takes level
names only — there is no `loggingClasses` equivalent:

```java
// test-case evidence — LoggingLevelsRuleTest
public class LoggingLevelsRuleTest {

    @ClassRule
    public static final LoggingLevelsRule loggingLevelsRule = levels("TRACE", "DEBUG", "INFO");

    @Test
    public void test() {
        assertNotNull(loggingLevelsRule);
    }
}
```

The rule wraps the test statement in `LoggingLevelsStatement`, which:

1. de-duplicates the level names (`ofSet(levels)`), so `"DEBUG", "DEBUG"` yields one iteration;
2. resolves the single target logger name as **`testClass.getPackage().getName()`**;
3. loads every `Logging` implementation visible to the test class' class loader;
4. for **each backend** (outer loop): records the current level, then for **each level** (inner loop) registers a
   child test description, applies the level and evaluates the wrapped statement, and finally restores the original
   level in a `finally` block.

Consequence: the test body runs **backends × levels** times, not `levels` times. With three levels and one backend
you get three runs; with three levels and two backends you get six. Use `@Rule` for per-method iteration and
`@ClassRule` when the whole class should repeat per level.

## Notes

- Level names must be valid for **every** backend on the test classpath, because the extension applies each level to
  all loaded backends. The four backends only share `INFO` and `OFF` by name (JUL uses `SEVERE`/`WARNING`/`FINE`,
  Logback has no `FATAL`, Log4j/Log4j2 do). Safest practice: keep a **single** backend module on the test classpath,
  or limit `levels` to names that backend supports.
- Levels are restored after every invocation, so tests stay order-independent and do not leak into each other —
  **but the restore is not always neutral**. `LoggingLevelCallback` records whatever `getLoggerLevel()` returned and
  feeds it straight back to `setLoggerLevel()`:
  - on Log4j2 an inherited logger reports `null`, and `null` is parsed to **`DEBUG`**, so the logger ends up pinned
    at DEBUG instead of inheriting;
  - on JUL an inherited logger reports `""`, and `Level.parse("")` fails, so the restore throws
    `IllegalArgumentException` for a logger that had no declared level.
  This is why the shipped tests only assert on levels they explicitly set.
- The class-template flavour (`@LoggingLevelsClass`) relies on JUnit Jupiter's
  `ClassTemplateInvocationContextProvider`, which is why this module requires **Jupiter 5.13+**; the method-level
  flavour only needs the classic `TestTemplateInvocationContextProvider`.
- The repository's CI runs `mvn test --activate-profiles test,coverage`, so these templates execute in the build;
  locally a plain `mvn test` in the module directory is enough.
- The extension logs each switch through the `microsphere-java-core` logger; the message is worded
  `Set logger[name :'{}']s' level ['{}' -> '{}] via {}` (the closing quote is missing in the source), so don't grep
  for a well-formed pattern when building log-parsing rules around it.
- Capturing output while iterating levels pairs well with `InMemoryAppender` from
  [microsphere-log4j2](./log4j2.md).
