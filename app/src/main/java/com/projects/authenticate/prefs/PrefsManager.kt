package com.projects.authenticate.prefs

import android.content.SharedPreferences
import androidx.core.content.edit

class PrefsManager(private val prefs:SharedPreferences) : IPrefsManager {


    /**
     * Puts a key-value pair in shared prefs if doesn't exists,
     * otherwise updates value on given [key]
     */
    operator fun SharedPreferences.set(key: String, value: Any?) {
        when (value) {
            is String? -> prefs.edit {
                putString(key, value)
            }
            is Int -> prefs.edit {
                putInt(key, value)
            }
            is Boolean -> prefs.edit {
                putBoolean(key, value)
            }
            is Float -> prefs.edit {
                putFloat(key, value)
            }
            is Long -> prefs.edit {
                putLong(key, value)
            }
            else -> throw UnsupportedOperationException("Not yet implemented")
        }
    }

    /**
     * Finds value on given key.
     * [T] is the type of value
     * @param defaultValue optional default value will take following if [defaultValue] is not specified
     * null for strings,
     * false for bool and
     * -1 for numeric values (int, float, long)
     */
    inline operator fun <reified T : Any> SharedPreferences.get(key: String, defaultValue: T? = null): T? {
        return when (T::class) {
            String::class -> getString(key, defaultValue as? String) as T?
            Int::class -> getInt(key, defaultValue as? Int ?: -1) as T?
            Boolean::class -> getBoolean(key, defaultValue as? Boolean ?: false) as T?
            Float::class -> getFloat(key, defaultValue as? Float ?: -1f) as T?
            Long::class -> getLong(key, defaultValue as? Long ?: -1) as T?
            else -> throw UnsupportedOperationException("Not yet implemented")
        }
    }

    override fun clearAll() {
        prefs.edit{
            clear()
        }
    }

    override fun setBool(key: String, value: Boolean) {
        prefs[key] = value
    }

    override fun getBool(key:String): Boolean {
        return prefs[key] ?: false
    }

    override fun setString(key: String, value: String) {
        prefs[key] = value
    }

    override fun getString(key:String): String {
        return prefs[key] ?: ""
    }

    override fun setInt(key: String, value: Int) {
        prefs[key] = value
    }

    override fun getInt(key:String): Int {
        return prefs[key] ?: 0
    }

    override fun setDouble(key: String, value: Double) {
        prefs[key] = value
    }

    override fun getDouble(key:String): Double {
        return prefs[key] ?: 0.0
    }

    override fun setLong(key: String, value: Long) {
        prefs[key] = value
    }

    override fun getLong(key:String): Long {
        return prefs[key] ?: 0L
    }


}