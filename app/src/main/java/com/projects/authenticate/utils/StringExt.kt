package com.projects.authenticate

import android.R.attr.password
import java.util.regex.Matcher
import java.util.regex.Pattern


fun String.getSectionHeader(): Char {
    val firstLetter = get(0).toUpperCase()
    val pattern = Pattern.compile("[^A-Za-z]")
    val matcher = pattern.matcher(firstLetter.toString())
    return if (matcher.find()) {
        '#'
    } else {
        firstLetter
    }
}

fun String.getInt():Int{
    return if(isEmpty()){
        0
    }else{
        toInt()
    }
}

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

fun Boolean.getInt():Int{
    return if(this){
        0
    }else {
        1
    }
}

fun Boolean.getFilesValue():Int{
    return if(this){
        1
    }else {
        0
    }
}

fun String.getFirstTwoLetter():String{
    val filterWords =  trim().split(Regex(" ")).toMutableList().filter { it.isNotEmpty() }
    if(filterWords.size==1){
        return filterWords[0].trim().capitalize().first().toString()
    }else{
        return filterWords[0].trim().capitalize().first().toString()+filterWords[1].trim().capitalize().first().toString()
    }
}

fun Int.getMinutesHours(): String {
    val hours = this / 60
    val minutes = this % 60
    return if (hours == 0) {
        "$minutes Mins"
    } else {
        var hourText = "Hrs"
        if(hours==1){
            hourText = "Hr"
        }
        if(minutes==0){
            "$hours $hourText"
        }else{
            "$hours $hourText $minutes Mins"
        }
    }

}