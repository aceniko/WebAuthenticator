package com.web.authenticator.ui.authenticate

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.zxing.integration.android.IntentIntegrator
import com.web.authenticator.R
import com.web.authenticator.databinding.FragmentAuthenticateBinding
import com.web.authenticator.util.SessionManager
import org.json.JSONException
import org.json.JSONObject

class AuthenticateFragment: Fragment() {
    private var _binding: FragmentAuthenticateBinding? = null
    private val viewModel by viewModels<AuthenticateViewModel>()
    private lateinit var qrScanIntegrator: IntentIntegrator

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAuthenticateBinding.inflate(layoutInflater)

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
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupScanner()
        setOnClickListener()
    }
    private fun setupScanner() {
        qrScanIntegrator = IntentIntegrator.forSupportFragment(this)
        qrScanIntegrator.setOrientationLocked(false)

    }

    private fun setOnClickListener() {
        _binding!!.btnScan.setOnClickListener { performAction() }
    }

    private fun performAction() {
        // Code to perform action when button is clicked.
        qrScanIntegrator.initiateScan()
    }

    fun onSuccess(response: Boolean){

    }

    fun onError(){

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            // If QRCode has no data.
            if (result.contents == null) {
                //Toast.makeText(activity, R.string.result_not_found, Toast.LENGTH_LONG).show()
            } else {
                // If QRCode contains data.
                try {
                    // Converting the data to json format
                    val sessionId = result.contents
                    processLogin(sessionId)
                    // Show values in UI.
                    //binding.name.text = obj.getString("name")
                    //binding.siteName.text = obj.getString("site_name")

                } catch (e: JSONException) {
                    e.printStackTrace()

                    // Data not in the expected format. So, whole object as toast message.
                    Toast.makeText(activity, result.contents, Toast.LENGTH_LONG).show()
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    fun processLogin(sessionId: String){
        viewModel.login(sessionId)
    }

}