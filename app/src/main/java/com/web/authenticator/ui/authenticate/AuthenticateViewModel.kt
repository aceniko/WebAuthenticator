package com.web.authenticator.ui.authenticate

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.web.authenticator.communication.RestClient
import com.web.authenticator.communication.contracts.LoginRequest
import com.web.authenticator.util.Security
import com.web.authenticator.util.SessionManager
import kotlinx.coroutines.launch
import java.util.*

class AuthenticateViewModel(application: Application): AndroidViewModel(application) {
    val restClient = RestClient()
    val activateResult: MutableLiveData<Boolean> = MutableLiveData<Boolean>()

    fun login(id: String){
        viewModelScope.launch {
            try {
                val loginRequest = LoginRequest()
                loginRequest.id = id
                loginRequest.hash = Security.digest(id, Base64.getUrlDecoder().decode(SessionManager.getToken(getApplication())!!))
                loginRequest.tokenId = SessionManager.getString(getApplication(), "TokenId")!!
                val response = restClient.authenticate(loginRequest = loginRequest)
                if(response?.code()==200){
                    activateResult.value = true
                }else{
                    throw Error(response?.message())
                }
            }catch (ex: Exception){
                throw ex
            }
        }

    }
}