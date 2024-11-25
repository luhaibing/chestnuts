@file:Suppress("unused")

package com.mercer.kernel.extension

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.eventFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.mapNotNull

/**
 * @author :Mercer
 * @Created on 2024/06/22.
 * @Description:
 *   Flow关联生命周期
 */
fun <T : Any?, E : Any> Flow<T>.target(vararg values: E, stateFlow: Flow<E>): Flow<T> {
    return combine(stateFlow) { element, value ->
        element to value
    }.mapNotNull { (element, value) ->
        if (value in values) {
            element
        } else {
            null
        }
    }
}

fun <T : Any?> Flow<T>.withStateAtLeast(value: Lifecycle.State, stateFlow: Flow<Lifecycle.State>): Flow<T> {
    return combine(stateFlow) { element, state ->
        element to state
    }.mapNotNull { (element, state) ->
        if (state.isAtLeast(value)) {
            element
        } else {
            null
        }
    }
}

fun <T : Any?> Flow<T>.withStateAtLeast(value: Lifecycle.Event, stateFlow: Flow<Lifecycle.State>): Flow<T> {
    return withStateAtLeast(value.targetState, stateFlow)
}

fun <T : Any?> Flow<T>.withStates(vararg values: Lifecycle.State, stateFlow: Flow<Lifecycle.State>): Flow<T> {
    return target(values = values, stateFlow = stateFlow)
}

fun <T : Any?> Flow<T>.withEvents(vararg values: Lifecycle.Event, eventFlow: Flow<Lifecycle.Event>): Flow<T> {
    return target(values = values, stateFlow = eventFlow)
}

///////////////////////////////////////////////////

fun <T : Any?> Flow<T>.withStateAtLeast(value: Lifecycle.State, lifecycle: Lifecycle): Flow<T> {
    return withStateAtLeast(value, lifecycle.currentStateFlow)
}

fun <T : Any?> Flow<T>.withStateAtLeast(value: Lifecycle.Event, lifecycle: Lifecycle): Flow<T> {
    return withStateAtLeast(value.targetState, lifecycle)
}

fun <T : Any?> Flow<T>.withStates(vararg values: Lifecycle.State, lifecycle: Lifecycle): Flow<T> {
    return withStates(*values, stateFlow = lifecycle.currentStateFlow)
}

fun <T : Any?> Flow<T>.withEvents(vararg values: Lifecycle.Event, lifecycle: Lifecycle): Flow<T> {
    return withEvents(*values, eventFlow = lifecycle.eventFlow)
}