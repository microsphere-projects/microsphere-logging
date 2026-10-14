# microsphere-logging-commons

[English version](../logging-commons.md)

> 核心 API 模块：`Logging` 抽象、SPI 加载工具、日志级别解析与 JMX 集成。

---

## 版本信息

| 项目 | 值 |
|------|-----|
| 分析分支 | `release`（文档集 `current`） |
| Maven 坐标 | `io.github.microsphere-projects:microsphere-logging-commons` |
| `release` 分支版本 | `0.2.3-SNAPSHOT` |
| 最新已发布版本 | `0.2.2` |
| 起始版本 | `1.0.0`（对应 0.1.x 系列，见源码 `@since`） |

## 兼容范围

| 依赖要求 | 版本 |
|----------|------|
| Java | 8 及以上 |
| microsphere-java-core | 传递依赖，版本由 `microsphere-logging-parent` 锁定（`microsphere-java` 0.3.19） |
| SLF4J / Log4j / Log4j2 / Logback | 均为可选依赖（`<optional>true</optional>`），仅在你自行引用其类时才需要 |

本模块**对任何日志后端都没有强制的运行时依赖**；各后端适配类分别位于 `microsphere-java-logging`、
`microsphere-logback`、`microsphere-log4j`、`microsphere-log4j2` 模块中。

## 安装

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

<dependencies>
    <dependency>
        <groupId>io.github.microsphere-projects</groupId>
        <artifactId>microsphere-logging-commons</artifactId>
    </dependency>
</dependencies>
```

> 多数应用无需直接依赖本模块——引入任一后端模块即会传递引入。

## 核心概念

### 1. `io.microsphere.logging.Logging`

日志设施的管理接口，与 `java.util.logging.LoggingMXBean` 基本兼容。它继承 `io.microsphere.lang.Prioritized`，
因此多个后端可在同一 JVM 共存并按优先级解析。

| 方法 | 约定 |
|------|------|
| `String getRootLoggerName()` | 根 logger 名称（非 null）：JUL/Log4j2 为 `""`，Logback 为 `"ROOT"`，Log4j 为 `"root"` |
| `List<String> getLoggerNames()` | 当前已注册的 logger 名称——活动快照；Logback 的列表**不含** `"ROOT"` |
| `Set<String> getSupportedLoggingLevels()` | 该后端支持的级别名集合（不可修改、无序） |
| `String getLoggerLevel(String loggerName)` | javadoc 约定：logger 不存在返回 `null`，级别为继承态时返回空串。**只有 JUL 严格遵循**——Logback/Log4j 按需创建 logger 并返回有效级别，Log4j2 在未声明级别时返回 `null` |
| `void setLoggerLevel(String loggerName, String levelName)` | 按名称设置级别；`loggerName` 必须非 null。`levelName` 为 `null` **只在 JUL 上**表示恢复继承；Logback/Log4j/Log4j2 用 `Level.toLevel(…)` 解析，`null` 与非法名称都静默落到 **DEBUG**（见[后端级别语义对照](./overview.md#后端级别语义对照)） |
| `String getParentLoggerName(String loggerName)` | 接口默认实现，纯词法推导——根 logger 名返回 `null`，无点号的名字返回 `getRootLoggerName()`，传 `null` 抛 `NullPointerException`；仅 `JavaLogging` 覆写了它 |
| `String getName()` | 后端名称，如 `Java Logging`、`Logback`、`Log4j`、`Log4j2`——同时作为 JMX 的 `type=` 属性 |
| `int getPriority()` | 优先级数值；**数值越小优先级越高**（`Prioritized.MAX_PRIORITY == Integer.MIN_VALUE`） |

```java
// 测试用例证据 —— LogbackLoggingTest.java:106-107
assertEquals(ROOT_LOGGER_NAME, logging.getParentLoggerName("io")); // "ROOT"
assertNull(logging.getParentLoggerName(ROOT_LOGGER_NAME));         // 是 null，不是 ""
```

### 2. `io.microsphere.logging.LoggingUtils`

基于 Java SPI（`META-INF/services/io.microsphere.logging.Logging`）的加载工具。

```java
import io.microsphere.logging.Logging;
import static io.microsphere.logging.LoggingUtils.load;
import static io.microsphere.logging.LoggingUtils.loadAll;

