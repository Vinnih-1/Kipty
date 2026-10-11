package io.github.vinnih.kipty.data.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import io.github.vinnih.kipty.data.settings.AppPreferencesRepository.Keys.HAS_SYNCED_KEY
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

data class AppSettings(
    val showTimestamp: Boolean,
    val receiveAlert: Boolean,
    val username: String,
    val profileIconPath: String,
    val profileIconUpdatedAt: Long
)

class AppPreferencesRepository @Inject constructor(private val dataStore: DataStore<Preferences>) {

    private object Keys {
        val SHOW_TIMESTAMP = booleanPreferencesKey("show_timestamp")
        val RECEIVE_ALERT = booleanPreferencesKey("receive_alert")
        val HAS_SYNCED_KEY = booleanPreferencesKey("has_synced")
        val USERNAME = stringPreferencesKey("username")
        val PROFILE_ICON_PATH = stringPreferencesKey("profile_icon_path")
        val PROFILE_ICON_UPDATED_AT = longPreferencesKey("profile_icon_updated_at")
        val HAS_POPULATED_DATABASE = booleanPreferencesKey("has_populated_database")
    }

    private val usernameFlow: Flow<String> = dataStore.data.map { preferences ->
        preferences[Keys.USERNAME] ?: ""
    }

    private val profileIconPathFlow: Flow<String> = dataStore.data.map { preferences ->
        preferences[Keys.PROFILE_ICON_PATH] ?: ""
    }
    private val showTimestampFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[Keys.SHOW_TIMESTAMP] ?: true
    }

    private val receiveAlertFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[Keys.RECEIVE_ALERT] ?: true
    }

    private val profileIconUpdatedAtFlow: Flow<Long> = dataStore.data.map { preferences ->
        preferences[Keys.PROFILE_ICON_UPDATED_AT] ?: 0L
    }

    val hasDatabasePopulatedFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[Keys.HAS_POPULATED_DATABASE] ?: false
    }

    val appSettingsFlow: Flow<AppSettings> = combine(
        showTimestampFlow,
        receiveAlertFlow,
        usernameFlow,
        profileIconPathFlow,
        profileIconUpdatedAtFlow
    ) { showTimestamp, receiveAlert, username, profileIconPath, profileIconUpdatedAt ->
        AppSettings(
            showTimestamp = showTimestamp,
            receiveAlert = receiveAlert,
            username = username,
            profileIconPath = profileIconPath,
            profileIconUpdatedAt = profileIconUpdatedAt
        )
    }

    suspend fun updateShowTimestamp(showTimestamp: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.SHOW_TIMESTAMP] = showTimestamp
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

    suspend fun updateUsername(username: String) {
        dataStore.edit { preferences ->
            preferences[Keys.USERNAME] = username
        }
    }

    suspend fun markDatabaseAsPopulated() {
        dataStore.edit { preferences ->
            preferences[Keys.HAS_POPULATED_DATABASE] = true
        }
    }

    suspend fun updateProfileIconPath(path: String) {
        dataStore.edit { preferences ->
            preferences[Keys.PROFILE_ICON_PATH] = path
            preferences[Keys.PROFILE_ICON_UPDATED_AT] = System.currentTimeMillis()
        }
    }
}
