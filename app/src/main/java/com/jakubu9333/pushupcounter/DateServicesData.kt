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

    private var date = LocalDate.now()
    private val _data = MutableLiveData<DataHolder>()
    val data: LiveData<DataHolder> = _data
    private val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
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
        return date.format(formatter)==LocalDate.now().format(formatter)
    }


    override fun toString(): String {


        return date.format(formatter)
    }
}