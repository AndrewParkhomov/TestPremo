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

package me.dmdev.premo.navigation

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import me.dmdev.premo.LifecycleState
import me.dmdev.premo.PresentationModel

interface PmRouter {
    val pmStackChanges: Flow<List<PresentationModel>>
    val pmStack: List<PresentationModel>
    fun push(pm: PresentationModel)
    fun popTop()
}

internal class PmRouterImpl(
    private val hostPm: PresentationModel
): PmRouter {

    private var _pmStack = mutableListOf<PresentationModel>()
    override val pmStack: List<PresentationModel> get() = _pmStack

    private val _pmStackChanges = MutableSharedFlow<List<PresentationModel>>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    override val pmStackChanges: Flow<List<PresentationModel>> get() = _pmStackChanges

    override fun push(pm: PresentationModel) {
        pm.parentPm = hostPm
        _pmStack.add(pm)
        pm.moveLifecycleTo(hostPm.lifecycleState.value)
        notifyChanges()
    }

    override fun popTop() {
        _pmStack.removeLast().moveLifecycleTo(LifecycleState.DESTROYED)
        notifyChanges()
    }

    private fun notifyChanges() {
       _pmStackChanges.tryEmit(_pmStack)
    }
}