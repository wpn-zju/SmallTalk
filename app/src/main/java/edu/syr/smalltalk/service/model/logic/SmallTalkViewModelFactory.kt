package edu.syr.smalltalk.service.model.logic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SmallTalkViewModelFactory(private val application: SmallTalkApplication) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SmallTalkViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SmallTalkViewModel(application, application.repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}