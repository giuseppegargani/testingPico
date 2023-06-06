package com.example.bttesting

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.bttesting.databinding.ErrorDownloadingDbBinding
import com.example.bttesting.viewModels.BluetoothViewModel

class ErrorDownloadDialog: DialogFragment() {

    val btViewModel: BluetoothViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogo = Dialog(requireContext())
        dialogo.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogo.setCancelable(false)
        dialogo.setCanceledOnTouchOutside(false)
        val binding = DataBindingUtil.inflate<ErrorDownloadingDbBinding>(LayoutInflater.from(context), R.layout.error_downloading_db, null,false)
        dialogo.setContentView(binding.root)
        dialogo.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.relaunchDownDbError.setOnClickListener {
            dialogo.dismiss()
            btViewModel.lanciaSaveDb()
        }
        binding.liveDownDbError.setOnClickListener {
            dialogo.dismiss()
        }
        binding.homeDownDbError.setOnClickListener {
            this.findNavController().navigate(ErrorDownloadDialogDirections.actionErrorDownloadDialogToHomeFragment())
        }

        dialogo.show()
        return dialogo
    }
}