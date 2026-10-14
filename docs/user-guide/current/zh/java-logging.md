# microsphere-java-logging

[English version](../java-logging.md)

> **Java Logging（JUL）** 适配模块，即 JDK 内置日志设施的实现。

---

## 版本信息

| 项目 | 值 |
|------|-----|
| 分析分支 | `release`（文档集 `current`） |
| Maven 坐标 | `io.github.microsphere-projects:microsphere-java-logging` |
| `release` 分支版本 | `0.2.3-SNAPSHOT` |
| 最新已发布版本 | `0.2.2` |
| 起始版本 | `1.0.0`（对应 0.1.x 系列） |

## 兼容范围

| 依赖要求 | 版本 |
|----------|------|
| Java | 8 及以上（使用 `java.util.logging.LoggingMXBean`，Java 6 起即有） |
| microsphere-logging-commons | 同版本线，由 BOM 管理 |

本模块无第三方依赖，仅需 JDK。

## 安装

```xml
<dependency>
    <groupId>io.github.microsphere-projects</groupId>
    <artifactId>microsphere-java-logging</artifactId>
</dependency>
```

（引入 `microsphere-logging-dependencies` BOM 后无需写 `<version>`。）

SPI 注册：`META-INF/services/io.microsphere.logging.Logging` → `io.microsphere.logging.jdk.JavaLogging`。

## 核心类：`io.microsphere.logging.jdk.JavaLogging`

`JavaLogging` 基于 JUL 的 `LogManager` 与 `java.util.logging.LoggingMXBean`
（`LogManager.getLoggingMXBean()`）实现 `io.microsphere.logging.Logging`。

| 常量 / 行为 | 值 |
|-------------|-----|
| `ROOT_LOGGER_NAME` | `""`（空字符串，JUL 约定） |
| `PRIORITY` | `NORMAL_PRIORITY + 10`（即 `10`）——四个内置适配器中优先级**最低**（SPI 列表按升序排序） |
| `ALL_LEVELS` | 由 `DefaultLoggingLevelsResolver` 解析自 `java.util.logging.Level` |
| `getName()` | `"Java Logging"` |
| `getRootLoggerName()` | `""` |

解析得到的级别名为：`OFF`、`SEVERE`、`WARNING`、`INFO`、`CONFIG`、`FINE`、`FINER`、`FINEST`、`ALL`。
JUL 在此不暴露 `TRACE`/`DEBUG` 名称——`FINEST` 对应 trace 语义、`FINE` 对应 debug 语义，
因此级别名称**不能**与其他后端互换。

```java
// 测试用例证据 —— JavaLoggingTest.java:58-60
assertEquals("", ROOT_LOGGER_NAME);
assertEquals(10, PRIORITY);
assertEquals(JAVA_LOGGING_LEVELS, ALL_LEVELS);
```

`JavaLoggingTest` 把 `load()` 直接强转为 `JavaLogging`，这**只在**该模块 classpath 上仅有这一个适配器时才成立。
一旦 `microsphere-log4j2` 或 `microsphere-log4j` 也在 classpath 上，`load()` 返回的就是它们
（优先级 `-5` / `-3` 胜过 `10`）。若实际使用的是其他后端，请用 `loadAll()` 按名称筛选
（示例见 [logging-commons.md](./logging-commons.md)）。

## 使用示例

```java
import io.microsphere.logging.Logging;
import static io.microsphere.logging.LoggingUtils.load;

// 当 classpath 上只有本模块时：
Logging logging = load();  // -> JavaLogging

// 当前 JUL LogManager 中注册的全部 logger 名
List<String> names = logging.getLoggerNames();

// 读取级别：返回级别名；Level 为 null（继承态）时返回空字符串
// logger 名未知时抛 IllegalArgumentException("Logger … does not exist")
String level = logging.getLoggerLevel("io.microsphere");

// 按名称设置级别（使用 JUL 名称：FINE，而非 DEBUG）
logging.setLoggerLevel("io.microsphere", "FINE");

// 恢复继承——JUL 特有语义：把 Level 置为 null
logging.setLoggerLevel("io.microsphere", null);

// 父级名称委托给 JDK MXBean（根 logger 返回 ""）
String parent = logging.getParentLoggerName("io.microsphere.logging"); // "io.microsphere"
```

JMX 能力由 `microsphere-logging-commons` 提供，与本适配器开箱即用：

```java
LoggingMXBeanRegistrar.registerAll();
// MBean ObjectName：io.microsphere.logging:type=Java Logging
```

## 注意事项

- JUL 的级别名与其他后端不同：这里没有 Logback 语义的 `TRACE`/`DEBUG`——`FINEST` 对应 trace、`FINE` 对应 debug。
- `setLoggerLevel` 在 logger 未注册于 `LogManager`、或级别名不是合法 JUL 级别时抛 `IllegalArgumentException`。
  这是**唯一**遵守 `Logging` javadoc 该校验约定的适配器——Logback / Log4j / Log4j2 经 `Level.toLevel(…)`
  解析并回退到 DEBUG。
- `getLoggerNames()` 只反映调用时 `LogManager` 中已存在的 logger；JUL 首次 `Logger.getLogger(...)` 时才创建。
- `getParentLoggerName` 是 `JavaLogging` 唯一覆写的方法；其余适配器都沿用接口的词法默认实现，
  其对根 logger 名返回 `null`（而非 `""`）。
- 由于 `getName()` 返回 `"Java Logging"`（含空格），注册的 `ObjectName` 为
  `io.microsphere.logging:type=Java Logging`；严格的 JMX 客户端在查询时需要加引号。
- 完整对照见[后端级别语义对照](./overview.md#后端级别语义对照)。
- 与 Logback/Log4j2 混用时，跨后端公共级别名基本只有 `INFO` 与 `OFF`，批量操作请按后端分别处理。
