package com.byted.myfitafy.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.byted.myfitafy.model.DailyTip
import com.byted.myfitafy.model.WeeklyItem
import com.byted.myfitafy.repository.ClothingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: ClothingRepository
) : ViewModel() {

    // On utilise mutableStateOf pour que Compose réagisse aux changements
    var dailyTip = mutableStateOf<DailyTip?>(null)
        private set
    var weeklyCollection = mutableStateOf<List<WeeklyItem>>(emptyList())
        private set

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            dailyTip.value = repository.getDailyTip()
            weeklyCollection.value = repository.getWeeklyCollection()
        }
    }
}