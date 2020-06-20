package com.example.android.politicalpreparedness.detail

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.CivicsApi

class ElectionDetailViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ElectionDetailViewModel(CivicsApi.retrofitService,ElectionDatabase.getInstance(context).electionDao) as T
    }


}