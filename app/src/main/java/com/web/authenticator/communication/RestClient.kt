package com.web.authenticator.communication

import com.web.authenticator.communication.contracts.ActivateDeviceRequest
import com.web.authenticator.communication.contracts.ActivateDeviceResponse
import com.web.authenticator.communication.contracts.LoginRequest
import retrofit2.Call
import retrofit2.Response

class RestClient {
    suspend fun activateDevice(activateDeviceRequest: ActivateDeviceRequest): Response<ActivateDeviceResponse>? {
        return  IRestClient.getApi()?.activateDevice(request = activateDeviceRequest)
    }

    suspend fun authenticate(loginRequest: LoginRequest): Response<Void>? {
        return IRestClient.getApi()?.authenticate(request = loginRequest)
    }
}