package com.web.authenticator.communication.contracts

class LoginRequest {
    var id: String = ""
        get() = field
        set(value) {
            field = value
        }

    var tokenId: String = ""
        get() = field
        set(value) {
            field = value
        }

    var hash: String = ""
        get() = field
        set(value) {
            field = value
        }
}
