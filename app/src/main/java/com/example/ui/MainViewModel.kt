package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.CalculatorRepository
import com.example.data.FavoriteEntity
import com.example.data.HistoryEntity
import com.example.data.ThemeMode
import com.example.data.UserPreferences
import com.example.model.CalculatorModel
import com.example.model.CalculatorsRegistry
import com.example.notification.NotificationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CalculatorRepository
    val userPreferences: UserPreferences

    val historyList: StateFlow<List<HistoryEntity>>
    val favoritesList: StateFlow<List<FavoriteEntity>>
    val themeMode: StateFlow<ThemeMode>
    val notificationsEnabled: StateFlow<Boolean>

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<CalculatorModel>>(emptyList())
    val searchResults: StateFlow<List<CalculatorModel>> = _searchResults.asStateFlow()

    init {
        val dao = AppDatabase.getDatabase(application).calculatorDao()
        repository = CalculatorRepository(dao)
        userPreferences = UserPreferences(application)

        historyList = repository.allHistory.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        favoritesList = repository.allFavorites.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        themeMode = userPreferences.themeMode
        notificationsEnabled = userPreferences.notificationsEnabled
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        _searchResults.value = CalculatorsRegistry.searchCalculators(query)
    }

    fun saveCalculationHistory(calcName: String, summary: String, category: String = "", calcId: String = "") {
        viewModelScope.launch {
            val history = HistoryEntity(
                calcId = calcId,
                calcName = calcName,
                category = category,
                inputSummary = calcName,
                resultSummary = summary
            )
            repository.insertHistory(history)

            if (notificationsEnabled.value) {
                NotificationHelper.showCalculationNotification(
                    getApplication(),
                    calcName,
                    summary
                )
            }
        }
    }

    fun deleteHistoryItem(id: Long) {
        viewModelScope.launch {
            repository.deleteHistory(id)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }

    fun toggleFavorite(calcId: String, calcName: String, category: String) {
        viewModelScope.launch {
            val isFav = favoritesList.value.any { it.calcId == calcId }
            repository.toggleFavorite(calcId, calcName, category, isFav)
        }
    }

    fun isFavorite(calcId: String): Boolean {
        return favoritesList.value.any { it.calcId == calcId }
    }

    fun setThemeMode(mode: ThemeMode) {
        userPreferences.setThemeMode(mode)
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        userPreferences.setNotificationsEnabled(enabled)
    }
}
