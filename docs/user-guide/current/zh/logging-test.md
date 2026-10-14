# microsphere-logging-test

[English version](../logging-test.md)

> 测试支持模块：借助 JUnit 4 Rule 与 JUnit Jupiter 5 扩展，让同一测试在多个日志级别下重复执行。

---

## 版本信息

| 项目 | 值 |
|------|-----|
| 分析分支 | `release`（文档集 `current`） |
| Maven 坐标 | `io.github.microsphere-projects:microsphere-logging-test` |
| `release` 分支版本 | `0.2.3-SNAPSHOT` |
| 最新已发布版本 | `0.2.2` |
| 起始版本 | `1.0.0`（对应 0.1.x 系列） |

## 兼容范围

| 依赖要求 | 版本 |
|----------|------|
| Java | 8 及以上 |
| JUnit 4 | 4+（可选，仅使用 `io.microsphere.logging.test.junit4` 包时需要） |
| JUnit Jupiter | 5.13+ |
| microsphere-logging-commons | 同版本线，由 BOM 管理 |

测试 classpath 上必须至少有一个后端适配模块（logback / log4j / log4j2 / java-logging）：
扩展通过 `LoggingUtils.loadAll()` 解析级别，因此会**同时**作用于全部已检测到的后端。

## 安装

```xml
<dependency>
    <groupId>io.github.microsphere-projects</groupId>
    <artifactId>microsphere-logging-test</artifactId>
    <scope>test</scope>
</dependency>
```

## 工作原理

1. 扩展 / Rule 加载当前 ClassLoader 可见的全部 `Logging` 实现。
2. 目标 **logger 名称**由注解属性解析：
   - `loggingClasses` 非空 → 取这些类的全限定名；
   - `loggingClasses` 为空 → 回退为**测试类所在包名**（整包切换级别）。
3. `levels` 数组的每个元素生成一个调用上下文（invocation context）。
4. 每次调用前记录所有后端上每个目标 logger 的当前级别并应用请求级别；调用结束后**恢复原级别**。

## JUnit Jupiter 5

### `@LoggingLevelsTest`——方法级级别矩阵

`@Target({METHOD, ANNOTATION_TYPE})`，与 `@Test` 一起使用；通过 `@ExtendWith` 自动注册
`LoggingLevelsTestExtension`。

```java
import io.microsphere.logging.test.jupiter.LoggingLevelsTest;
import org.junit.jupiter.api.Test;

class MyServiceTest {

    @Test
    @LoggingLevelsTest(loggingClasses = {MyService.class}, levels = {"TRACE", "DEBUG", "INFO"})
    void testBehaviorAcrossLogLevels(String level, int index) {
        // 执行三次：("TRACE", 0)、("DEBUG", 1)、("INFO", 2)
        assertEquals(new String[]{"TRACE", "DEBUG", "INFO"}[index], level);
    }

    @Test
    @LoggingLevelsTest(levels = {"DEBUG"})   // 省略 loggingClasses -> 使用测试类所在包
    void onlyNeedsDebug(String level) {
        // ...
    }
}
```

属性说明：

| 属性 | 类型 | 默认值 | 含义 |
|------|------|--------|------|
| `loggingClasses` | `Class<?>[]` | `{}` | 需要切换级别的类；为空则使用测试类包名 |
| `levels` | `String[]` | 必填 | 需要遍历的级别名，会应用到全部已加载后端 |

### `@LoggingLevelsClass`——整个测试类按级别重复

`@Target({TYPE, ANNOTATION_TYPE})`，注册 `LoggingLevelsClassExtension`，使类中每个测试方法都按级别重复执行。
它适合被**组合**进自定义类级注解：

```java
@LoggingLevelsClass(loggingClasses = {MyService.class}, levels = {"DEBUG", "INFO"})
@Retention(RUNTIME)
@Target(TYPE)
public @interface DefaultLoggingLevelsClass {
}
```

```java
@DefaultLoggingLevelsClass          // 通过元注解携带级别矩阵
class MyServiceIT {

    @Test
    void a(String level, int index) {   // 先在 DEBUG 下执行，再在 INFO 下执行
        // ...
    }
}
```

### 参数注入

`LoggingLevelParameterResolver` 把 `String` 类型参数解析为当前级别名、`int` 类型参数解析为级别下标，
参数顺序任意，仅支持这两种类型。

堆栈中可能见到的内部类：`LoggingLevelsExtension`（抽象基类）、`LoggingLevelsTestExtension`、
`LoggingLevelsClassExtension`、`LoggingLevelCallback`（设置与恢复）、`LoggingLevelTemplateInvocationContext`、
`LoggingLevelsAttributes`。

## JUnit 4

`LoggingLevelsRule` 实现 `TestRule`，可作为 `@ClassRule` / `@Rule`：

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
        // 按 Rule 中声明的每个级别各执行一次
    }
}
```

其内部由 `LoggingLevelsStatement` 包装测试语句，用测试类的 ClassLoader 加载所有 `Logging` 实现，
并在每次执行前后设置 / 恢复级别。

## 注意事项

- 由于每个级别都会应用到所有已加载后端，`levels` 中的名称必须对**每个**后端都有效。四个后端按名称真正公共的
  级别基本只有 `INFO` 与 `OFF`（JUL 使用 `SEVERE`/`WARNING`/`FINE`，Logback 无 `FATAL`）。
  最稳妥的做法是测试 classpath 只保留**一个**后端模块，或只使用该后端支持的级别名。
- 每次调用结束后级别都会恢复，测试之间互不影响、与执行顺序无关。
- 遍历级别时抓取输出，可配合 [microsphere-log4j2](./log4j2.md) 的 `InMemoryAppender`。
