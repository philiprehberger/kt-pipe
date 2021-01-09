# Changelog

## 0.1.6 (2026-03-31)

- Standardize README to 3-badge format with emoji Support section
- Update CI checkout action to v5 for Node.js 24 compatibility
- Add GitHub issue templates, dependabot config, and PR template

## 0.1.5 (2026-03-22)

- Fix README compliance (badge label, installation format)

## 0.1.4 (2026-03-22)

- Standardize CHANGELOG format

## 0.1.3 (2026-03-20)

- Standardize README: fix title, badges, version sync, remove Requirements section

## 0.1.2 (2026-03-18)

- Upgrade to Kotlin 2.0.21 and Gradle 8.12
- Enable explicitApi() for stricter public API surface
- Add issueManagement to POM metadata

## 0.1.1 (2026-03-18)

- Fix CI badge and gradlew permissions

## 0.1.0 (2026-03-17)

### Added
- `pipe` infix operator for left-to-right function chaining
- `pipeline { }` DSL for building named-stage pipelines
- `stage()` for adding named transformation stages
- `stageIf()` for conditional stage execution
- `onError()` callback for error handling
- `PipelineResult` sealed class (Success/Failure) with stage-level error tracking
- `then` infix operator for composing pipelines sequentially
