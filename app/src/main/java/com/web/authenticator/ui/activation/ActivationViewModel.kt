package com.web.authenticator.ui.activation

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.web.authenticator.communication.RestClient
import com.web.authenticator.communication.contracts.ActivateDeviceRequest
import com.web.authenticator.communication.contracts.ActivateDeviceResponse
import com.web.authenticator.util.Security
import com.web.authenticator.util.SessionManager
import kotlinx.coroutines.launch
import java.security.KeyPair
import java.util.*

class ActivationViewModel(application: Application): AndroidViewModel(application) {
    val restClient = RestClient()
    val activateResult: MutableLiveData<Boolean> = MutableLiveData<Boolean>()

    fun activateDevice(code1: String, code2: String) {

        viewModelScope.launch {
            try {
                val keyPair = Security.generateKeyPair()

                val activateDeviceRequest = ActivateDeviceRequest()
                activateDeviceRequest.code1 = code1
                activateDeviceRequest.code2 = code2
                activateDeviceRequest.publicKey = Base64.getEncoder().encodeToString(keyPair.public.encoded)
                Log.d("ActivationViewModel", keyPair.public.encoded.size.toString())
                val response = restClient.activateDevice(activateDeviceRequest = activateDeviceRequest)
                if (response?.code() == 200) {
                    if(!response.body()?.serverPublicKey.isNullOrBlank()){
                        val secret = generateSecret(response.body()!!, keyPair)
                        Log.i("ActivationViewModel Secret", secret)
                        saveSecret(secret)
                        saveTokenInformation(response.body()!!.tokenId, response.body()!!.tokenSerial)
                        activateResult.value = true
                    }else{
                        activateResult.value = false
                    }
                } else {
                    throw Error(response?.message())
                }

            } catch (ex: Exception) {
                throw ex
            }
        }
    }

    fun generateSecret(response: ActivateDeviceResponse, keyPair: KeyPair):String{
        val spk = Base64.getUrlDecoder().decode(response.serverPublicKey)
        val secret = Security.performKeyExchange(keyPair.private.encoded, spk)
        return Base64.getUrlEncoder().encodeToString(secret)
    }

    fun saveSecret(secret: String){
        SessionManager.saveAuthToken(getApplication(), secret)
    }

    fun saveTokenInformation(tokenId: Int, tokenSerial: String){
        SessionManager.saveString(getApplication(), "TokenId", tokenId.toString())
        SessionManager.saveString(getApplication(), "TokenSerial", tokenSerial)
    }
}