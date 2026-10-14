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
| `PRIORITY` | `NORMAL_PRIORITY - 5`（即 `-5`）——四个内置适配器中数值**最小**，因此优先级**最高**，也是 `load()` 的返回结果 |
| `ALL_LEVELS` | 解析自 `org.apache.logging.log4j.Level`：`OFF`、`FATAL`、`ERROR`、`WARN`、`INFO`、`DEBUG`、`TRACE`、`ALL` |
| `getName()` | `"Log4j2"` |

### `io.microsphere.logging.log4j2.Log4j2Logger` / `Log4j2LoggerFactory`

把 `org.apache.logging.log4j.Logger` 桥接到统一的 `io.microsphere.logging.Logger` 门面：

```java
import io.microsphere.logging.Logger;
import io.microsphere.logging.LoggerFactory;

Logger logger = LoggerFactory.getLogger(MyClass.class); // 本 classpath 上为 Log4j2Logger
logger.warn("Value: {}", value);

// 需要 Log4j2 专有 API 时取出被委托对象
org.apache.logging.log4j.Logger delegate =
        (org.apache.logging.log4j.Logger) ((DelegatingWrapper) logger).getDelegate();
```

`Log4j2Logger` 是包级私有类，内部持有 `Log4j2Utils.getLogger(name)` 创建的 Log4j2 logger；所有
`is*Enabled` 判断与 `trace/debug/info/warn/error` 方法（消息形式、消息 + `Throwable` 形式）都是直接透传，
适配层本身不做缓冲、格式化或级别判断。请通过 `LoggerFactory` 获取，不要自行构造。

`Log4j2LoggerFactory.getPriority()` 返回 `Log4j2Logging.PRIORITY`（`-5`），因此 `Logging` 与 `LoggerFactory`
两份 SPI 列表口径一致：只要 Log4j2 在 classpath 上，两类 SPI 都由它胜出。

### `io.microsphere.logging.log4j2.util.Log4j2Utils`

级别管理：

```java
import io.microsphere.logging.log4j2.util.Log4j2Utils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Configuration;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;

Configuration config = Log4j2Utils.getConfiguration();
LoggerContext context = Log4j2Utils.getLoggerContext();     // (LoggerContext) LogManager.getContext(false)
Collection<Logger> loggers = Log4j2Utils.getLoggers();      // context.getLoggers()

Logger logger = Log4j2Utils.getLogger("io.microsphere");   // 或 getLogger(MyClass.class)

Level level = Log4j2Utils.getLevel("io.microsphere");       // 声明级别——继承态时为 null
String levelString = Log4j2Utils.getLevelString("io.microsphere");

Log4j2Utils.setLoggerLevel("io.microsphere", "DEBUG");   // 按名称（null/非法值经 Level.toLevel 得 DEBUG）
Log4j2Utils.setLoggerLevel(logger, Level.TRACE);         // 按 Logger + Level
Log4j2Utils.setLoggerLevel(logger, (Level) null);        // logger.setLevel(null) -> 恢复继承父级配置
```

`Log4j2Utils.getLevel` 只读 `logger.getLevel()`，不会回退到有效级别，所以继承态的 logger 返回 `null`。
应把 `null` 理解为“该 logger 未声明级别”，而非错误。

Appender 操作：

```java
// 对每个 logger 执行消费逻辑
Log4j2Utils.doInLogger(l -> l.setAdditivity(true));

// 挂载 / 卸载 Appender（仅运行期生效，不会写回 log4j2.xml）
Log4j2Utils.addAppender(myAppender, Log4j2Utils.getLoggers());
Log4j2Utils.addAppenderForAllLoggers(myAppender);
Log4j2Utils.removeAppender(myAppender, loggers);
Log4j2Utils.removeAppenderForAllLoggers(myAppender);

// 按名称查找已配置的 Appender
ConsoleAppender console = Log4j2Utils.findAppender("Console");
```

`addAppender` / `removeAppender` 内部对 `LoggerContext` 加锁同步，按需 start/stop 该 Appender，修改
`Configuration`，再对每个 `LoggerConfig` 挂载或卸载 Appender，最后调用 `loggerContext.updateLoggers()`。
因此变更**立即生效且只在内存中**；一旦配置重载（`Configurator.initialize(...)` 或定时重配检查）就会回到
配置文件声明的状态。

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
appender.transfer(listAppender);        // 破坏性操作：事件被逐个 pollFirst() 取出

appender.stop();                        // stop() 会清空已缓冲事件

// 查找已在 Log4j2 配置中声明的实例（可能返回 null）
InMemoryAppender configured = InMemoryAppender.findInMemoryAppender();
```

```java
// 测试用例证据 —— InMemoryAppenderTest.java:95-100
// 只有配置里声明了名为 "InMemory" 的 Appender，findInMemoryAppender() 才返回非 null
```

### Layout 与比较器

- `DelegatingLayout<T extends Serializable>`：可变的 `Layout` 持有者，`getFooter`、`getHeader`、`toByteArray`、
  `toSerializable`、`getContentType`、`getContentFormat`、`encode` 全部透传给被委托对象。
  用 `new DelegatingLayout<>(other)` 构造；无参构造会把 delegate 留为 `null`，必须先 `setDelegate(...)`
  才能使用，否则首次格式化即 NPE。适合在不重写格式规则的前提下包装 Appender。
- `SmartFileAppenderLayout<T extends Serializable>`：以 `LoggerContext` 构造，且是**急切**的——一次性遍历
  `context.getLoggers()`，为每个 logger 名缓存一个委托 `Layout`。选取规则：先找该 logger 的
  `RollingFileAppender`，退回到任意 `FileAppender`，**跳过名为 `InMemory` 的 Appender**；若该 logger 自身
  没有非 in-memory Appender，则递归到父 logger；整条链路都取不到 Layout 时使用 `DEFAULT_LAYOUT`
  （pattern 为 `%m` 的 `PatternLayout`）。格式化 `LogEvent` 时按 `getOrDefault(loggerName, DEFAULT_LAYOUT)`
  查表，因此**构造之后**才创建的 logger 一律用 `%m` 格式化。
- `LogEventComparator`（单例 `INSTANCE`）：先按 `getInstant().getEpochMillisecond()` 比较，再用
  `getNanoOfMillisecond()` 打破平局；已被 `InMemoryAppender` 内部使用。

## 注意事项

- 通过本适配器修改的级别立即作用于运行中的 `LoggerContext`，且不会写回配置文件。
- Log4j2 的优先级**最高**（`-5`），因此只要本模块在 classpath 上，`LoggingUtils.load()` 就返回
  `Log4j2Logging`——即使 JUL、Log4j 或 Logback 同时存在。需要其他后端时用 `loadAll()` 按 `getName()` 过滤。
- `FATAL` 在 Log4j 1.x 与 Log4j2 中存在，但 Logback 没有。
- 本分支实际生效的 `log4j-api` 版本为 `2.9.1`（由 BOM 声明）；模块使用的 Log4j2 core API
  （`LoggerContext`、`LoggerConfig`、`AbstractLifeCycle`）自 2.4 起保持稳定。
