package com.philiprehberger.pipe

/**
 * Composes two pipelines, creating a new pipeline that feeds the output
 * of this pipeline into [other].
 *
 * If the first pipeline fails, the second is never executed.
 *
 * ```
 * val parseAge = pipeline<String, Int> { stage("parse") { it.toInt() } }
 * val classify = pipeline<Int, String> { stage("classify") { if (it >= 18) "adult" else "minor" } }
 * val combined = parseAge then classify
 * ```
 *
 * @param other the pipeline to execute after this one
 * @return a new composed [Pipeline]
 */
infix fun <A, B, C> Pipeline<A, B>.then(other: Pipeline<B, C>): Pipeline<A, C> {
    return ComposedPipeline(this, other)
}

/**
 * A pipeline that is the composition of two sequential pipelines.
 */
internal class ComposedPipeline<A, B, C>(
    private val first: Pipeline<A, B>,
    private val second: Pipeline<B, C>
) : Pipeline<A, C>(emptyList(), null) {
    override fun execute(input: A): PipelineResult<C> {
        return when (val result = first.execute(input)) {
            is PipelineResult.Success -> second.execute(result.value)
            is PipelineResult.Failure -> PipelineResult.Failure(result.stageName, result.error)
        }
    }
}
