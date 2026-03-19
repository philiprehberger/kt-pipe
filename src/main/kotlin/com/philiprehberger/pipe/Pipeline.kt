package com.philiprehberger.pipe

/**
 * A composable pipeline of named processing stages.
 *
 * Each stage transforms the data sequentially. If any stage throws an exception,
 * execution stops and a [PipelineResult.Failure] is returned with the stage name
 * and the exception.
 *
 * @param A the input type
 * @param B the output type
 */
public open class Pipeline<A, B> internal constructor(
    private val stages: List<Stage<*, *>>,
    private val errorHandler: ((String, Throwable) -> Unit)?
) {
    /**
     * Executes the pipeline with the given [input].
     *
     * @param input the value to feed into the first stage
     * @return [PipelineResult.Success] with the final output, or [PipelineResult.Failure]
     *         if any stage throws an exception
     */
    @Suppress("UNCHECKED_CAST")
    public open fun execute(input: A): PipelineResult<B> {
        var current: Any? = input
        for (stage in stages) {
            try {
                val transform = stage.transform as (Any?) -> Any?
                current = transform(current)
            } catch (e: Throwable) {
                errorHandler?.invoke(stage.name, e)
                return PipelineResult.Failure(stage.name, e)
            }
        }
        return PipelineResult.Success(current as B)
    }

    internal data class Stage<T, R>(
        val name: String,
        val transform: (T) -> R
    )
}

/**
 * The result of executing a [Pipeline].
 *
 * @param T the output type of the pipeline
 */
public sealed class PipelineResult<out T> {
    /**
     * The pipeline completed successfully.
     *
     * @property value the final output value
     */
    public data class Success<T>(public val value: T) : PipelineResult<T>()

    /**
     * The pipeline failed at a specific stage.
     *
     * @property stageName the name of the stage that threw
     * @property error the exception that was thrown
     */
    public data class Failure(public val stageName: String, public val error: Throwable) : PipelineResult<Nothing>()
}

/**
 * Creates a [Pipeline] using the builder DSL.
 *
 * ```
 * val p = pipeline<String, Int> {
 *     stage("trim") { it.trim() }
 *     stage("parse") { it.toInt() }
 * }
 * val result = p.execute("  42  ")
 * ```
 *
 * @param block the builder configuration
 * @return a configured [Pipeline]
 */
public fun <A, B> pipeline(block: PipelineBuilder<A, B>.() -> Unit): Pipeline<A, B> {
    val builder = PipelineBuilder<A, B>()
    builder.block()
    return builder.build()
}

/**
 * Builder for constructing a [Pipeline] with named stages.
 *
 * @param A the input type of the pipeline
 * @param B the output type of the pipeline
 */
public class PipelineBuilder<A, B> {
    private val stages = mutableListOf<Pipeline.Stage<*, *>>()
    private var errorHandler: ((String, Throwable) -> Unit)? = null

    /**
     * Adds a named processing stage to the pipeline.
     *
     * @param name a descriptive name for this stage (used in error reporting)
     * @param transform the function to apply
     */
    public fun <T, R> stage(name: String, transform: (T) -> R): Unit {
        stages.add(Pipeline.Stage(name, transform))
    }

    /**
     * Adds a conditional stage that only executes when [condition] is true.
     * When the condition is false, the input passes through unchanged.
     *
     * @param condition whether to apply this stage
     * @param name a descriptive name for this stage
     * @param transform the function to apply when the condition is true
     */
    public fun <T> stageIf(condition: Boolean, name: String, transform: (T) -> T): Unit {
        if (condition) {
            stages.add(Pipeline.Stage(name, transform))
        }
    }

    /**
     * Registers an error handler that is called when a stage throws an exception.
     *
     * @param handler a callback receiving the stage name and the exception
     */
    public fun onError(handler: (String, Throwable) -> Unit): Unit {
        this.errorHandler = handler
    }

    internal fun build(): Pipeline<A, B> {
        return Pipeline(stages.toList(), errorHandler)
    }
}
