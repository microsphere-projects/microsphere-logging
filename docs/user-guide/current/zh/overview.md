# Microsphere Logging 用户指南（release 分支 / current）

[English version](../overview.md)

> 分析分支：`release` · 版本：`0.2.3-SNAPSHOT`（最新已发布：`0.2.2`）

---

## Microsphere Logging 是什么

Microsphere Logging 是跨多个 Java 日志框架的**运行时日志管理统一抽象层**。它**不替代**你现有的日志框架，
而是在其之上叠加一套与框架无关的管理 API，从而实现：

- **不重启服务**即可查询和修改日志级别；
- 一套运维代码同时适配 **JUL、Logback、Log4j 1.x 与 Log4j 2**；
- 通过 **JMX**（`io.microsphere.logging:type=<后端名>`）向运维工具暴露日志配置；
- 使用开箱即用的 JUnit 4 / JUnit Jupiter 5 支持，在**多日志级别矩阵**下验证应用行为；
- 通过 **Java SPI**（`META-INF/services/io.microsphere.logging.Logging`）扩展自定义后端。

## 模块地图

| 模块 | 文档 | 提供的能力 |
|------|------|------------|
| `microsphere-logging-commons` | [logging-commons.md](./logging-commons.md) | `Logging` 接口、`LoggingUtils`（SPI 加载）、级别解析器、JMX 适配器与注册器 |
| `microsphere-java-logging` | [java-logging.md](./java-logging.md) | `JavaLogging`——JUL 后端适配 |
| `microsphere-logback` | [logback.md](./logback.md) | `LogbackLogging` + `LogbackUtils` |
| `microsphere-log4j` | [log4j.md](./log4j.md) | `Log4jLogging`、`LoggerFactory` SPI、`Log4jUtils` |
| `microsphere-log4j2` | [log4j2.md](./log4j2.md) | `Log4j2Logging`、`LoggerFactory` SPI、`Log4j2Utils`、`InMemoryAppender`、Layout |
| `microsphere-logging-test` | [logging-test.md](./logging-test.md) | `@LoggingLevelsTest` / `@LoggingLevelsClass` / `LoggingLevelsRule` |
| `microsphere-logging-parent` / `-dependencies` | [parent-and-dependencies.md](./parent-and-dependencies.md) | 父 POM + BOM 版本管理 |
| `microsphere-logging-examples` | [examples.md](./examples.md) | 可运行的 `LoggingExample` |

## 快速开始（5 分钟）

1. 引入 BOM：

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>io.github.microsphere-projects</groupId>
            <artifactId>microsphere-logging-dependencies</artifactId>
            <version>0.2.2</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

2. 添加与实际日志框架匹配的后端模块（以 Logback 为例）：

```xml
<dependency>
    <groupId>io.github.microsphere-projects</groupId>
    <artifactId>microsphere-logback</artifactId>
</dependency>
```

3. 运行时管理日志：

```java
import io.microsphere.logging.Logging;
import static io.microsphere.logging.LoggingUtils.load;

Logging logging = load();                           // SPI：优先级数值最小者胜出
logging.setLoggerLevel("io.microsphere", "DEBUG");  // 立即生效
```

