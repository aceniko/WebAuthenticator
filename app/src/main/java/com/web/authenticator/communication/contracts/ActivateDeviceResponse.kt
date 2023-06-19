package com.web.authenticator.communication.contracts


data class ActivateDeviceResponse(
    var serverPublicKey: String,
    var tokenSerial: String,
    var tokenId: Int
)