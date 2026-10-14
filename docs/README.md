# Microsphere Logging — Documentation

[中文版](./zh/README.md)

Documentation for `io.github.microsphere-projects:microsphere-logging`, organized by **guide type**, then by
**branch** (document set), then by **language**. English is the default language; the Chinese mirror lives in a
parallel `zh/` directory with identical file names.

---

## Guides

| Guide | What it covers | Entry point |
|-------|----------------|-------------|
| **User Guide** | Per-module APIs, runnable code examples taken from the test cases, version information and compatibility ranges | [user-guide/](./user-guide/README.md) |
| **Developer Guide** | Architecture design, source-code analysis, design patterns, and flowcharts for every non-trivial flow | [developer-guide/](./developer-guide/README.md) |

## Document sets (branches)

Each document set is derived from exactly one long-lived branch, and gets its own directory:

| Branch analyzed | Directory | Status |
|-----------------|-----------|--------|
| `release` | `current` | Available — this is the maintained line (`0.2.3-SNAPSHOT`) |
| `release-1.x` | `1.x` | **Not documented** — the branch does not exist in this repository, so no `1.x` document set is produced |

## Layout

```
docs/
├── README.md                     ← you are here (English)
├── zh/README.md                  ← Chinese entry point
├── user-guide/
│   ├── README.md                 ← User Guide index (English)
│   ├── zh/README.md              ← User Guide index (Chinese)
│   └── current/                  ← `release` branch document set
│       ├── *.md                  ← English, one file per Maven module
│       └── zh/*.md               ← Chinese mirror, same file names
└── developer-guide/
    ├── README.md
    ├── zh/README.md
    └── current/
        ├── *.md
        └── zh/*.md
```

## Version facts used across the whole documentation set

| Item | Value |
|------|-------|
| Branch analyzed | `release` |
| Working-tree version | `0.2.3-SNAPSHOT` (`revision` property in `pom.xml`) |
| Latest published version | `0.2.2` |
| Java baseline | 8 (inherited from the external `microsphere-build` parent) |
| CI JDK matrix | 8, 11, 17, 21, 25 (`.github/workflows/maven-build.yml`) |
| Build parent | `io.github.microsphere-projects:microsphere-build` `0.3.17` |

Every statement about behavior in these documents is traceable to a source file, a test case, or a POM line,
cited in the form `File.java:123-128`.
