package com.web.authenticator.communication


import com.web.authenticator.communication.contracts.ActivateDeviceRequest
import com.web.authenticator.communication.contracts.ActivateDeviceResponse
import com.web.authenticator.communication.contracts.LoginRequest
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface IRestClient {
    @POST("api/device/activate")
    suspend fun activateDevice(@Body request: ActivateDeviceRequest): Response<ActivateDeviceResponse>

    @POST("api/authenticate/authenticate")
    suspend fun authenticate(@Body request: LoginRequest): Response<Void>

    companion object {
        fun getApi(): IRestClient? {
            return ApiClient.client?.create(IRestClient::class.java)
        }
    }
}