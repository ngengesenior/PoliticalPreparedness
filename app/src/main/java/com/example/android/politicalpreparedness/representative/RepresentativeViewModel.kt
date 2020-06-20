package com.example.android.politicalpreparedness.representative

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.network.CivicsApiService
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.model.Representative
import com.example.android.politicalpreparedness.utils.ElectionsApiStatus
import kotlinx.coroutines.launch
import java.lang.Exception

class RepresentativeViewModel(private val civicsApiService: CivicsApiService): ViewModel() {

    private val _status = MutableLiveData<ElectionsApiStatus>()
    val status: LiveData<ElectionsApiStatus>
        get() = _status

    //TODO: Establish live data for representatives and address

    private val _representatives = MutableLiveData<List<Representative>>()
    val representatives:LiveData<List<Representative>>
    get() = _representatives

    private val _address = MutableLiveData<Address>()
    val address:LiveData<Address>
    get() = _address

    //TODO: Create function to fetch representatives from API from a provided address

    fun fetchRepresentatives(address: Address) {
        _status.value = ElectionsApiStatus.LOADING

        viewModelScope.launch {
            val representativesDeferred = civicsApiService.getRepresentatives(address.toFormattedString())
            try {
                val (offices,officials) = representativesDeferred.await()
                _representatives.value = offices.flatMap { office -> office.getRepresentatives(officials) }
                _status.value = ElectionsApiStatus.DONE
            } catch (ex:Exception) {
                _status.value = ElectionsApiStatus.ERROR
                _representatives.value = arrayListOf()
            }
        }



    }

    fun bindAddress(anAddress: Address?) {
        _address.value = anAddress
    }

    /**
     *  The following code will prove helpful in constructing a representative from the API. This code combines the two nodes of the RepresentativeResponse into a single official :

    val (offices, officials) = getRepresentativesDeferred.await()
    _representatives.value = offices.flatMap { office -> office.getRepresentatives(officials) }

    Note: getRepresentatives in the above code represents the method used to fetch data from the API
    Note: _representatives in the above code represents the established mutable live data housing representatives

     */

    //TODO: Create function get address from geo location

    fun getAddressFromLatLng(latitude:Double,longitude:Double) {

    }

    //TODO: Create function to get address from individual fields
    fun makeAddress(line1:String,line2:String,city:String,state:String,zip:String) {
        _address.value = Address(line1=line1,line2 = line2,city = city,state = state,zip = zip)
    }

}
