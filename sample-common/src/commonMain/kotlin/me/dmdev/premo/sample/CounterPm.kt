/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020-2021 Dmitriy Gorbunov (dmitriy.goto@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.dmdev.premo.sample

import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import me.dmdev.premo.PresentationModel
import me.dmdev.premo.action
import me.dmdev.premo.consumeBy
import me.dmdev.premo.state

class CounterPm : PresentationModel() {

    private val maxCount = 10

    val count = state(0)

    val plusButtonEnabled = state(false) {
        count.flow().map { it < maxCount }
    }

    val minusButtonEnabled = state(false) {
        count.flow().map { it > 0 }
    }

    val plus = action<Unit> {
        this.map { count.value + 1}
            .filter { it <= maxCount }
            .consumeBy(count)
    }

    val minus = action<Unit> {
        this.map { count.value - 1 }
            .filter { it >= 0 }
            .consumeBy(count)
    }
}