# microsphere-logback

[English version](../logback.md)

> **Logback** 适配模块（基于 `logback-classic` / SLF4J）。

---

## 版本信息

| 项目 | 值 |
|------|-----|
| 分析分支 | `release`（文档集 `current`） |
| Maven 坐标 | `io.github.microsphere-projects:microsphere-logback` |
| `release` 分支版本 | `0.2.3-SNAPSHOT` |
| 最新已发布版本 | `0.2.2` |
| 起始版本 | `1.0.0`（对应 0.1.x 系列） |

## 兼容范围

| 依赖要求 | 版本 |
|----------|------|
| Java | 8 及以上 |
| Logback（`logback-classic`） | 1.2+ |
| SLF4J API | 1.7+ |
| microsphere-logging-commons | 同版本线，由 BOM 管理 |

## 安装

```xml
<dependency>
    <groupId>io.github.microsphere-projects</groupId>
    <artifactId>microsphere-logback</artifactId>
</dependency>
```

SPI 注册：`META-INF/services/io.microsphere.logging.Logging` → `LogbackLogging`。

## 核心类：`io.microsphere.logging.logback.LogbackLogging`

基于 Logback `LoggerContext` 实现 `Logging`。

| 常量 / 行为 | 值 |
|-------------|-----|
| 根 logger 名 | `"ROOT"`（`org.slf4j.Logger.ROOT_LOGGER_NAME`） |
| `PRIORITY` | `NORMAL_PRIORITY`（即 0）——第三方后端中最高 |
| `ALL_LEVELS` | 解析自 `ch.qos.logback.classic.Level`：`OFF`、`ERROR`、`WARN`、`INFO`、`DEBUG`、`TRACE`、`ALL` |
| `getName()` | `"Logback"` |

本模块**只**注册 `Logging` 服务，**不**注册 `LoggerFactory` SPI：业务日志输出仍完全走原有 SLF4J/Logback，
本模块只负责**管理**能力（级别查询与修改、logger 列表、JMX）。

## 使用示例

通过框架无关的 `Logging` API：

```java
import io.microsphere.logging.Logging;
import static io.microsphere.logging.LoggingUtils.load;

Logging logging = load();                 // Logback 胜出时返回 LogbackLogging
logging.setLoggerLevel("io.microsphere", "DEBUG");   // 立即生效
logging.setLoggerLevel("io.microsphere", null);      // 恢复继承父级级别
```

通过 `LogbackUtils` 直接操作 Logback：

```java
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import io.microsphere.logging.logback.util.LogbackUtils;

LoggerContext context = LogbackUtils.getLoggerContext();

Logger byClass = LogbackUtils.getLogger(MyService.class);
Logger byName = LogbackUtils.getLogger("io.microsphere");

Level level = LogbackUtils.getLevel("io.microsphere");              // Level 对象
String levelString = LogbackUtils.getLevelString("io.microsphere"); // 例如 "DEBUG"

LogbackUtils.setLoggerLevel("io.microsphere", "TRACE");  // 按名称 + 级别名
LogbackUtils.setLoggerLevel(byName, Level.DEBUG);        // 按 Logger + Level 对象
LogbackUtils.setLoggerLevel(byName, null);               // null 表示继承有效级别
```

JMX 暴露由 commons 提供：

```java
LoggingMXBeanRegistrar.registerAll();
// MBean ObjectName：io.microsphere.logging:type=Logback
```

## 注意事项

- 解析出的级别集合为 `OFF`、`ERROR`、`WARN`、`INFO`、`DEBUG`、`TRACE`、`ALL`。Logback 少用的
  `FINE`/`FINER`/`FINEST` 别名不在 `getSupportedLoggingLevels()` 中，因为解析器只读取
  `public static final Level` 字段。
- `setLoggerLevel(loggerName, ...)` 在 logger 名未知或级别名非法时抛 `IllegalArgumentException`。
- `getLoggerNames()` 反映调用时 `LoggerContext` 中的内容；Logback 首次 `getLogger(...)` 时才创建 logger。
- 若同一套管理代码可能面向多个后端，建议统一使用大写级别名以保证可移植性。
