package com.projects.authenticate.utils

import android.R.attr.password
import java.util.regex.Matcher
import java.util.regex.Pattern


fun String.containsDigit():Boolean{
    val regex = "([0-9])"
    val p = Pattern.compile(regex)
    val input = this
    val m: Matcher = p.matcher(input)
    return m.find()
}

fun String.containsLowerCase():Boolean{
    val regex = "([a-z])"
    val p = Pattern.compile(regex)
    val m: Matcher = p.matcher(this)
    return m.find()
}

fun String.containsUpperCase():Boolean{
    val regex = "([A-Z])"
    val p = Pattern.compile(regex)
    val m: Matcher = p.matcher(this)
    return m.find()
}

fun String.containsSpecialCharacter():Boolean{
    val regex = "(?=.*[!_@#\$%^;:'\\[.\\]{}~`*|&+=\"<>/()-/\\\\])"
    val p = Pattern.compile(regex)
    val m: Matcher = p.matcher(this)
    return m.find()
}


