package io.github.vinnih.kipty.data.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import io.github.vinnih.kipty.data.settings.AppPreferencesRepository.Keys.HAS_SYNCED_KEY
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

data class AppSettings(
    val showTimestamp: Boolean,
    val minimumThreads: Int,
    val receiveAlert: Boolean
)

class AppPreferencesRepository @Inject constructor(private val dataStore: DataStore<Preferences>) {

    private object Keys {
        val SHOW_TIMESTAMP = booleanPreferencesKey("show_timestamp")
        val MINIMUM_THREADS = intPreferencesKey("minimum_threads")
        val RECEIVE_ALERT = booleanPreferencesKey("receive_alert")
        val HAS_SYNCED_KEY = booleanPreferencesKey("has_synced")
    }

    private val showTimestampFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[Keys.SHOW_TIMESTAMP] ?: true
    }

    private val minimumThreadsFlow: Flow<Int> = dataStore.data.map { preferences ->
        preferences[Keys.MINIMUM_THREADS] ?: 2
    }

    private val receiveAlertFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[Keys.RECEIVE_ALERT] ?: true
    }

    val appSettingsFlow: Flow<AppSettings> = combine(
        showTimestampFlow,
        minimumThreadsFlow,
        receiveAlertFlow
    ) { showTimestamp, minimumThreads, receiveAlert ->
        AppSettings(showTimestamp, minimumThreads, receiveAlert)
    }

    suspend fun updateShowTimestamp(showTimestamp: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.SHOW_TIMESTAMP] = showTimestamp
        }
    }

    suspend fun updateMinimumThreads(minimumThreads: Int) {
        dataStore.edit { preferences ->
            preferences[Keys.MINIMUM_THREADS] = minimumThreads
        }
    }

    suspend fun updateReceiveAlert(receiveAlert: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.RECEIVE_ALERT] = receiveAlert
        }
    }

    suspend fun runOnlyOnFirstSync(action: suspend () -> Unit) {
        dataStore.edit { prefs ->
            val hasSynced = prefs[HAS_SYNCED_KEY] ?: false
            if (!hasSynced) {
                action()
                prefs[HAS_SYNCED_KEY] = true
            }
        }
    }
}
