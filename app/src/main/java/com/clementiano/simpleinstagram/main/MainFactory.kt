package com.clementiano.simpleinstagram.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.clementiano.simpleinstagram.data.PreferenceStore

class MainFactory (private val preferenceStore: PreferenceStore): ViewModelProvider.Factory {
    /**
     * Creates a new instance of the given `Class`.
     *
     *
     *
     * @param modelClass a `Class` whose instance is requested
     * @param <T>        The type parameter for the ViewModel.
     * @return a newly created ViewModel
    </T> */
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MainViewModel(preferenceStore) as T
    }

}