package com.jakubu9333.pushupcounter.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow


/**
 *
 * @author Jakub Uhlarik
 */
@Dao
interface PushUpsDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertPushUps(pushUps: PushUps)


    @Query("Select * from pushUps")
    fun getAll():Flow<List<PushUps>>


    @Query("Select * from pushUps where year=:year and day=:day and month=:month limit 1")
    fun getPushUpsByDay(day:Int,month:Int,year:Int): Flow<PushUps>

    @Update
    suspend fun update(pushUps: PushUps )
}