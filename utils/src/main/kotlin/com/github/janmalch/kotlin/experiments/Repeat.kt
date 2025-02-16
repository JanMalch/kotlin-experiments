package com.github.janmalch.kotlin.experiments

fun String.repeatALot(n: Int): String {
    require(n >= 0) { "Count 'n' must be non-negative, but was $n." }
    return when (n) {
        0 -> ""
        1 -> this
        2 -> this + this
        3 -> this + this + this
        else -> when (length) {
            0 -> ""
            else -> {
                // https://github.com/jonschlinkert/repeat-string/blob/3914965fe08212b60cdbb32ab83ef5f8b1d5df47/index.js
                val max = length * n
                var result = ""
                var num = n
                var str = this
                while (max > result.length && num > 1) {
                    if (num % 2 == 1) {
                        result += str
                    }
                    num = num shr 1
                    str += str
                }

                result += str
                result.substring(0, max)
            }
        }
    }
}
