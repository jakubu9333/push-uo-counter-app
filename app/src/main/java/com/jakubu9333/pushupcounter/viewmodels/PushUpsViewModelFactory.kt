package com.jakubu9333.pushupcounter.viewmodels


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jakubu9333.pushupcounter.database.PushUpsDao

/**
 *
 * @author Jakub Uhlarik
 */
class PushUpsViewModelFactory (
    private val dataSource: PushUpsDao,
) : ViewModelProvider.Factory {


    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PushUpsViewModel::class.java)) {
            return PushUpsViewModel(dataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
