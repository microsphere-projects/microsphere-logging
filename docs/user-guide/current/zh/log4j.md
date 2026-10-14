# microsphere-log4j

[English version](../log4j.md)

> **Log4j 1.x** 适配模块（`log4j:log4j`）。

---

## 版本信息

| 项目 | 值 |
|------|-----|
| 分析分支 | `release`（文档集 `current`） |
| Maven 坐标 | `io.github.microsphere-projects:microsphere-log4j` |
| `release` 分支版本 | `0.2.3-SNAPSHOT` |
| 最新已发布版本 | `0.2.2` |
| 起始版本 | `1.0.0`（对应 0.1.x 系列） |

## 兼容范围

| 依赖要求 | 版本 |
|----------|------|
| Java | 8 及以上 |
| Log4j | 1.2+ |
| microsphere-logging-commons | 同版本线，由 BOM 管理 |

> Log4j 1.x 上游已停止维护（EOL）。本适配模块服务于遗留系统；新项目建议使用 `microsphere-log4j2`
> 或 `microsphere-logback`。

## 安装

```xml
<dependency>
    <groupId>io.github.microsphere-projects</groupId>
    <artifactId>microsphere-log4j</artifactId>
</dependency>
```

## SPI 注册

本模块注册**两个**服务：

| 服务文件 | 实现类 | 作用 |
|----------|--------|------|
| `META-INF/services/io.microsphere.logging.Logging` | `Log4jLogging` | Log4j logger 的运行时管理 |
| `META-INF/services/io.microsphere.logging.LoggerFactory` | `Log4jLoggerFactory` | `LoggerFactory.getLogger(...)` 返回 Log4j 支撑的 `Logger` |

## 核心类

### `io.microsphere.logging.log4j.Log4jLogging`

| 常量 / 行为 | 值 |
|-------------|-----|
| 根 logger 名 | `Log4jUtils.ROOT_LOGGER_NAME`——即 Log4j 1.x 根 logger `"root"` |
| `PRIORITY` | `NORMAL_PRIORITY - 3`（即 `-3`） |
| `ALL_LEVELS` | 解析自 `org.apache.log4j.Level`：`OFF`、`FATAL`、`ERROR`、`WARN`、`INFO`、`DEBUG`、`TRACE`、`ALL` |
| `getName()` | `"Log4j"` |

### `io.microsphere.logging.log4j.Log4jLogger` / `Log4jLoggerFactory`

把 Log4j 1.x 的 `org.apache.log4j.Logger` 桥接到 `microsphere-java-core` 提供的
`io.microsphere.logging.Logger` / `LoggerFactory` 门面，使业务日志与管理代码共用一套 API：

```java
import io.microsphere.logging.Logger;
import io.microsphere.logging.LoggerFactory;

Logger logger = LoggerFactory.getLogger(MyClass.class); // 本 classpath 上为 Log4jLogger
logger.info("Logging through Log4j 1.x: {}", value);
```

### `io.microsphere.logging.log4j.util.Log4jUtils`

```java
import io.microsphere.logging.log4j.util.Log4jUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerRepository;

LoggerRepository repo = Log4jUtils.getLoggerRepository();

Logger logger = Log4jUtils.getLogger("io.microsphere");   // 或 getLogger(MyClass.class)

Level level = Log4jUtils.getLevel("io.microsphere");
String levelString = Log4jUtils.getLevelString("io.microsphere"); // 例如 "INFO"

Log4jUtils.setLoggerLevel("io.microsphere", "DEBUG");  // 按名称
Log4jUtils.setLoggerLevel(logger, Level.DEBUG);        // 按 Logger + Level
Log4jUtils.setLoggerLevel(logger, null);               // null 表示恢复继承
```

## 使用示例（管理 API）

```java
Logging logging = LoggingUtils.load();      // 优先级胜出时返回 Log4jLogging

logging.getLoggerNames();                   // 仓库中全部 logger
logging.getLoggerLevel("io.microsphere");   // 例如 "INFO"
logging.setLoggerLevel("io.microsphere", "TRACE");
logging.setLoggerLevel("io.microsphere", null);   // 恢复继承
```

父 logger 名称通过接口默认方法按点号层级推导，例如
`getParentLoggerName("io.microsphere.logging")` 返回 `"io.microsphere"`。

## 注意事项

- Log4j 1.x（与 Log4j2）存在 `FATAL`，而 Logback 没有——跨后端可移植的代码应避免使用。
- 非法级别名（如 `"VERBOSE"`）或未知 logger 名都会抛 `IllegalArgumentException`。
- `getLoggerNames()` 反映 `LoggerRepository` 当前内容；Log4j 1.x 在首次获取时才创建 logger。