// classpath 上优先级最高的 Logging 实现
// （getPriority() 数值最小者——四个内置适配器中是 Log4j2Logging）
Logging logging = load();

// 全部已检测到的实现，优先级高的在前（不可修改列表）
List<Logging> all = loadAll();

// 指定 ClassLoader（模块化 / 容器环境常用）
Logging web = load(threadContextClassLoader);
```

当 classpath 上没有任何后端模块声明 SPI 时，`load()` / `loadAll()` 抛出 `IllegalArgumentException`。
两组方法均提供可空的 `ClassLoader` 重载，`microsphere-java-core` 按
「线程上下文 ClassLoader → 加载 `ClassLoaderUtils` 的 ClassLoader → 系统 ClassLoader」的兜底链解析，
因此**永不返回 null**。

两点缓存说明：

- `ClassLoader` 参数可以为 `null`，由上面的兜底链替换。
- 两个方法都向下传递 `cached = true`，而缓存键**只有服务类型**。因此同一 JVM 内首次 `loadAll()` 的结果
  会被后续所有调用复用，即使换了不同的 `ClassLoader`。

### 3. 运行时级别管理

```java
Logging logging = load();

String level = logging.getLoggerLevel("io.microsphere");   // 例如 "INFO"（null/"" 的含义因后端而异）
logging.setLoggerLevel("io.microsphere", "DEBUG");         // 立即生效，无需重启

// 先校验再设置：只有 JUL 会拒绝非法级别名
if (logging.getSupportedLoggingLevels().contains("TRACE")) {
    logging.setLoggerLevel("io.microsphere", "TRACE");
}
```

> `logging.setLoggerLevel("io.microsphere", null)` 表示“继承”**只在 JUL 上成立**。
> Logback、Log4j、Log4j2 会把 `null` 交给 `Level.toLevel(…)`，最终级别变成 **DEBUG**。

### 4. `LoggingLevelsResolver` / `DefaultLoggingLevelsResolver`

通过反射读取级别类中 `public static final` 且类型为该级别类的字段，解析出级别名集合：

```java
import io.microsphere.logging.DefaultLoggingLevelsResolver;
import ch.qos.logback.classic.Level;

Set<String> levels = DefaultLoggingLevelsResolver.INSTANCE.resolve(Level.class);
// [OFF, ERROR, WARN, INFO, DEBUG, TRACE, ALL] —— 不可修改，顺序无保证
```

过滤规则刻意收紧：`getFields()`（公开字段，含继承）依次按 *static*、*final*、*字段类型 `==` 级别类* 三重过滤。
这会排除 Logback 的 `int`/`Integer` 型 `*_INT` 常量、Log4j 1.x 的 `TRACE_INT`，以及任何私有或非 final 成员；
而枚举常量因类型即枚举本身而通过，所以 `resolve(org.slf4j.event.Level.class)` 返回
`[ERROR, WARN, INFO, DEBUG, TRACE]`。

| 级别类 | 解析结果（由 `DefaultLoggingLevelsResolverTest` 断言） |
|--------|--------------------------------------------------------|
| `java.util.logging.Level` | OFF、SEVERE、WARNING、INFO、CONFIG、FINE、FINER、FINEST、ALL |
| `ch.qos.logback.classic.Level` | OFF、ERROR、WARN、INFO、DEBUG、TRACE、ALL |
| `org.apache.log4j.Level` | OFF、FATAL、ERROR、WARN、INFO、DEBUG、TRACE、ALL |
| `org.apache.logging.log4j.Level` | OFF、FATAL、ERROR、WARN、INFO、DEBUG、TRACE、ALL |
| `org.slf4j.event.Level` | ERROR、WARN、INFO、DEBUG、TRACE |

若某后端使用非标准级别类，实现 `LoggingLevelsResolver` 自行扩展。

### 5. JMX 集成（`io.microsphere.logging.jmx`）

- `LoggingMXBeanAdapter`：把 `Logging` 实例适配为标准 `java.util.logging.LoggingMXBean`；实现
  `DelegatingWrapper`，通过 `getDelegate()` 暴露被包装对象，`equals`/`hashCode`/`toString` 均委托处理。
- `LoggingMXBeanRegistrar`：把每个已检测到的 `Logging` 后端注册为 MBean，JMX 域为 `io.microsphere.logging`，
  `ObjectName` 形如 `io.microsphere.logging:type=<后端名>`。

```java
import io.microsphere.logging.jmx.LoggingMXBeanRegistrar;

