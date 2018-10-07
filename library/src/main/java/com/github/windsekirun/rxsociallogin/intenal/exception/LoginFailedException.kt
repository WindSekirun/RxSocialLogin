package com.github.windsekirun.rxsociallogin.intenal.exception

class LoginFailedException : IllegalStateException {

    constructor(message: String) : super(message)

    constructor(message: String, cause: Throwable) : super(message, cause)
}
