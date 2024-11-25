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
 * @Created on 2024/06/22.
 * @Description:
 *   视图动作转事件的扩展
 */

/*
 * 点击事件
 */
fun <T> View.click2event(predicate: (Unit, View) -> T): Flow<T> = callbackFlow {
    setOnClickListener {
        trySend(predicate(Unit, this@click2event))
    }
    awaitClose { setOnClickListener(null) }
}

/*
 * 文本编辑的变化
 */
fun <T> EditText.text2event(predicate: (CharSequence, EditText) -> T) = callbackFlow {
    val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(s: Editable) {
            trySend(predicate(s, this@text2event))
        }
    }
    addTextChangedListener(textWatcher)
    awaitClose { removeTextChangedListener(textWatcher) }
}

/*
 * 焦点变化
 */
fun <T> View.focus2event(predicate: (Boolean, View) -> T): Flow<T> = callbackFlow {
    val onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
        trySend(predicate(hasFocus, v))
    }
    setOnFocusChangeListener(onFocusChangeListener)
    awaitClose { setOnFocusChangeListener(null) }
}

///////////////////////////////////////////////////

fun View.click2event(): Flow<Unit> = click2event { _, _ -> }

fun EditText.text2event(): Flow<CharSequence> = text2event { charSequence, _ -> charSequence }

fun View.focus2event(): Flow<Boolean> = focus2event { hasFocus, _ -> hasFocus }
