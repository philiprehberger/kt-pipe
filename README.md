# kt-pipe

[![CI](https://github.com/philiprehberger/kt-pipe/actions/workflows/ci.yml/badge.svg)](https://github.com/philiprehberger/kt-pipe/actions/workflows/ci.yml)
[![Maven Central](https://img.shields.io/maven-central/v/com.philiprehberger/pipe)](https://central.sonatype.com/artifact/com.philiprehberger/pipe)

Function pipeline composition for Kotlin with named stages and error handling.

## Requirements

- Kotlin 1.9+ / Java 17+

## Installation

### Gradle (Kotlin DSL)

```kotlin
dependencies {
    implementation("com.philiprehberger:pipe:0.1.0")
}
```

### Gradle (Groovy)

```groovy
dependencies {
    implementation 'com.philiprehberger:pipe:0.1.0'
}
```

### Maven

```xml
<dependency>
    <groupId>com.philiprehberger</groupId>
    <artifactId>pipe</artifactId>
    <version>0.1.0</version>
</dependency>
```

## Usage

### Pipe Operator

```kotlin
import com.philiprehberger.pipe.pipe

val result = "  hello world  " pipe { it.trim() } pipe { it.uppercase() } pipe { it.length }
// result == 11
```

### Named Pipeline

```kotlin
import com.philiprehberger.pipe.*

val p = pipeline<String, Int> {
    stage("trim") { it.trim() }
    stage("parse") { it.toInt() }
    stage("double") { it * 2 }
    onError { stage, error -> println("Failed at $stage: $error") }
}

when (val result = p.execute("  21  ")) {
    is PipelineResult.Success -> println(result.value) // 42
    is PipelineResult.Failure -> println("Error in ${result.stageName}")
}
```

### Conditional Stages

```kotlin
val verbose = true
val p = pipeline<String, String> {
    stage("trim") { it.trim() }
    stageIf(verbose, "log") { println(it); it }
    stage("upper") { it.uppercase() }
}
```

### Pipeline Composition

```kotlin
val parse = pipeline<String, Int> { stage("parse") { it.toInt() } }
val classify = pipeline<Int, String> { stage("classify") { if (it >= 18) "adult" else "minor" } }

val combined = parse then classify
combined.execute("25") // Success("adult")
```

## API

| Class / Function | Description |
|------------------|-------------|
| `A.pipe(transform)` | Infix operator that pipes a value through a transformation |
| `pipeline { }` | DSL builder for creating a named-stage pipeline |
| `Pipeline.execute(input)` | Runs the pipeline and returns a `PipelineResult` |
| `PipelineBuilder.stage(name, transform)` | Adds a named transformation stage |
| `PipelineBuilder.stageIf(condition, name, transform)` | Adds a conditional stage |
| `PipelineBuilder.onError(handler)` | Registers an error callback |
| `PipelineResult.Success` | Successful pipeline result with output value |
| `PipelineResult.Failure` | Failed result with stage name and exception |
| `Pipeline.then(other)` | Composes two pipelines sequentially |

## Development

```bash
./gradlew test       # Run tests
./gradlew check      # Run all checks
./gradlew build      # Build JAR
```

## License

MIT
