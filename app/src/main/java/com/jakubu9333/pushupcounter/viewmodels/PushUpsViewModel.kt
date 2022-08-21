package com.jakubu9333.pushupcounter.viewmodels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.jakubu9333.pushupcounter.database.PushUps
import com.jakubu9333.pushupcounter.database.PushUpsDao
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.time.LocalDate

/**
 *
 * @author Jakub Uhlarik
 */
class PushUpsViewModel(val dao:PushUpsDao) : ViewModel() {

    fun update(pushUps: PushUps,numToAdd: Int){
        viewModelScope.launch {
            val newPushUps=pushUps.copy(counter = pushUps.counter+numToAdd )
            dao.update(newPushUps)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun retrieveItem(date: LocalDate): LiveData<PushUps> {
        val day=date.dayOfMonth
        val month=date.monthValue
        val year=date.year
        return dao.getPushUpsByDay(day,month,year).asLiveData()
    }

   /*fun getAll(): Flow<List<PushUps>>{
        return dao.getAll()
    }*/



    @RequiresApi(Build.VERSION_CODES.O)
    fun insertCounter(count:Int, date: LocalDate){
        val pushups=PushUps(counter = count,day=date.dayOfMonth, month = date.monthValue, year = date.year)
        GlobalScope.launch {

            dao.insertPushUps(pushups)
        }
    }
}