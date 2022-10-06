package com.jakubu9333.pushupcounter

import android.os.Build
import androidx.annotation.RequiresApi

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 *
 * @author Jakub Uhlarik
 */
@RequiresApi(Build.VERSION_CODES.O)
class DateServicesData() {

     var date = LocalDate.now()
    private val _data = MutableLiveData<DataHolder>()
    val data: LiveData<DataHolder> = _data

    init {
        _data.value=DataHolder()
        changeData()
    }

    fun addDay(){
        date=date.plusDays(1)
        changeData()
    }
    private fun changeData(){

        _data.value?.stringDate  = this.toString()
        _data.value?.today=isToday()
    }

    fun minusDay(){
        date=date.minusDays(1)
        changeData()
    }
    private fun isToday():Boolean{
        return date==LocalDate.now()
    }


    override fun toString(): String {
        val formatter = DateTimeFormatter.ofPattern("EEEE, dd.MM.yyyy")

        return date.format(formatter)
    }
}