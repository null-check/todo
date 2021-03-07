package com.arjun.todo.data

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "PreferencesManager"

enum class SortOrder { BY_NAME, BY_DATE }

data class FilterPreferences(val sortOrder: SortOrder, val hideCompleted: Boolean)

@Singleton
class PreferencesManager @Inject constructor(@ApplicationContext private val context: Context) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

    val preferencesFlow = context.dataStore.data.catch { exception ->
        if (exception is IOException) {
            Log.e(TAG, "Error reading preferences: ", exception)
            emit(emptyPreferences())
        } else {
            throw exception
        }
    }.map { preferencesFlow ->
        val sortOrder =
            SortOrder.valueOf(preferencesFlow[PreferencesKeys.SORT_ORDER] ?: SortOrder.BY_DATE.name)
        val hideCompleted = preferencesFlow[PreferencesKeys.HIDE_COMPLETED] ?: false
        FilterPreferences(sortOrder, hideCompleted)
    }

    suspend fun updateSortOrder(sortOrder: SortOrder) {
        context.dataStore.edit { preferencesFlow ->
            preferencesFlow[PreferencesKeys.SORT_ORDER] = sortOrder.name
        }
    }

    suspend fun updateHideCompleted(hideCompleted: Boolean) {
        context.dataStore.edit { preferencesFlow ->
            preferencesFlow[PreferencesKeys.HIDE_COMPLETED] = hideCompleted
        }
    }

    private object PreferencesKeys {
        val SORT_ORDER = stringPreferencesKey("sort_order")
        val HIDE_COMPLETED = booleanPreferencesKey("hide_completed")
    }
}
