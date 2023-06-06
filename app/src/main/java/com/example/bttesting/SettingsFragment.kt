package com.example.bttesting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.findNavController
import com.example.bttesting.databinding.SettingsFragmentBinding

class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = DataBindingUtil.inflate<SettingsFragmentBinding>(inflater, R.layout.settings_fragment, container, false)

        //questi sono i listeners del menu laterale
        binding.patientLogSettings.setOnClickListener {
            this.findNavController().navigate(R.id.connectFragment)
        }
        binding.historicalLogoSettings.setOnClickListener {
            this.findNavController().navigate(R.id.historicalFragment)
        }
        binding.settingsLogoSettings.setOnClickListener {
            //this.findNavController().navigate(R.id.settingsFragment)
        }
        binding.helpLogoSettings.setOnClickListener {
            this.findNavController().navigate(R.id.helpFragment)
        }
        binding.backBtnSettings.setOnClickListener {
            this.findNavController().navigate(R.id.homeFragment)
        }

        binding.pairConstraintSettings.setOnClickListener {
            this.findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToPairFragment())
        }

        binding.languagesLayoutSettings.setOnClickListener {
            this.findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToLanguagesFragment())
        }

        return binding.root
    }

}