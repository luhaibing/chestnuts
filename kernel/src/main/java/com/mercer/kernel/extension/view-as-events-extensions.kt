package com.mercer.kernel.extension

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * @author :Mercer
 * @Created on 2025/04/13.
 * @Description:
 *   视图动作转事件的扩展
 */

/*
 * 点击事件
 */
fun <T> View.clickAsEvents(predicate: (Unit, View) -> T): Flow<T> = callbackFlow {
    setOnClickListener {
        trySend(predicate(Unit, this@clickAsEvents))
    }
    awaitClose { setOnClickListener(null) }
}

/*
 * 文本编辑的变化
 */
fun <T> EditText.textAsEvents(predicate: (CharSequence, EditText) -> T) = callbackFlow {
    val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(s: Editable) {
            trySend(predicate(s, this@textAsEvents))
        }
    }
    addTextChangedListener(textWatcher)
    awaitClose { removeTextChangedListener(textWatcher) }
}

/*
 * 焦点变化
 */
fun <T> View.focusAsEvents(predicate: (Boolean, View) -> T): Flow<T> = callbackFlow {
    val onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
        trySend(predicate(hasFocus, v))
    }
    setOnFocusChangeListener(onFocusChangeListener)
    awaitClose { setOnFocusChangeListener(null) }
}

///////////////////////////////////////////////////

fun View.clickAsEvents(): Flow<Unit> = clickAsEvents { _, _ -> }

fun EditText.textAsEvents(): Flow<CharSequence> = textAsEvents { charSequence, _ -> charSequence }

fun View.focusAsEvents(): Flow<Boolean> = focusAsEvents { hasFocus, _ -> hasFocus }