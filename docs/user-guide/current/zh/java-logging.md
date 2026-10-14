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
| `PRIORITY` | `NORMAL_PRIORITY + 10`（即 `10`），内置后端中**最高** |
| `ALL_LEVELS` | 由 `DefaultLoggingLevelsResolver` 解析自 `java.util.logging.Level` |
| `getName()` | `"Java Logging"` |
| `getRootLoggerName()` | `""` |

解析得到的级别名为：`OFF`、`SEVERE`、`WARNING`、`INFO`、`CONFIG`、`FINE`、`FINER`、`FINEST`、`ALL`。
JUL 在此不暴露 `TRACE`/`DEBUG` 名称——`FINEST` 对应 trace 语义、`FINE` 对应 debug 语义，
因此级别名称**不能**与其他后端互换。

由于优先级最高，只要本模块在 classpath 上，`LoggingUtils.load()` 就会优先返回 `JavaLogging`，
即使同时存在 Logback 或 Log4j2。若实际使用的是其他后端，请用 `loadAll()` 按名称筛选
（示例见 [logging-commons.md](./logging-commons.md)）。

## 使用示例

```java
import io.microsphere.logging.Logging;
import static io.microsphere.logging.LoggingUtils.load;

Logging logging = load();  // 本模块占优时返回 JavaLogging

// 当前 JUL LogManager 中注册的全部 logger 名
List<String> names = logging.getLoggerNames();

// 读取级别：logger 不存在返回 null；Level 为 null（继承态）时返回空字符串
String level = logging.getLoggerLevel("io.microsphere");

// 按名称设置级别（使用 JUL 名称：FINE，而非 DEBUG）
logging.setLoggerLevel("io.microsphere", "FINE");

// 恢复继承（JUL 语义：级别设为 null）
logging.setLoggerLevel("io.microsphere", null);

// 父级名称按点号层级推导
String parent = logging.getParentLoggerName("io.microsphere.logging"); // "io.microsphere"
```

JMX 能力由 `microsphere-logging-commons` 提供，与本适配器开箱即用：

```java
LoggingMXBeanRegistrar.registerAll();
// MBean ObjectName：io.microsphere.logging:type=Java Logging
```

## 注意事项

- `setLoggerLevel` 在 logger 未注册于 `LogManager`、或级别名不是合法 JUL 级别时抛 `IllegalArgumentException`。
- `getLoggerNames()` 只反映调用时 `LogManager` 中已存在的 logger；JUL 首次 `Logger.getLogger(...)` 时才创建。
- 与 Logback/Log4j2 混用时，跨后端公共级别名基本只有 `INFO` 与 `OFF`，批量操作请按后端分别处理。
