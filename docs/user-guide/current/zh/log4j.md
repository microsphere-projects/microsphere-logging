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

`Log4jLoggerFactory.getPriority()` 刻意返回 `Log4jLogging.PRIORITY`（`-3`），因此两份 SPI 列表的排序依据一致，
同一后端的 `Logging`/`LoggerFactory` 配对保持同步。`microsphere-java-core` 在静态字段中只解析一次
`LoggerFactory`，所以胜出者固定到 JVM 结束——若 Log4j2 也在 classpath 上，接管日志输出的是 Log4j2（`-5`）。

## 核心类

### `io.microsphere.logging.log4j.Log4jLogging`

| 常量 / 行为 | 值 |
|-------------|-----|
| 根 logger 名 | `Log4jUtils.ROOT_LOGGER_NAME`——即 Log4j 1.x 根 logger `"root"` |
| `PRIORITY` | `NORMAL_PRIORITY - 3`（即 `-3`）——仅次于 Log4j2（`-5`），高于 Logback（`0`）与 JUL（`10`） |
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

LoggerRepository repo = Log4jUtils.getLoggerRepository();   // LogManager.getLoggerRepository()，缓存在静态字段

Logger logger = Log4jUtils.getLogger("io.microsphere");   // 或 getLogger(MyClass.class)

Level level = Log4jUtils.getLevel("io.microsphere");        // 声明级别，为 null 时取有效级别
String levelString = Log4jUtils.getLevelString("io.microsphere"); // 例如 "INFO"

Log4jUtils.setLoggerLevel("io.microsphere", "DEBUG");  // 按名称
Log4jUtils.setLoggerLevel(logger, Level.DEBUG);        // 按 Logger + Level
Log4jUtils.setLoggerLevel(logger, (String) null);      // Level.toLevel(null) -> DEBUG，并非继承
Log4jUtils.setLoggerLevel(logger, (Level) null);       // 真正“恢复继承父级”的写法
```

`ROOT_LOGGER_NAME` 在类初始化时就从仓库读取（`Log4jUtils.java:44`），所以 Log4j 的根 logger 上报为
`"root"`——这正是 Log4j 1.x 给根 logger 的名字。

## 使用示例（管理 API）

```java
Logging logging = LoggingUtils.load();      // 只有在 Log4j2 缺席时才返回 Log4jLogging（-3 vs -5）

logging.getLoggerNames();                   // 仓库中全部 logger
logging.getLoggerLevel("io.microsphere");   // 例如 "INFO"
logging.setLoggerLevel("io.microsphere", "TRACE");
logging.setLoggerLevel("io.microsphere", null);   // 注意：Level.toLevel(null) -> DEBUG，不是“继承”
```

父 logger 名称通过接口默认方法按点号层级推导，例如
`getParentLoggerName("io.microsphere.logging")` 返回 `"io.microsphere"`，
`getParentLoggerName("root")` 返回 `null`。

## 注意事项

- Log4j 1.x（与 Log4j2）存在 `FATAL`，而 Logback 没有——跨后端可移植的代码应避免使用。
- 非法或 `null` 级别名**不会**抛异常：`Level.toLevel(...)` 将其默认为 `DEBUG`。未知的 logger 名同样被接受，
  因为 Log4j 1.x 在查找时按需创建 logger。
- `getLoggerNames()` 在调用时排空 `LoggerRepository.getCurrentLoggers()`（一个 `Enumeration`）；
  Log4j 1.x 首次获取时才创建 logger，因此尚无人请求的新 logger 可能不在列表中。
- Log4j 1.x 的 `Level` 暴露 `public static final int TRACE_INT` 等常量，解析器的类型过滤会排除它们，
  这正是 `getSupportedLoggingLevels()` 只返回八个 `Level` 类型常量的原因。
