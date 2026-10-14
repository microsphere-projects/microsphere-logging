# Microsphere Logging — 文档总目录

[English version](../README.md)

`io.github.microsphere-projects:microsphere-logging` 的文档，按 **文档类型** → **分支（文档集）** → **语言** 三级组织。
英文为默认语言，中文镜像存放在同级的 `zh/` 目录中，文件名完全一致。

---

## 文档类型

| 文档 | 内容 | 入口 |
|------|------|------|
| **用户文档（User Guide）** | 各模块 API 说明、取自测试用例的可运行代码示例、版本信息与版本兼容范围 | [user-guide/](../user-guide/zh/README.md) |
| **开发者文档（Developer Guide）** | 架构设计、源码分析、设计模式，以及所有复杂流程的流程图 | [developer-guide/](../developer-guide/zh/README.md) |

## 文档集（分支）

每个文档集只来源于一个长期分支，并拥有独立目录：

| 分析分支 | 目录名 | 状态 |
|----------|--------|------|
| `release` | `current` | 可用——即当前维护线（`0.2.3-SNAPSHOT`） |
| `release-1.x` | `1.x` | **无文档**——本仓库中不存在该分支，因此不生成 `1.x` 文档集 |

## 目录结构

```
docs/
├── README.md                     ← 英文总目录
├── zh/README.md                  ← 你在这里（中文总目录）
├── user-guide/
│   ├── README.md                 ← 用户文档索引（英文）
│   ├── zh/README.md              ← 用户文档索引（中文）
│   └── current/                  ← `release` 分支文档集
│       ├── *.md                  ← 英文，每个 Maven 模块一篇
│       └── zh/*.md               ← 中文镜像，文件名一致
└── developer-guide/
    ├── README.md
    ├── zh/README.md
    └── current/
        ├── *.md
        └── zh/*.md
```

## 全套文档共用的版本事实

| 项目 | 值 |
|------|-----|
| 分析分支 | `release` |
| 工作区版本 | `0.2.3-SNAPSHOT`（`pom.xml` 中的 `revision` 属性） |
| 最新已发布版本 | `0.2.2` |
| Java 基线 | 8（继承自外部父工程 `microsphere-build`） |
| CI JDK 矩阵 | 8、11、17、21、25（`.github/workflows/maven-build.yml`） |
| 构建父工程 | `io.github.microsphere-projects:microsphere-build` `0.3.17` |

文档中所有关于行为的描述均可追溯到源文件、测试用例或 POM 的某一行，引用形式为 `File.java:123-128`。
