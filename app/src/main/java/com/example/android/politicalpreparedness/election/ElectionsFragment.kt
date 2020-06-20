package com.example.android.politicalpreparedness.election

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter

class ElectionsFragment : Fragment() {

    lateinit var savedAdapter: ElectionListAdapter
    lateinit var upcomingAdapter: ElectionListAdapter
    private lateinit var binding: FragmentElectionBinding
    //TODO: Declare ViewModel
    val viewModel: ElectionsViewModel by viewModels{
        ElectionsViewModelFactory(activity as Context)
    }
    private val itemDecoration: DividerItemDecoration by lazy {
        DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        //TODO: Add ViewModel values and create ViewModel
        //TODO: Add binding values
        binding = FragmentElectionBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        setUpAdapters()


        return binding.root

        //TODO: Link elections to voter info

        //TODO: Initiate recycler adapters

        //TODO: Populate recycler adapters

    }

    private fun setUpAdapters() {
        binding.savedList.adapter  = ElectionListAdapter(ElectionListAdapter.ElectionListener {
            val action = ElectionsFragmentDirections.actionElectionsFragmentToElectionDetailFragment(it)
            findNavController().navigate(action)

        })

        binding.upcomingList.adapter  = ElectionListAdapter(ElectionListAdapter.ElectionListener {
            val action = ElectionsFragmentDirections.actionElectionsFragmentToElectionDetailFragment(it)
            findNavController().navigate(action)
        })

        binding.savedList.addItemDecoration(itemDecoration)
        binding.upcomingList.addItemDecoration(itemDecoration)
    }

    override fun onResume() {
        super.onResume()
       viewModel.getSavedElections()
        viewModel.getUpcomingElections()

    }

    //TODO: Refresh adapters when fragment loads

}