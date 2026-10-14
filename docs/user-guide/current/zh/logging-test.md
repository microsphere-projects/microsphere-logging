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
`LoggingLevelsTestExtension`。被标注的方法成为一个 JUnit **测试模板（test template）**，按 `levels` 的每个元素
执行一次；当前级别只能通过 `Logging` API 观察，不能通过方法参数获取（见[参数注入](#参数注入)）。

```java
// 测试用例证据 —— LoggingLevelsTestTest
@Test
@LoggingLevelsTest(levels = {"WARN", "ERROR"})
void test() {
    Logging logging = load();
    String loggerName = LoggingLevelsTestTest.class.getPackage().getName();
    assertNotEquals("INFO", logging.getLoggerLevel(loggerName));
}
```

属性说明：

| 属性 | 类型 | 默认值 | 含义 |
|------|------|--------|------|
| `loggingClasses` | `Class<?>[]` | `{}` | 需要切换级别的类；为空则使用测试类包名 |
| `levels` | `String[]` | 必填 | 需要遍历的级别名，会应用到全部已加载后端 |

### `@LoggingLevelsClass`——整个测试类按级别重复

`@Target({TYPE, ANNOTATION_TYPE})`，注册 `LoggingLevelsClassExtension`，使**整个测试类**按级别重复执行。
它适合被**组合**进自定义类级注解：

```java
// 测试用例证据 —— DefaultLoggingLevelsClass.java
@Target(TYPE)
@Retention(RUNTIME)
@LoggingLevelsClass(levels = {"TRACE", "INFO", "WARN"})
public @interface DefaultLoggingLevelsClass {
}
```

```java
// 测试用例证据 —— LoggingLevelsClassTest
@DefaultLoggingLevelsClass          // 通过元注解携带级别矩阵
class LoggingLevelsClassTest {

    @Test
    void test(String level, int index) {
        LoggingLevelsClass annotation = findMetaAnnotation(LoggingLevelsClassTest.class, LoggingLevelsClass.class);
        String[] levels = annotation.levels();
        assertEquals(levels[index], level);   // 依次为 TRACE/0、INFO/1、WARN/2
    }
}
```

注意上面的 `@Test` 方法并未直接标注 `@LoggingLevelsClass`——扩展是通过 `@DefaultLoggingLevelsClass` 上的元注解
找到它的。你在组合注解上设置的属性，就是扩展实际读取的属性。

### 参数注入

`LoggingLevelParameterResolver` 把 `String` 类型参数解析为当前级别名、`int` 类型参数解析为零基级别下标，
参数顺序任意。它**只在类模板场景**注册
（`LoggingLevelTemplateInvocationContext.getAdditionalExtensions()` 仅在 `isClassTemplate` 为 `true` 时添加），因此：

- `@LoggingLevelsClass`（或组合注解）——`void test(String level, int index)` 可用；
- 方法上的 `@LoggingLevelsTest`——**不会**注入参数；请声明无参测试方法，并通过
  `Logging.getLoggerLevel(...)` 读回当前级别。

每次调用的展示名为 `[级别名]`（例如 `[TRACE]`、`[INFO]`、`[WARN]`）。

堆栈中可能见到的内部类：`LoggingLevelsExtension`（抽象基类）、`LoggingLevelsTestExtension`、
`LoggingLevelsClassExtension`、`LoggingLevelCallback`（设置与恢复）、`LoggingLevelTemplateInvocationContext`、
`LoggingLevelsAttributes`。

## JUnit 4

`LoggingLevelsRule` 实现 `TestRule`，可作为 `@Rule` 或 `@ClassRule`。它的工厂方法只接收级别名，
**没有** `loggingClasses` 等价物：

```java
// 测试用例证据 —— LoggingLevelsRuleTest
public class LoggingLevelsRuleTest {

    @ClassRule
    public static final LoggingLevelsRule loggingLevelsRule = levels("TRACE", "DEBUG", "INFO");

    @Test
    public void test() {
        assertNotNull(loggingLevelsRule);
    }
}
```

Rule 用 `LoggingLevelsStatement` 包装测试语句，后者按以下顺序执行：

1. 用 `ofSet(levels)` 对级别名去重，因此 `"DEBUG", "DEBUG"` 只产生一次迭代；
2. 目标 logger 名固定为 **`testClass.getPackage().getName()`**（只有一个）；
3. 用测试类的 ClassLoader 加载全部 `Logging` 实现；
4. **外层遍历后端**：先记录当前级别，**内层遍历级别**——注册子测试描述、设置级别、执行被包装的语句，
   最后在 `finally` 中恢复原级别。

因此测试体的执行次数是 **后端数 × 级别数**，而不是级别数：三个级别 + 一个后端 = 3 次，
三个级别 + 两个后端 = 6 次。方法级重复用 `@Rule`，整类重复用 `@ClassRule`。

## 注意事项

- 由于每个级别都会应用到所有已加载后端，`levels` 中的名称必须对**每个**后端都有效。四个后端按名称真正公共的
  级别基本只有 `INFO` 与 `OFF`（JUL 使用 `SEVERE`/`WARNING`/`FINE`，Logback 无 `FATAL`）。
  最稳妥的做法是测试 classpath 只保留**一个**后端模块，或只使用该后端支持的级别名。
- 每次调用结束后级别都会恢复，测试之间互不影响、与执行顺序无关——**但恢复并不总是中性的**。
  `LoggingLevelCallback` 把 `getLoggerLevel()` 的返回值原样再传给 `setLoggerLevel()`：
  - Log4j2 上继承态的 logger 返回 `null`，而 `null` 被解析为 **`DEBUG`**，于是该 logger 被固定在 DEBUG，
    不再是继承态；
  - JUL 上继承态的 logger 返回 `""`，而 `Level.parse("")` 会失败，因此对未显式声明过级别的 logger，
    恢复时抛 `IllegalArgumentException`。
  这也是仓库内建测试只断言自己显式设置过的级别的原因。
- 类模板形态（`@LoggingLevelsClass`）依赖 JUnit Jupiter 的 `ClassTemplateInvocationContextProvider`，
  这正是本模块要求 **Jupiter 5.13+** 的原因；方法级形态只需经典的 `TestTemplateInvocationContextProvider`。
- 扩展通过 `microsphere-java-core` 的 logger 打印每次切换，日志文案为
  `Set logger[name :'{}']s' level ['{}' -> '{}] via {}`（源码里右引号缺失），
  编写日志解析规则时不要按规范引号去匹配。
- 遍历级别时抓取输出，可配合 [microsphere-log4j2](./log4j2.md) 的 `InMemoryAppender`。
