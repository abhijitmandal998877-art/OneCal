package com.example.data

import kotlinx.coroutines.flow.Flow

class CalculatorRepository(private val dao: CalculatorDao) {
    val allHistory: Flow<List<HistoryEntity>> = dao.getAllHistory()
    val allFavorites: Flow<List<FavoriteEntity>> = dao.getAllFavorites()

    fun isFavorite(calcId: String): Flow<Boolean> = dao.isFavorite(calcId)

    suspend fun insertHistory(history: HistoryEntity) {
        dao.insertHistory(history)
    }

    suspend fun deleteHistory(id: Long) {
        dao.deleteHistoryById(id)
    }

    suspend fun clearHistory() {
        dao.clearAllHistory()
    }

    suspend fun toggleFavorite(calcId: String, calcName: String, category: String, currentFavorite: Boolean) {
        if (currentFavorite) {
            dao.removeFavorite(calcId)
        } else {
            dao.addFavorite(FavoriteEntity(calcId = calcId, calcName = calcName, category = category))
        }
    }

    suspend fun clearFavorites() {
        dao.clearAllFavorites()
    }
}
