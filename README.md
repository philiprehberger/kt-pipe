# pipe

[![Tests](https://github.com/philiprehberger/kt-pipe/actions/workflows/publish.yml/badge.svg)](https://github.com/philiprehberger/kt-pipe/actions/workflows/publish.yml)
[![Maven Central](https://img.shields.io/maven-central/v/com.philiprehberger/pipe.svg)](https://central.sonatype.com/artifact/com.philiprehberger/pipe)
[![Last updated](https://img.shields.io/github/last-commit/philiprehberger/kt-pipe)](https://github.com/philiprehberger/kt-pipe/commits/main)

Function pipeline composition for Kotlin with named stages and error handling.

## Installation

### Gradle (Kotlin DSL)

```kotlin
implementation("com.philiprehberger:pipe:0.1.5")
```

### Maven

```xml
<dependency>
    <groupId>com.philiprehberger</groupId>
    <artifactId>pipe</artifactId>
    <version>0.1.5</version>
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

## Support

If you find this project useful:

⭐ [Star the repo](https://github.com/philiprehberger/kt-pipe)

🐛 [Report issues](https://github.com/philiprehberger/kt-pipe/issues?q=is%3Aissue+is%3Aopen+label%3Abug)

💡 [Suggest features](https://github.com/philiprehberger/kt-pipe/issues?q=is%3Aissue+is%3Aopen+label%3Aenhancement)

❤️ [Sponsor development](https://github.com/sponsors/philiprehberger)

🌐 [All Open Source Projects](https://philiprehberger.com/open-source-packages)

💻 [GitHub Profile](https://github.com/philiprehberger)

🔗 [LinkedIn Profile](https://www.linkedin.com/in/philiprehberger)

## License

[MIT](LICENSE)