// 注册 classpath 上发现的全部后端
List<ObjectInstance> instances = LoggingMXBeanRegistrar.registerAll();

// 或显式注册某一个
ObjectInstance instance = LoggingMXBeanRegistrar.register(logging);

// 查看已注册的 MBean（查询模式：io.microsphere.logging:type=*）
Set<ObjectInstance> registered = LoggingMXBeanRegistrar.getRegisteredLoggingObjectInstances();
```

```java
// 测试用例证据 —— LoggingMXBeanRegistrarTest.java:59
assertEquals("io.microsphere.logging:type=Testing", objectName.toString());
```

在生产环境接入前需要了解的行为：

- `registerAll()` 返回**不可修改**列表，每个成功注册（或早已注册）的后端对应一项；注册失败的后端
  **既不产生条目也不抛异常**。测试中注册的 `ThrowableLogging` 其 `getName()` 会抛异常，
  `registerAll()` 依然正常返回一条记录。
- 注册是**幂等**的：先用 `isRegistered` 探测同名 `ObjectName`，命中则返回既有 `ObjectInstance` 并打印 warn，
  不会抛 `InstanceAlreadyExistsException`。
- JMX 侧只暴露 `java.util.logging.LoggingMXBean` 的四个操作
  （`getLoggerLevel`、`setLoggerLevel`、`getLoggerNames`、`getParentLoggerName`）。
  `getSupportedLoggingLevels()`、`getName()`、`getPriority()` **不在该接口中**，JMX 客户端无法调用。
- **没有反注册 API**，注册关系随 JVM 生命周期存在。
- `JavaLogging.getName()` 是 `"Java Logging"`（含空格），其 `ObjectName` 为
  `io.microsphere.logging:type=Java Logging`；部分 JMX 客户端在查询时需要加引号。
- `java.util.logging.LoggingMXBean` 自 Java 9 起已被标记废弃；适配器仍可用，但长期运维工具需留意。

## 后端优先级汇总

`io.microsphere.lang.Prioritized` 定义 `MAX_PRIORITY = Integer.MIN_VALUE`、`MIN_PRIORITY = Integer.MAX_VALUE`，
且 SPI 列表按 `getPriority()` **升序**排序，所以数值越小优先级越高：

| `loadAll()` 返回顺序 | 后端实现 | 所属模块 | 优先级数值 |
|----------------------|----------|----------|------------|
| 第 1 | `Log4j2Logging` | microsphere-log4j2 | `NORMAL_PRIORITY - 5`（`-5`） |
| 第 2 | `Log4jLogging` | microsphere-log4j | `NORMAL_PRIORITY - 3`（`-3`） |
| 第 3 | `LogbackLogging` | microsphere-logback | `NORMAL_PRIORITY`（`0`） |
| 第 4 | `JavaLogging` | microsphere-java-logging | `NORMAL_PRIORITY + 10`（`10`） |

`LoggingUtils.load()` 返回该有序列表的首元素，因此四个适配器齐全时选中的是 **Log4j2**，而不是 JUL。
若要指定后端，请用 `loadAll()` 并按 `getName()` 过滤：

```java
Logging logging = LoggingUtils.loadAll().stream()
        .filter(l -> "Logback".equals(l.getName()))
        .findFirst()
        .orElseThrow();
```

## 相关文档

- [Java Logging（JUL）适配](./java-logging.md)
- [Logback 适配](./logback.md)
- [Log4j 适配](./log4j.md)
- [Log4j2 适配](./log4j2.md)
- [测试支持](./logging-test.md)
