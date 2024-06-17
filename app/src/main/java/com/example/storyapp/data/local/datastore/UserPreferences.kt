package com.example.storyapp.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "sessions")

class UserPreferences private constructor(private val dataStore: DataStore<Preferences>){

    fun getSession(): Flow<String?>{
        return dataStore.data.map { preferences->
            preferences[TOKEN_KEY]
        }
    }

    suspend fun saveSession(token: String){
        dataStore.edit { preferences->
            preferences[TOKEN_KEY] = token
        }
    }

    suspend fun logout(){
        dataStore.edit { preferences->
            preferences.clear()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreferences? = null

        val TOKEN_KEY = stringPreferencesKey("token")

        fun getInstance(dataStore: DataStore<Preferences>): UserPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}