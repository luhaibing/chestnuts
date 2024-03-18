package com.mskj.mercer.core.tool

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mskj.mercer.core.model.NetResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

typealias MutableListLiveData<T> = MutableLiveData<List<T>>

typealias ListLiveData<T> = LiveData<List<T>>

typealias NetListResult<T> = NetResult<List<T>>

typealias MutableStateListFlow<T> = MutableStateFlow<List<T>>

typealias StateListFlow<T> = StateFlow<List<T>>