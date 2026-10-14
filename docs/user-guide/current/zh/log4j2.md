# microsphere-log4j2

[English version](../log4j2.md)

> **Apache Log4j 2** 适配模块（`log4j-api` / `log4j-core`），并提供 Appender 与 Layout 工具。

---

## 版本信息

| 项目 | 值 |
|------|-----|
| 分析分支 | `release`（文档集 `current`） |
| Maven 坐标 | `io.github.microsphere-projects:microsphere-log4j2` |
| `release` 分支版本 | `0.2.3-SNAPSHOT` |
| 最新已发布版本 | `0.2.2` |
| 起始版本 | `1.0.0`（对应 0.1.x 系列） |

## 兼容范围

| 依赖要求 | 版本 |
|----------|------|
| Java | 8 及以上 |
| Log4j2（`log4j-api` + `log4j-core`） | 2.4+ |
| microsphere-logging-commons | 同版本线，由 BOM 管理 |

## 安装

```xml
<dependency>
    <groupId>io.github.microsphere-projects</groupId>
    <artifactId>microsphere-log4j2</artifactId>
</dependency>
```

## SPI 注册

| 服务文件 | 实现类 | 作用 |
|----------|--------|------|
| `META-INF/services/io.microsphere.logging.Logging` | `Log4j2Logging` | Log4j2 logger 的运行时管理 |
| `META-INF/services/io.microsphere.logging.LoggerFactory` | `Log4j2LoggerFactory` | `LoggerFactory.getLogger(...)` 返回 Log4j2 支撑的 `Logger` |

## 核心类

### `io.microsphere.logging.log4j2.Log4j2Logging`

| 常量 / 行为 | 值 |
|-------------|-----|
| 根 logger 名 | `""`（`org.apache.logging.log4j.LogManager.ROOT_LOGGER_NAME`） |
| `PRIORITY` | `NORMAL_PRIORITY - 5`（即 `-5`），内置后端中最低 |
| `ALL_LEVELS` | 解析自 `org.apache.logging.log4j.Level`：`OFF`、`FATAL`、`ERROR`、`WARN`、`INFO`、`DEBUG`、`TRACE`、`ALL` |
| `getName()` | `"Log4j2"` |

### `io.microsphere.logging.log4j2.Log4j2Logger` / `Log4j2LoggerFactory`

把 `org.apache.logging.log4j.Logger` 桥接到统一的 `io.microsphere.logging.Logger` 门面：

```java
import io.microsphere.logging.Logger;
import io.microsphere.logging.LoggerFactory;

Logger logger = LoggerFactory.getLogger(MyClass.class); // 本 classpath 上为 Log4j2Logger
logger.warn("Value: {}", value);
```

### `io.microsphere.logging.log4j2.util.Log4j2Utils`

级别管理：

```java
import io.microsphere.logging.log4j2.util.Log4j2Utils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Configuration;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;

Configuration config = Log4j2Utils.getConfiguration();
LoggerContext context = Log4j2Utils.getLoggerContext();
Collection<Logger> loggers = Log4j2Utils.getLoggers();

Logger logger = Log4j2Utils.getLogger("io.microsphere");   // 或 getLogger(MyClass.class)

Level level = Log4j2Utils.getLevel("io.microsphere");
String levelString = Log4j2Utils.getLevelString("io.microsphere"); // 例如 "INFO"

Log4j2Utils.setLoggerLevel("io.microsphere", "DEBUG");   // 按名称
Log4j2Utils.setLoggerLevel(logger, Level.TRACE);         // 按 Logger + Level
Log4j2Utils.setLoggerLevel(logger, null);                // null 表示继承父级配置
```

Appender 操作：

```java
// 对每个 logger 执行消费逻辑
Log4j2Utils.doInLogger(l -> l.setAdditivity(true));

// 挂载 / 卸载 Appender
Log4j2Utils.addAppender(myAppender, Log4j2Utils.getLoggers());
Log4j2Utils.addAppenderForAllLoggers(myAppender);
Log4j2Utils.removeAppender(myAppender, loggers);
Log4j2Utils.removeAppenderForAllLoggers(myAppender);

// 按名称查找已配置的 Appender
ConsoleAppender console = Log4j2Utils.findAppender("Console");
```

### `io.microsphere.logging.log4j2.appender.InMemoryAppender`

名称为 `"InMemory"` 的 `Appender`（继承 `AbstractLifeCycle`），把 `LogEvent` 缓冲在按
`LogEventComparator` 排序的 `ConcurrentSkipListSet` 中，因此抓取到的事件天然保持时间顺序。

```java
InMemoryAppender appender = new InMemoryAppender();
appender.initialize();
appender.start();                       // 需要先启动生命周期才会记录

Log4j2Utils.addAppenderForAllLoggers(appender);
MyService.doSomething();                // 被测代码产生日志

// 缓冲区没有公开的读取方法，可 transfer 到其他 Appender（如 Log4j2 自带的 ListAppender）再断言
appender.transfer(listAppender);

appender.stop();                        // stop() 会清空已缓冲事件

// 查找已在 Log4j2 配置中声明的实例（可能返回 null）
InMemoryAppender configured = InMemoryAppender.findInMemoryAppender();
```

### Layout 与比较器

- `DelegatingLayout<T extends Serializable>`：把 `toByteArray` / `toSerializable` / `encode` / 头尾内容
  全部转发给被委托的 Layout（`new DelegatingLayout<>(other)` 或无参构造）。适合在不重写格式规则的前提下包装 Appender。
- `SmartFileAppenderLayout<T extends Serializable>`：以 `LoggerContext` 构造；对每条 `LogEvent` 选择其所属 logger
  对应的文件 Appender / Layout 进行格式化。其 `DEFAULT_LAYOUT` 是 pattern 为 `%m`（仅消息）的 `PatternLayout`。
- `LogEventComparator`（单例 `INSTANCE`）：按时间顺序比较 `LogEvent`，已被 `InMemoryAppender` 内部使用。

## 注意事项

- 通过本适配器修改的级别对运行中的 `LoggerContext` 立即生效。
- 若同时引入 JUL 适配模块，`LoggingUtils.load()` 仍先返回 `JavaLogging`（优先级 `+10` 对 `-5`）；
  需要显式指定 Log4j2 时请用 `loadAll()` 并按 `getName()` 过滤。
- `FATAL` 在 Log4j 1.x 与 Log4j2 中存在，但 Logback 没有。
