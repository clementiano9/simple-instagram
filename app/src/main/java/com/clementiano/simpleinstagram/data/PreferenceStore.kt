package com.clementiano.simpleinstagram.data

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.WorkerThread
import androidx.core.content.edit
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

private const val FILENAME = "com.clementiano.simpleinstagram.pref_file"
private const val PREF_USERNAME = "pref_username"
private const val PREF_LOGGED_IN = "pref_logged_in"
private const val PREF_AUTH_TOKEN = "pref_auth_token"
private const val PREF_FULLNAME = "pref_fullname"

class PreferenceStore(context: Context) {

    private val preferences: SharedPreferences by lazy { context.getSharedPreferences(FILENAME, Context.MODE_PRIVATE) }

    var isLoggedIn by BooleanPreference(preferences, PREF_LOGGED_IN, false)
    var auth by StringPreference(preferences, PREF_AUTH_TOKEN, null)
    var username by StringPreference(preferences, PREF_USERNAME, null)
    var fullname by StringPreference(preferences, PREF_FULLNAME, null)

    fun setUserLoggedIn(authToken: String) {
        isLoggedIn = true
        auth = authToken
    }

    fun logout() {
        isLoggedIn = false
        preferences.edit { clear() }
    }


    class BooleanPreference (
        private val preferences: SharedPreferences,
        private val name: String,
        private val defaultValue: Boolean
    ): ReadWriteProperty<Any, Boolean> {

        @WorkerThread
        override fun getValue(thisRef: Any, property: KProperty<*>): Boolean {
            return preferences.getBoolean(name, defaultValue)
        }

        override fun setValue(thisRef: Any, property: KProperty<*>, value: Boolean) {
            preferences.edit { putBoolean(name, value) }
        }
    }

    class StringPreference (
        private val preferences: SharedPreferences,
        private val name: String,
        private val defaultValue: String?
    ) : ReadWriteProperty<Any, String?> {

        @WorkerThread
        override fun getValue(thisRef: Any, property: KProperty<*>): String? {
            return preferences.getString(name, defaultValue)
        }

        override fun setValue(thisRef: Any, property: KProperty<*>, value: String?) {
            preferences.edit { putString(name, value) }
        }
    }

    class IntPreference (
        private val preferences: SharedPreferences,
        private val name: String,
        private val defaultValue: Int
    ) : ReadWriteProperty<Any, Int> {

        @WorkerThread
        override fun getValue(thisRef: Any, property: KProperty<*>): Int {
            return preferences.getInt(name, defaultValue)
        }

        override fun setValue(thisRef: Any, property: KProperty<*>, value: Int) {
            preferences.edit { putInt(name, value) }
        }
    }
}