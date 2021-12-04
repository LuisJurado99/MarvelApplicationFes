package developer.unam.marvelapplication.utils

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences

class ShareUtil(private val activity: Activity) {
    private val SHARE_PREF_FILE="MarvAppByLJ"

    private val share:SharedPreferences
    get() = activity.getSharedPreferences(SHARE_PREF_FILE,Context.MODE_PRIVATE)

    fun setRemember(value:Boolean) = share.edit().putBoolean("remember",value).apply()

    fun getRemember():Boolean=share.getBoolean("remember",false)

    fun clear()=share.edit().clear().apply()


}