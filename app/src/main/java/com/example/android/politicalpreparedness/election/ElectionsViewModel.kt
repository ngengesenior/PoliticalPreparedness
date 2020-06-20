package com.example.android.politicalpreparedness.election

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.CivicsApiService
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.utils.ElectionsApiStatus
import kotlinx.coroutines.*
import kotlin.Exception


//TODO: Construct ViewModel and provide election datasource
class ElectionsViewModel(private val civicsApiService: CivicsApiService, private val dao: ElectionDao) : ViewModel() {

    val isSaved:MutableLiveData<Boolean> = MutableLiveData(false)
    lateinit var election:Election

    private val viewmodelJob = Job()
    private val coroutineScope = CoroutineScope(viewmodelJob + Dispatchers.Main)

    //TODO: Create live data val for upcoming elections
    private val _upComingElections = MutableLiveData<List<Election>>()
    val upcomingElections: LiveData<List<Election>>
        get() = _upComingElections

    //TODO: Create live data val for saved elections
    private val _savedElections = MutableLiveData<List<Election>>()
    val savedElections: LiveData<List<Election>>
        get() = _savedElections

    private val _status = MutableLiveData<ElectionsApiStatus>()
    val status: LiveData<ElectionsApiStatus>
        get() = _status

    private val _navigateToElectionDetails = MutableLiveData<Election>()
    val navigateToElectionDetails:LiveData<Election>
    get() = _navigateToElectionDetails

    fun verifyIfElectionIsSaved() {
        viewModelScope.launch(Dispatchers.IO) {
            val electionFromDb = dao.getElection(election.id)
            isSaved.postValue(electionFromDb != null)
        }
    }

    fun saveElection() {
        Log.d("SAVING", "saveElection: ")
        viewModelScope.launch {
            if (isSaved.value!!){
                dao.deleteById(election.id)
                Log.d("SAVING", "saveElection: Deleted")
            } else {
                dao.insert(election)
                Log.d("SAVING", "saveElection:inserted ")
            }
            isSaved.value = !isSaved.value!!
        }
    }


    //TODO: Create val and functions to populate live data for upcoming elections from the API and saved elections from local database

    fun getUpcomingElections() {
        _status.value = ElectionsApiStatus.LOADING
        viewModelScope.launch {
            val getElectionsDeferred = civicsApiService.getElectionsAsync()
            try {
                val result = getElectionsDeferred.await()
                _upComingElections.value = result.elections
                _status.value = ElectionsApiStatus.DONE
                Log.d(ElectionsViewModel::class.java.simpleName, "${result.elections}")

            } catch (ex: Exception) {
                _status.value = ElectionsApiStatus.ERROR
                _upComingElections.value = ArrayList()
                Log.d(ElectionsViewModel::class.java.simpleName, "getUpcomingElections: Error ${ex.message} ")

            }
        }
    }

    fun getSavedElections() {
        viewModelScope.launch {
            _savedElections.value = dao.getElections()
        }
    }
    //TODO: Create functions to navigate to saved or upcoming election voter info



    override fun onCleared() {
        super.onCleared()
        viewmodelJob.cancel()
    }

}

