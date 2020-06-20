package com.example.android.politicalpreparedness.detail

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.CivicsApiService
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import com.example.android.politicalpreparedness.utils.ElectionsApiStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ElectionDetailViewModel(private val civicsApiService: CivicsApiService, private val dataSource:ElectionDao):ViewModel() {
    val isSaved:MutableLiveData<Boolean> = MutableLiveData(false)
    lateinit var election:Election
    private val _voterInfo = MutableLiveData<VoterInfoResponse>()
    val voterInfoResponse:LiveData<VoterInfoResponse>
    get() = _voterInfo


    private val _status = MutableLiveData<ElectionsApiStatus>()
    val status: LiveData<ElectionsApiStatus>
        get() = _status

     fun verifyIfElectionIsSaved() {
        viewModelScope.launch(Dispatchers.IO){
            val electionFromDb = dataSource.getElection(election.id)
            isSaved.postValue(electionFromDb != null)
        }
    }

    fun getVoterInfoResponse(address:String,electionId:Int) {
        _status.value = ElectionsApiStatus.LOADING
        viewModelScope.launch {
            val voterInfoDeferred = civicsApiService.getVoterInfo(election.division.state,electionId)
            try {
                val result = voterInfoDeferred.await()
                _voterInfo.value = result
                Log.d(ElectionDetailViewModel::class.simpleName, "getVoterInfoResponse: ${result}")
                _status.value = ElectionsApiStatus.DONE


            } catch (ex: Exception) {
                _status.value = ElectionsApiStatus.ERROR

                Log.d(ElectionDetailViewModel::class.simpleName, "getVoterInfoResponse: Error ${ex.message}")

            }
        }
    }


    fun startElectionsInfo(context: Context,url:String) {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))

    }



    fun saveElection() {
        viewModelScope.launch {
            if (isSaved.value!!){
                dataSource.deleteById(election.id)
            } else {
                dataSource.insert(election)
            }
            isSaved.value = !isSaved.value!!
        }
    }

}