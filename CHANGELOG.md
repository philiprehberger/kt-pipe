# Changelog
## 0.1.3 (2026-03-20)- Standardize README: fix title, badges, version sync, remove Requirements section

All notable changes to this library will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.1.2] - 2026-03-18

- Upgrade to Kotlin 2.0.21 and Gradle 8.12
- Enable explicitApi() for stricter public API surface
- Add issueManagement to POM metadata

## [Unreleased]

## [0.1.1] - 2026-03-18

- Fix CI badge and gradlew permissions

## [0.1.0] - 2026-03-17

### Added
- `pipe` infix operator for left-to-right function chaining
- `pipeline { }` DSL for building named-stage pipelines
- `stage()` for adding named transformation stages
- `stageIf()` for conditional stage execution
- `onError()` callback for error handling
- `PipelineResult` sealed class (Success/Failure) with stage-level error tracking
- `then` infix operator for composing pipelines sequentially
