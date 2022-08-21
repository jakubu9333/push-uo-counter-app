package com.jakubu9333.pushupcounter.database


import androidx.room.Entity
import androidx.room.PrimaryKey


/**
 *
 * @author Jakub Uhlarik
 */

@Entity(tableName = "pushUps")
data class PushUps(
    var counter:Int=0,
    @PrimaryKey(autoGenerate = true)
    var id: Int=0,
    var day:Int=0,
    var month:Int=0,
    var year: Int=0,
){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PushUps

        if (day != other.day) return false
        if (month != other.month) return false
        if (year != other.year) return false

        return true
    }

    override fun hashCode(): Int {
        var result = day
        result = 31 * result + month
        result = 31 * result + year
        return result
    }


}
