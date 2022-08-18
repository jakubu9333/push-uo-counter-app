package com.jakubu9333.pushupcounter

import androidx.databinding.BaseObservable

/**
 *
 * @author Jakub Uhlarik
 */
class DataHolder:BaseObservable() {
    var stringDate=""
        set(value) {
            field = value
            notifyChange()
        }
    var today=false
        set(value) {
            field = value
            notifyChange()
        }

}