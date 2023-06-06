package com.example.bttesting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.example.bttesting.databinding.HomeFragmentBinding

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = DataBindingUtil.inflate<HomeFragmentBinding>(inflater, R.layout.home_fragment, container, false)

        binding.patientIconRelativeHome.setOnClickListener {
            it.findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToConnectFragment())
        }
        binding.helpIconRelativeHome.setOnClickListener {
            it.findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToHelpFragment())
        }
        binding.folderIconRelativeHome.setOnClickListener {
            it.findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToHistoricalFragment())
        }
        binding.settingsIconRelativeHome.setOnClickListener {
            it.findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToSettingsFragment())
        }

        return binding.root
    }

}