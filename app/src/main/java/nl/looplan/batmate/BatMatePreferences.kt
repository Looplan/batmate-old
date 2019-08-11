package nl.looplan.batmate

import android.content.Context
import android.content.SharedPreferences

object BatMatePreferences {

    private const val PREFERENCES_KEY = "BatMate"

    lateinit var preferences : SharedPreferences

    private const val INTRO_FINISHED_BY_USER_BEFORE_KEY = "INTRO_FINISHED_BY_USER_BEFORE"

    fun initialize(application: BatMateApplication) {
        preferences = application.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE)
    }

    fun isIntroFinishedByUserBefore() : Boolean {
        return preferences.getBoolean(INTRO_FINISHED_BY_USER_BEFORE_KEY, false)
    }

    fun setIntroFinishedByUserBefore(value : Boolean) {
        preferences.edit().putBoolean(INTRO_FINISHED_BY_USER_BEFORE_KEY, value).apply()
    }

}