package me.imran.listjobs.helper
import android.content.Context
import android.content.SharedPreferences

fun getSharedPreferences(context: Context): SharedPreferences {
    return context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE)
}

fun saveInSharedPref(key: String, value: Any, context: Context) {
    val sharedPref = getSharedPreferences(context)
    with(sharedPref.edit()) {
        when (value) {
            is String -> putString(key, value)
            is Int -> putInt(key, value)
            is Boolean -> putBoolean(key, value)
            is Float -> putFloat(key, value)
            is Long -> putLong(key, value)
            else -> throw IllegalArgumentException("Unsupported type")
        }
        apply()
    }
}

fun getFromSharedPref(key: String, context: Context): Any? {
    val sharedPref = getSharedPreferences(context)
    return when (sharedPref.all[key]) {
        is String -> sharedPref.getString(key, null)
        is Int -> sharedPref.getInt(key, 0)
        is Boolean -> sharedPref.getBoolean(key, false)
        is Float -> sharedPref.getFloat(key, 0f)
        is Long -> sharedPref.getLong(key, 0L)
        else -> null
    }
}
fun clearSharedPref(context: Context){
    val sharedPref = getSharedPreferences(context)
    with(sharedPref.edit()) {
        clear()
        apply()
    }
}

fun clearFromSharedPref(key: String, context: Context) {
    val sharedPref = getSharedPreferences(context)
    with(sharedPref.edit()) {
        remove(key)
        apply()
    }
}