package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.network.models.Address
import com.google.android.gms.location.*
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import java.util.*

class DetailFragment : Fragment() {
    private lateinit var fragmentContext: Context

    private val fusedLocationClient:FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    private val representativeViewModel:RepresentativeViewModel by viewModels {
        RepresentativeViewModelFactory()
    }
    private lateinit var binding:FragmentRepresentativeBinding
    lateinit var address1:String
    lateinit var address2:String
    lateinit var cityString:String
    lateinit var zipString:String
    lateinit var stateString:String
    companion object {
        //TODO: Add Constant for Location request
        val locationRequest = LocationRequest()
        const val LOCATION_REQUEST_PERMISSION = 1000

    }

    //TODO: Declare ViewModel

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {


        //TODO: Establish bindings
        binding = FragmentRepresentativeBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.viewModel = representativeViewModel
        fragmentContext = requireActivity()


        val states = resources.getStringArray(R.array.states)
        val adapter = ArrayAdapter(requireActivity(),android.R.layout.simple_spinner_item,states)
        binding.state.adapter = adapter
        //TODO: Define and assign Representative adapter

        //TODO: Populate Representative adapter

        //TODO: Establish button listeners for field and location search
        binding.buttonSearch.setOnClickListener {
            hideKeyboard()
            binding.apply {
                address1 = addressLine1.text.toString()
                address2 = addressLine2.text.toString()
                cityString = city.text.toString()
                zipString = zip.text.toString()
                stateString = state.selectedItem.toString()
                representativeViewModel.makeAddress(address1,address2,cityString,stateString,zipString)
            }
        }

        binding.buttonLocation.setOnClickListener {
            if (checkLocationPermissions()) {
                getLocation()
            }
        }

        setUpListAdapter()

        representativeViewModel.address.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            representativeViewModel.fetchRepresentatives(it)
        })

        return binding.root

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        //TODO: Handle location permission result to get location on permission granted
        if (requestCode == LOCATION_REQUEST_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted
                getLocation()
            } else {
                Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
            }

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun checkLocationPermissions(): Boolean {
        return if (isPermissionGranted()) {
            true
        } else {
            //TODO: Request Location permissions
            requestForLocationPermission()
            false
        }
    }

    private fun isPermissionGranted() : Boolean {
        //TODO: Check if permission is already granted and return (true = granted, false = denied/other)
        return ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestForLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),Manifest.permission.ACCESS_FINE_LOCATION)) {
            AlertDialog.Builder(requireContext())
                    .setTitle("Location permission needed")
                    .setMessage("Location permissions are needed to get your address")
                    .setPositiveButton("OK") { dialog, which -> ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST_PERMISSION)}
                    .setNegativeButton("Cancel") { dialog, which -> dialog.dismiss()}
                    .create()
                    .show()
        } else {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST_PERMISSION)
            //Toast.makeText(requireContext(),"Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setUpListAdapter() {
        binding.representativesList.adapter = RepresentativeListAdapter()
    }

    @Suppress("MissingPermission")
    private fun getLocation() {
        //TODO: Get location from LocationServices
        //TODO: The geoCodeLocation method is a helper function to change the lat/long location to a human readable street address

        locationRequest.apply {
            interval = 1000
            fastestInterval = 3000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, object : LocationCallback() {
            override fun onLocationResult(result: LocationResult?) {
                super.onLocationResult(result)
                if (result != null && result.locations.size > 0) {
                    val address = geoCodeLocation(result.locations[result.locations.lastIndex])
                    binding.address = address
                    representativeViewModel.bindAddress(address)

                }
            }

        }, Looper.getMainLooper())


    }

    private fun geoCodeLocation(location: Location): Address {
        val geocoder = Geocoder(fragmentContext, Locale.getDefault())
        return geocoder.getFromLocation(location.latitude, location.longitude, 1)
                .map { address ->
                    Address(address.thoroughfare, address.subThoroughfare, address.locality, address.adminArea, address.postalCode)
                }
                .first()
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0)
    }

}
