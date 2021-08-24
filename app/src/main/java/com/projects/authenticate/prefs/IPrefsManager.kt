package com.projects.authenticate.prefs

interface IPrefsManager {

    fun clearAll()
    fun setBool(key:String,value:Boolean)
    fun getBool(key:String):Boolean
    fun setString(key:String,value:String)
    fun getString(key:String):String
    fun setInt(key:String,value:Int)
    fun getInt(key:String):Int
    fun setDouble(key:String, value:Double)
    fun getDouble(key:String):Double
    fun setLong(key:String,value:Long)
    fun getLong(key:String):Long

}