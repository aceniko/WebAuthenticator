package com.web.authenticator.ui.activation

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.web.authenticator.R
import com.web.authenticator.communication.contracts.ActivateDeviceResponse
import com.web.authenticator.databinding.FragmentActivationBinding
import com.web.authenticator.util.SessionManager

class ActivationFragment : Fragment() {
    private var _binding: FragmentActivationBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<ActivationViewModel>()
    private lateinit var context : Context

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //val activationViewModel =
         //   ViewModelProvider(this).get(ActivationViewModel::class.java)

        _binding = FragmentActivationBinding.inflate(inflater, container, false)
        val root: View = binding.root
        context = requireContext()
        binding.btnActivate.setOnClickListener {
            activate()
        }

        viewModel.activateResult.observe(viewLifecycleOwner, Observer { result ->
            try {
                onSuccess(result)
            } catch (e: Exception) {
                // Catch and handle exceptions
                e.printStackTrace()
                onError()
            }
        })
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun activate(){
        val code1 = binding.textCode1.text.toString()
        val code2 = binding.textCode2.text.toString()

        viewModel.activateDevice(code1, code2)
    }

    fun onSuccess(response: Boolean){
        SessionManager.getToken(context)?.let { Log.d("ActivationFragment", it) };
        findNavController().navigate(R.id.nav_authenticate)
    }

    fun onError(){

    }
}