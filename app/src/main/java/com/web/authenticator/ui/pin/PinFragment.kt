package com.web.authenticator.ui.pin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.web.authenticator.databinding.FragmentPinBinding

class PinFragment: Fragment() {
    private var _binding: FragmentPinBinding? = null
    private val viewModel by ViewModel<PinViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPinBinding.inflate(layoutInflater)

        viewModel.activateResult.observe(viewLifecycleOwner, Observer { result ->
            try {
                onSuccess(result)
            } catch (e: Exception) {
                // Catch and handle exceptions
                e.printStackTrace()
                onError()
            }
        })

        return _binding!!.root
    }
}