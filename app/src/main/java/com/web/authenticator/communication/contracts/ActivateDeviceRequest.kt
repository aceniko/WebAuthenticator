package com.web.authenticator.communication.contracts

class ActivateDeviceRequest {
    var code1: String = ""
        get() = field
        set(value){
            field = value
        }

    var code2: String = ""
        get()=field
        set(value) {
            field = value
        }
    var publicKey: String = ""
        get()=field
        set(value){
            field = value
        }
    constructor(code1: String, code2: String, publicKey: String){
        this.code1 = code1
        this.code2 = code2
        this.publicKey = publicKey
    }

    constructor();
}