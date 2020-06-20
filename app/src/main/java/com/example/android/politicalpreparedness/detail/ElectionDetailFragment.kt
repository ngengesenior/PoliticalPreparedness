package com.example.android.politicalpreparedness.detail

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.example.android.politicalpreparedness.databinding.FragmentElectionDetailBinding
import com.example.android.politicalpreparedness.election.ElectionsViewModelFactory

class ElectionDetailFragment:Fragment() {


    private val args: ElectionDetailFragmentArgs by navArgs()
    val detailViewModel by viewModels<ElectionDetailViewModel> { ElectionDetailViewModelFactory(activity as Context) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentElectionDetailBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        val election = args.election

        detailViewModel.election = election
        detailViewModel.getVoterInfoResponse(electionId = election.id,address = election.division.state)
        binding.electionName.text = election.name
        binding.electionDateTextview.text = election.electionDay.toString()
        detailViewModel.voterInfoResponse.observe(viewLifecycleOwner, Observer { voterInfoResponse ->
            voterInfoResponse.state?.let { states ->
                if (states.isNotEmpty()) {
                    val administrationBody = states[0].electionAdministrationBody
                    administrationBody.ballotInfoUrl?.let {url ->
                        binding.ballotInformation.setOnClickListener {
                            detailViewModel.startElectionsInfo(requireContext(),url)
                        }
                    }

                    administrationBody.votingLocationFinderUrl?.let {url ->
                        binding.votingLocations.setOnClickListener {
                            detailViewModel.startElectionsInfo(requireContext(),url)
                        }

                    }
                }

            }

        })

        //val factory = ElectionDetailViewModelFactory(ElectionDatabase.getInstance(requireContext()).electionDao)
        //detailViewModel.election = election

        binding.viewModel = detailViewModel



        detailViewModel.verifyIfElectionIsSaved()
        return binding.root
    }
}