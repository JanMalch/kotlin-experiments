package com.github.janmalch.kotlin.experiments

import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.Fork
import org.openjdk.jmh.annotations.Measurement
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State
import org.openjdk.jmh.annotations.Warmup
import java.util.concurrent.TimeUnit

private const val PLACEHOLDER_WITH_SEPARATOR = "(?),"

/*

Benchmark                             Mode  Cnt          Score           Error  Units
RepeatBenchmark.two_repeat           thrpt    3   28558737,136 ±   5325680,250  ops/s
RepeatBenchmark.two_repeatALot       thrpt    3  123398158,430 ± 155110219,330  ops/s

RepeatBenchmark.ten_repeat           thrpt    3   11567365,180 ±   2234166,653  ops/s
RepeatBenchmark.ten_repeatALot       thrpt    3   13610411,105 ±   4113721,056  ops/s

RepeatBenchmark.hundred_repeat       thrpt    3    1409161,406 ±    379187,075  ops/s
RepeatBenchmark.hundred_repeatALot   thrpt    3    4630536,155 ±    537744,886  ops/s

RepeatBenchmark.thousand_repeat      thrpt    3     142869,269 ±     44483,049  ops/s
RepeatBenchmark.thousand_repeatALot  thrpt    3     507835,573 ±    560460,446  ops/s

 */

@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 1)
@Measurement(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
class RepeatBenchmark {

    @Benchmark fun two_repeat(): String = PLACEHOLDER_WITH_SEPARATOR.repeat(2)
    @Benchmark fun ten_repeat(): String = PLACEHOLDER_WITH_SEPARATOR.repeat(10)
    @Benchmark fun hundred_repeat(): String = PLACEHOLDER_WITH_SEPARATOR.repeat(100)
    @Benchmark fun thousand_repeat(): String = PLACEHOLDER_WITH_SEPARATOR.repeat(1000)

    @Benchmark fun two_repeatALot(): String = PLACEHOLDER_WITH_SEPARATOR.repeatALot(2)
    @Benchmark fun ten_repeatALot(): String = PLACEHOLDER_WITH_SEPARATOR.repeatALot(10)
    @Benchmark fun hundred_repeatALot(): String = PLACEHOLDER_WITH_SEPARATOR.repeatALot(100)
    @Benchmark fun thousand_repeatALot(): String = PLACEHOLDER_WITH_SEPARATOR.repeatALot(1000)

}
