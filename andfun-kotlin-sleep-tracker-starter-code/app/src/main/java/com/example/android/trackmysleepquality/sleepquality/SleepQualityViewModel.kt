/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.trackmysleepquality.sleepquality

import android.app.Application
import androidx.lifecycle.*
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import kotlinx.coroutines.*

class SleepQualityViewModel(
    private val sleepNightKey: Long = 0L,
    val database: SleepDatabaseDao
) : ViewModel() {

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    private val _navigateToSleepTracker = MutableLiveData<Boolean?>()
    val navigateToSleepTracker: LiveData<Boolean?>
        get() = _navigateToSleepTracker

    fun doneNavigating() {
//        _navigateToSleepTracker.value = null
        _navigateToSleepTracker.value = false // 이게 맞는거 아냐?
    }

    // old version
    fun onSetSleepQuality(quality: Int) {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                val tonight = database.get(sleepNightKey) ?: return@withContext
                tonight.sleepQuality = quality
                database.update(tonight)
            }
            _navigateToSleepTracker.value = true
        }
    }

//    // new version  -> 이유는 모르겠는데 새로운 버전으로 viewModelScope를 이용하려니까 quality view -> tracker view nav 과정에서 exception 발생
//    // 특히 여기서... java.lang.IllegalStateException: Cannot access database on the main thread since it may potentially lock the UI for a long period of time.
//    fun onSetSleepQuality(quality: Int) {
//        viewModelScope.launch {
//            val tonight = database.get(sleepNightKey) ?: return@launch
//            tonight.sleepQuality = quality
//            database.update(tonight)
//            _navigateToSleepTracker.value = true
//        }
//    }

}