> 级别参数传 `null` **只有在 JUL 适配器上**才表示恢复继承。Logback、Log4j、Log4j2 用
> `Level.toLevel(...)` 解析级别字符串，其默认值是 **`DEBUG`**——详见
> [后端级别语义对照](#后端级别语义对照)。

4. （可选）通过 JMX 开放给运维：

```java
io.microsphere.logging.jmx.LoggingMXBeanRegistrar.registerAll();
```

## 版本与兼容性汇总

| 项目 | 值 |
|------|-----|
| `release` 分支版本 | `0.2.3-SNAPSHOT` |
| 最新已发布版本 | `0.2.2` |
| Java 基线 | 8 及以上 |
| microsphere-java 依赖线 | 0.3.19（由 `microsphere-logging-parent` 锁定） |
| JUL | JDK 内置（`LoggingMXBean` 自 Java 6 起提供） |
| Logback | 1.2+（SLF4J 1.7+） |
| Log4j | 1.2+（上游已 EOL，仅用于遗留系统） |
| Log4j2 | 2.4+ |
| JUnit | 4+ / Jupiter 5.13+（测试模块） |

源码 javadoc 中的 `@since 1.0.0` 表示 API 世代，而非某个已发布的 `1.x` 版本线：本仓库发布的是 `0.1.x` / `0.2.x`，
**不存在 `release-1.x` 分支**，因此 `docs/` 下只有 `current` 一套文档。

## 后端优先级（`load()` 的胜出者）

优先级数值按**升序**比较，而 `io.microsphere.lang.Prioritized` 定义
`MAX_PRIORITY = Integer.MIN_VALUE`、`MIN_PRIORITY = Integer.MAX_VALUE`。因此数值**越小优先级越高**，
`LoggingUtils.load()` 返回排序后 SPI 列表的第一个元素。

| 后端 | 优先级数值 | `load()` 顺序 |
|------|------------|---------------|
| Log4j2Logging | `NORMAL_PRIORITY - 5`（即 `-5`） | **第 1**——四个内置适配器中优先级最高 |
| Log4jLogging | `NORMAL_PRIORITY - 3`（即 `-3`） | 第 2 |
| LogbackLogging | `NORMAL_PRIORITY`（即 `0`） | 第 3 |
| JavaLogging（JUL） | `NORMAL_PRIORITY + 10`（即 `10`） | 第 4——优先级最低 |

实际影响：

- 四个适配模块同时在 classpath 上时，`load()` 返回的是 **`Log4j2Logging`**，不是 JUL。
- 需要指定某个后端时，用 `loadAll()`（不可修改、按优先级排序的列表）再按 `getName()` 过滤。
- SPI 结果在 `microsphere-java-core` 中**按服务类型缓存**（`LoggingUtils` 传入 `cached = true`），
  因此同一个 JVM 内首次加载的结果会一直复用，即使后续调用换用了不同的 `ClassLoader`。

```java
Logging logback = LoggingUtils.loadAll().stream()
        .filter(l -> "Logback".equals(l.getName()))
        .findFirst()
        .orElseThrow();
```

## 后端级别语义对照

`Logging` 接口承诺一套行为，但四个适配器底下的框架原生语义各不相同。下表是可以依赖的实测契约
（每一条都由对应模块的测试用例锁定）。

| 行为 | `JavaLogging` | `LogbackLogging` | `Log4jLogging` | `Log4j2Logging` |
|------|---------------|------------------|----------------|-----------------|
| 根 logger 名 | `""` | `"ROOT"` | `"root"` | `""` |
| 支持的级别 | OFF、SEVERE、WARNING、INFO、CONFIG、FINE、FINER、FINEST、ALL | OFF、ERROR、WARN、INFO、DEBUG、TRACE、ALL | OFF、FATAL、ERROR、WARN、INFO、DEBUG、TRACE、ALL | OFF、FATAL、ERROR、WARN、INFO、DEBUG、TRACE、ALL |
| `getLoggerLevel("从未声明的.logger")` | 抛 `IllegalArgumentException: Logger … does not exist` | 返回级别字符串——Logback 按需创建 logger | 返回级别字符串——Log4j 按需创建 logger | 返回级别字符串——Log4j2 按需创建 logger |
| logger 未声明级别时的返回值 | JUL 报告继承值 | 有效级别 | 有效级别 | `null`（Log4j2 只取声明级别） |
| `setLoggerLevel(name, null)` | `Level` 置为 `null` ⇒ **真正恢复继承** | 经 `Level.toLevel(null)` ⇒ **DEBUG** | `Level.toLevel(null)` ⇒ **DEBUG** | `Level.toLevel(null)` ⇒ **DEBUG** |
| `setLoggerLevel(name, "BOGUS")` | 抛 `IllegalArgumentException: Unknown level "BOGUS"` | 静默 **DEBUG** | 静默 **DEBUG** | 静默 **DEBUG** |
| `setLoggerLevel(根logger名, level)` | 可用 | 可用 | 可用 | 可用 |
| `getParentLoggerName(根logger名)` | 委托 JDK ⇒ `""` | `null` | `null` | `null` |
| `getParentLoggerName("无点号名")` | JDK 行为 | `"ROOT"` | `"root"` | `""` |

需要牢记的三点：

1. **只有 JUL 把 `null` 当作“继承”**。其他后端把 `null` 解析成 DEBUG，任何无法识别的级别名同样如此。
   若需要 javadoc 承诺的 `IllegalArgumentException`，请在调用 `setLoggerLevel` 前用
   `getSupportedLoggingLevels()` 自行校验。
2. **Log4j2 可能返回 `null` 级别**。`Log4j2Utils.getLevel` 取的是 `logger.getLevel()`，即*声明*级别，
   可能未设置；其他适配器会回退到有效级别。请把 `null` 理解为“继承”。
3. **`getLoggerNames()` 是活动 logger 的快照，且可能不含根 logger**——Logback 的 `getLoggerList()`
   不包含 `"ROOT"`，尽管 `getRootLoggerName()` 返回它。

```java
// 测试用例证据 —— LogbackLoggingTest.java:106-107
assertEquals(ROOT_LOGGER_NAME, logging.getParentLoggerName("io")); // "ROOT"
assertNull(logging.getParentLoggerName(ROOT_LOGGER_NAME));         // 不是 ""
```

## 阅读路径建议

- **新用户**：本页 → 你所用后端的文档 → [logging-commons.md](./logging-commons.md)。
- **运维场景**：JMX 注册见 [logging-commons.md](./logging-commons.md)；运行时级别调整见各后端文档示例。
- **测试场景**：多级别测试见 [logging-test.md](./logging-test.md)；日志抓取见
  [log4j2.md](./log4j2.md) 中的 `InMemoryAppender`。
