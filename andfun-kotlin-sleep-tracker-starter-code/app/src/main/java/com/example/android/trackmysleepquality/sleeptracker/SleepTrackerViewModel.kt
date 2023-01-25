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

package com.example.android.trackmysleepquality.sleeptracker

import android.app.Application
import androidx.lifecycle.*
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.formatNights
import kotlinx.coroutines.*

/**
 * ViewModel for SleepTrackerFragment.
 */
class SleepTrackerViewModel(
        val database: SleepDatabaseDao,
        application: Application) : AndroidViewModel(application) {

        private var viewModelJob = Job()

        override fun onCleared() {
                super.onCleared()
                viewModelJob.cancel()
        }

        private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

        private var tonight = MutableLiveData<SleepNight?>()

        val nights = database.getAllNights()

        val nightsString = Transformations.map(nights) { nights ->
                formatNights(nights, application.resources)
        }

        val startButtonVisible = Transformations.map(tonight) {
                null == it
        }

        val stopButtonVisible = Transformations.map(tonight) {
                null != it
        }

        val clearButtonVisible = Transformations.map(nights) {
                it?.isNotEmpty()
        }

        private var _showSnackbarEvent = MutableLiveData<Boolean>()
        val showSnackbarEvent : LiveData<Boolean>
                get() = _showSnackbarEvent

        fun doneShowingSnackbar() {
                _showSnackbarEvent.value = false
        }

        private val _navigateToSleepQuality = MutableLiveData<SleepNight>()
        val navigateToSleepQuality: LiveData<SleepNight>
                get() = _navigateToSleepQuality

        fun doneNavigating(){
                _navigateToSleepQuality.value = null
        }

        // 자 문제는 갑자기 등장한 얘네....
        private val _navigateToSleepDataQuality = MutableLiveData<Long>()
        val navigateToSleepDataQuality
                get() = _navigateToSleepDataQuality

        fun onSleepNightClicked(id: Long) {
                _navigateToSleepDataQuality.value = id
        }

        fun onSleepDataQualityNavigated() {
                _navigateToSleepDataQuality.value = null
        }

        init {
            initializeTonight()
        }

        // old version
        private fun initializeTonight() {
                uiScope.launch {
                        tonight.value = getTonightFromDatabase()
                }
        }

//        // new version -> 이유는 모르겠는데 새로운 버전으로 viewModelScope를 이용하려니까 quality view -> tracker view nav 과정에서 exception 발생
//        private fun initializeTonight() {
//                viewModelScope.launch {
//                        tonight.value = getTonightFromDatabase()
//                }
//        }

        private suspend fun getTonightFromDatabase(): SleepNight? {
                return withContext(Dispatchers.IO) {
                        var night = database.getTonight()
                        if (night?.endTimeMilli != night?.startTimeMilli) {
                                night = null
                        }
                        night
                }
        }

        private suspend fun clear() {
                withContext(Dispatchers.IO) {
                        database.clear()
                }
        }

        private suspend fun update(night: SleepNight) {
                withContext(Dispatchers.IO) {
                        database.update(night)
                }
        }
        private suspend fun insert(night: SleepNight) {
                withContext(Dispatchers.IO) {
                        database.insert(night)
                }
        }

        fun onStartTracking(){
                uiScope.launch {
                        val newNight = SleepNight()
                        insert(newNight)
                        tonight.value = getTonightFromDatabase()
                }
        }
        fun onStopTracking(){
                uiScope.launch {
                        val oldNight = tonight.value ?: return@launch
                        oldNight.endTimeMilli = System.currentTimeMillis()
                        update(oldNight)

                        _navigateToSleepQuality.value = oldNight
                }
        }

        fun onClear() {
                uiScope.launch {
                        clear()
                        tonight.value = null
                        _showSnackbarEvent.value = true
                }
        }


}

