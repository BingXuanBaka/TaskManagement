package com.bingxuan.taskmanagement.data

import java.util.*

fun parseDate(date: Date): String{
    val cal = Calendar.getInstance()
    cal.time = date
    val currentCal = Calendar.getInstance()
    val days = cal.get(Calendar.DAY_OF_YEAR) + (cal.get(Calendar.YEAR) * 365.25).toInt()
    val currentDays = currentCal.get(Calendar.DAY_OF_YEAR) + (currentCal.get(Calendar.YEAR) * 365.25).toInt()
    val parsedTime =
        (if(cal.get(Calendar.AM_PM) == 0) "上午 " else "下午 ") +
        "${if(cal.get(Calendar.HOUR) < 10) "0${cal.get(Calendar.HOUR)}" else cal.get(Calendar.HOUR)}:" +
        "${if(cal.get(Calendar.MINUTE) < 10) "0${cal.get(Calendar.MINUTE)}" else cal.get(Calendar.MINUTE)}"

    if (days - currentDays == 0){
        return "今天$parsedTime"
    }

    if (days - currentDays == -1){
        return "昨天$parsedTime"
    }

    if (days - currentDays == -2){
        return "前天$parsedTime"
    }

    if (days - currentDays == 1){
        return "明天$parsedTime"
    }

    if (days - currentDays == 2){
        return "后天$parsedTime"
    }

    return "${cal.get(Calendar.YEAR)}/${cal.get(Calendar.MONTH) + 1}/${cal.get(Calendar.DAY_OF_MONTH)} $parsedTime"
}