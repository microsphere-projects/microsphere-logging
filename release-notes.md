# Release Notes

## v0.1.6

# Release Notes - Version 0.1.6

## New Features
- Added a workflow to sync branches from the upstream repository. ([a17afa8](https://example.com/commit/a17afa8))
- Introduced a matrix job for merging `main` into multiple branches. ([d586baa](https://example.com/commit/d586baa))

## Other Changes
- Upgraded `microsphere-java.version` to `0.2.7`. ([b93ea50](https://example.com/commit/b93ea50))
- Updated `maven-publish.yml` configuration. ([db19392](https://example.com/commit/db19392))
- Renamed wiki workflow and removed stale pages. ([aebec3f](https://example.com/commit/aebec3f))
- Fixed Java matrix setup and added necessary workflow permissions. ([fb35565](https://example.com/commit/fb35565))
- Bumped version to prepare for the next patch release. ([98b983a](https://example.com/commit/98b983a))

## v0.1.7

# Release Notes - Version 0.1.7

## Dependency Updates
- **microsphere-java**: Bumped to version `0.2.9`.  
- **Parent POM**: Upgraded to version `0.2.7`.  

## Build and Workflow Enhancements
- Updated Maven wrapper to version `3.9.14`.  
- Improved GitHub workflows for release notes generation and Java setup.  
- Normalized spacing in `maven-build.yml`.  

## Other Changes
- Routine merges from main branch into release to maintain consistency.  

---  
**Full Changelog**: [0.1.6...0.1.7](https://github.com/microsphere-projects/microsphere-logging/compare/0.1.6...0.1.7)  

**Full Changelog**: https://github.com/microsphere-projects/microsphere-logging/compare/0.1.6...0.1.7## v0.1.8

# Release Notes - Version 0.1.8

## Dependency Updates
- **Upgraded microsphere-java to version 0.3.0** for compatibility and feature enhancements.
- **Updated Maven wrapper to 3.9.15** using Aliyun mirror.

## Build and Workflow Enhancements
- **Switched to official Maven Central wrapper URL** to improve build stability.

## Other Changes
- Updated versioning scheme post-0.1.7 publication.
- Routine merges from `main` into release branches for alignment.

**Full Changelog**: https://github.com/microsphere-projects/microsphere-logging/compare/0.1.7...0.1.8## v0.1.9

# Release Notes for Version 0.1.9

## Dependency Updates
- **microsphere-java.version** updated to `0.3.1`. ([cfd6d5d](link))

## Build and Workflow Enhancements
- Merged `main` into `release`. [skip ci] ([c054e6a](link))
- Synced `release` back into `main`. [skip ci] ([06427cc](link))
- Updated version to prepare for next patch after `0.1.8`. ([a28c6ce](link))

---

**Full Changelog**: https://github.com/microsphere-projects/microsphere-logging/compare/0.1.8...0.1.9## v0.1.10

# Release Notes for v0.1.10

## New Features
- Introduced utility enhancements: renamed `LoggerUtils`, added Log4j2 utilities and layouts. ([30c76f6](#))

## Bug Fixes
- Improved `LogEvent` comparison by using full instant time. ([66aa10c](#))
- Enhanced `LogEvent` precision with nanosecond-level accuracy. ([ddd916e](#))

## Dependency Updates
- Removed `microsphere-java-core` dependency. ([320a12b](#))
- Upgraded parent and `microsphere-java` versions. ([749aae6](#))

## Build and Workflow Enhancements
- Introduced Maven wrapper and adjusted Maven cache strategy. ([5a91714](#), [c4e57bb](#))

## Test Improvements
- Removed redundant `InMemoryAppender` test. ([a22397e](#))

## Other Changes
- Routine merges from `main` to `release`. ([1bbfab1](#), [798c76b](#), [3fb0fd9](#), [ad053d0](#), [6897b75](#), [10a6c82](#))
- Bump version to prepare for next patch. ([024d93b](#)) 

---

**Full Changelog:** [v0.1.9...v0.1.10](#)

**Full Changelog**: https://github.com/microsphere-projects/microsphere-logging/compare/0.1.9...0.1.10## v0.1.11

# Release Notes - Version 0.1.11

## New Features
- Added JavaDoc with example usage to all main source classes and non-private methods. [#30]

## Documentation
- Updated README to include the DeepWiki badge in the User Guide.

## Test Improvements
- Added sleep in tests and switched to static `newBuilder` imports for improved consistency.

## Dependency Updates
- Bumped `microsphere-java` version to `0.3.3`.

## Build and Workflow Enhancements
- Multiple merges from `main` to `release` branch for keeping consistency. [skip ci]
- Bumped version to next patch after publishing 0.1.10.

---

**Note**: For full details of changes, refer to the individual commits in the repository.

**Full Changelog**: https://github.com/microsphere-projects/microsphere-logging/compare/0.1.10...0.1.11## v0.1.12

# Release Notes - Version 0.1.12

## New Features
- Introduced `LogEventComparatorTest` to enhance log event sorting functionality.  
- Added new tests for `SmartFileAppenderLayout` and `LoggingLevelParameterResolverTest`.

## Bug Fixes
- Fixed redundant code issues and improved exception handling across modules.  
- Adjusted `LoggerConfig` to eliminate redundant null checks for cleaner logic.

## Documentation
- Comprehensive rewrite of `README.md` with improved formatting and expanded content.  
- Updated maintainer details to reflect Microsphere Projects organization.

## Dependency Updates
- Bumped dependency on `microsphere-java` to version `0.3.4`.

## Test Improvements
- Enhanced test coverage by adding missing branches and scenarios.  
- Refactored test logic for consistency and readability.

## Build and Workflow Enhancements
- Integrated `.github` prompt templates for better collaboration.  
- Optimized integration with project branches by merging changes from `main` and cleaning up workflows.

## Other Changes
- Refactored code structure to replace `List.of()` with `Lists.ofList()` for better compatibility.  
- Rolled back certain changes in `LogbackUtils` and `getLoggerContext()` for stability.

---

**Full Changelog**: https://github.com/microsphere-projects/microsphere-logging/compare/0.1.11...0.1.12## v0.1.13

# Release Notes - Version 0.1.13

## Dependency Updates
- Bumped `microsphere-java.version` to `0.3.5`. ([bbd77e0](#))
- Updated parent POM version to `0.3.0`. ([9404fd3](#))

## Documentation
- Fixed formatting in the license section of the `README`. ([06dec31](#))

## Build and Workflow Enhancements
- Prepared next patch version after publishing `0.1.12`. ([5c95904](#))
- Merged `main` into `release`. ([074d727](#), [ed5a3b5](#))
- Merged `release` into `main`. ([df68331](#))

**Full Changelog**: https://github.com/microsphere-projects/microsphere-logging/compare/0.1.12...0.1.13## v0.1.14

# Release Notes - Version 0.1.14

## New Features
- **Refactoring:** Refactored collection constructors to use `ListUtils` and `MapUtils` factory methods for improved clarity and performance. (#37)

## Bug Fixes
- Fixed JavaDoc formatting issues and removed unused imports. (#36)  
- Removed unnecessary imports and eliminated duplicated line separators and trailing whitespace. (#34)

## Documentation
- Improved JavaDoc structure: moved "Example Usage" section above `@param` tags for better readability. (#36)  

## Dependency Updates
- Bumped `microsphere-java` dependency version to `0.3.6`.  

## Other Changes
- General code cleanup and maintenance tasks.  
- Integrated multiple main branch updates into the release branch.  

**Full Changelog**: https://github.com/microsphere-projects/microsphere-logging/compare/0.1.13...0.1.14## v0.1.15

# Release Notes - Version 0.1.15

## Dependency Updates
- Bumped `microsphere-java` to `0.3.7`.  
- Updated parent project version to `0.3.1`.

## Build and Workflow Enhancements
- Synced `main` with `release` branch.  
- Automated version bump to `0.1.15`.  

---  
No new features, bug fixes, or documentation changes in this release.

**Full Changelog**: https://github.com/microsphere-projects/microsphere-logging/compare/0.1.14...0.1.15## v0.1.16

# Release Notes for Version 0.1.16

## Dependency Updates
- Bumped `microsphere-java` dependency to `0.3.8`. ([5716bf3](#))
- Updated parent project version to `0.3.3`. ([25ba487](#))

## Build and Workflow Enhancements
- Merged `main` branch into release and vice versa to sync changes. ([8a3de12](#), [3ee339f](#), [dfa18fb](#))
- Bumped version to prepare for the next patch release after publishing `0.1.15`. ([0fbbb23](#))

**Full Changelog**: https://github.com/microsphere-projects/microsphere-logging/compare/0.1.15...0.1.16## v0.1.17

# Release Notes - Version 0.1.17

## Dependency Updates
- Updated `microsphere-java.version` to `0.3.9`.  
- Bumped parent version to `0.3.4`.  

## Build and Workflow Enhancements
- Merged `main` into `release` and `release` into `main`.  

## Other Changes
- Bumped version to the next patch after publishing `0.1.16`.  

---

*(Note: Skipped CI-related commits and merges without versioning or functional impact.)*

**Full Changelog**: https://github.com/microsphere-projects/microsphere-logging/compare/0.1.16...0.1.17## v0.1.18

# Release Notes - Version 0.1.18

## Dependency Updates
- Bumped `microsphere-java` version to `0.3.10`. ([7c19607](#))

## Build and Workflow Enhancements
- Merged `main` into `release`. ([e0a9a2e](#))
- Merged `release` into `main`. ([cb93a23](#))
- Bumped version to next patch after publishing `0.1.17`. ([77146d5](#)) 

---

*Full Changelog: Refer to repository commit history for detailed changes.*

**Full Changelog**: https://github.com/microsphere-projects/microsphere-logging/compare/0.1.17...0.1.18## v0.1.19

# Release Notes for Version 0.1.19

## Dependency Updates
- **microsphere-java**: Bumped to version `0.3.11` (2943dee).  
- **microsphere-build**: Updated parent version (6820168).  

## Build and Workflow Enhancements
- Merged `main` into `release` (69a3f3b, 4a9da60) and `release` into `main` (91722d5) [skip ci].
- Bumped version to prepare for the next patch release (0043103).

---

**Full Changelog**: https://github.com/microsphere-projects/microsphere-logging/compare/0.1.18...0.1.19