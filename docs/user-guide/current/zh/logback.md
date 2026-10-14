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
| `PRIORITY` | `NORMAL_PRIORITY`（即 `0`）——四个内置适配器中排第三，Log4j（`-3`）与 Log4j2（`-5`）优先级更高 |
| `ALL_LEVELS` | 解析自 `ch.qos.logback.classic.Level`：`OFF`、`ERROR`、`WARN`、`INFO`、`DEBUG`、`TRACE`、`ALL` |
| `getName()` | `"Logback"` |

`LogbackUtils` 用静态字段持有上下文：

```java
// LogbackUtils.java:38
static final LoggerContext loggerContext = (LoggerContext) getILoggerFactory();
```

这里的强制转型未做检查。若 classpath 上 SLF4J 实际绑定的是别的服务提供者（Log4j2、JUL、reload4j），
首次访问 `LogbackUtils` 会在静态初始化块中抛 `ClassCastException`，表现为
`ExceptionInInitializerError`，后续访问变成 `NoClassDefFoundError`。因此只有当 `logback-classic`
确实是 SLF4J 绑定时才引入本模块。

本模块**只**注册 `Logging` 服务，**不**注册 `LoggerFactory` SPI：业务日志输出仍完全走原有 SLF4J/Logback，
本模块只负责**管理**能力（级别查询与修改、logger 列表、JMX）。

## 使用示例

通过框架无关的 `Logging` API：

```java
import io.microsphere.logging.Logging;
import static io.microsphere.logging.LoggingUtils.load;

Logging logging = load();                        // 只有在没有更高优先级适配器时才返回 LogbackLogging
logging.setLoggerLevel("io.microsphere", "DEBUG");  // 立即生效
logging.setLoggerLevel("io.microsphere", null);     // 注意：经 Level.toLevel 解析为 DEBUG，并非恢复继承
```

通过 `LogbackUtils` 直接操作 Logback：

```java
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import io.microsphere.logging.logback.util.LogbackUtils;

LoggerContext context = LogbackUtils.getLoggerContext();

Logger byClass = LogbackUtils.getLogger(MyService.class);
Logger byName = LogbackUtils.getLogger("io.microsphere");   // 按需创建

Level level = LogbackUtils.getLevel("io.microsphere");              // 声明级别，为 null 时取有效级别
String levelString = LogbackUtils.getLevelString("io.microsphere"); // 例如 "DEBUG"

LogbackUtils.setLoggerLevel("io.microsphere", "TRACE");  // 按名称 + 级别名（null/非法 -> DEBUG）
LogbackUtils.setLoggerLevel(byName, Level.DEBUG);        // 按 Logger + Level 对象
LogbackUtils.setLoggerLevel(byName, (Level) null);       // 真正恢复继承的写法
```

`LogbackUtils.setLoggerLevel(Logger, Level)` 直接调用 `logger.setLevel(...)`，因此传入 `null` **`Level`**
会真正恢复继承；而两个 `String` 重载经 `Level.toLevel(...)` 解析，`null` 与非法名称都会落到 `DEBUG`。

`LogbackUtilsTest` 锁定的空值行为：

```java
// 测试用例证据 —— LogbackUtilsTest.java:56-57,62,80
assertThrows(NullPointerException.class, () -> getLogger((Class<?>) null));
assertThrows(IllegalArgumentException.class, () -> getLogger((String) null)); // Logback："name argument cannot be null"
assertNull(getLevel((Logger) null));                       // logger 为 null -> 返回 null，不抛异常
setLoggerLevel((Logger) null, "DEBUG");                    // logger 为 null -> 静默无操作
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
- `setLoggerLevel(loggerName, …)` 不会因 logger 名未知而抛异常——Logback 按需创建 logger；
  非法或 `null` 级别名经 `Level.toLevel(...)` 静默变成 `DEBUG`。
- `getLoggerNames()` 反映调用时 `LoggerContext` 的内容（按名称升序）；Logback 首次 `getLogger(...)`
  时才创建 logger。注意 `getLoggerList()` **不包含根 logger**，所以 `"ROOT"` 只出现在
  `getRootLoggerName()` 中，永远不会出现在 `getLoggerNames()` 里。
- `getParentLoggerName` 沿用 `Logging` 接口默认的纯词法实现，因此无点号的名字返回 `"ROOT"`，
  根 logger 名返回 `null`——详见[后端级别语义对照](./overview.md#后端级别语义对照)。
- 若同一套管理代码可能面向多个后端，建议统一使用大写级别名以保证可移植性。
