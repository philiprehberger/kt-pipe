package com.philiprehberger.pipe

/**
 * Pipes this value through [transform], returning the result.
 *
 * Enables a left-to-right function composition style:
 * ```
 * val result = 5 pipe { it * 2 } pipe { it + 1 }
 * // result == 11
 * ```
 *
 * @param transform the function to apply to this value
 * @return the result of applying [transform]
 */
public infix fun <A, B> A.pipe(transform: (A) -> B): B = transform(this)
