package com.cherry.kmp.domain.exception

import io.ktor.http.HttpStatusCode

sealed class ApiException(message: String) : Exception(message) {
    class NotFoundException : ApiException("Not Found")
    class UnauthorizedException : ApiException("Unauthorized! Please check the API Key")
    class BadRequestException : ApiException("Bad Request")
    class ServerErrorException : ApiException("Server Error")
    class UnknownException(status: HttpStatusCode) : ApiException("Unknown error: $status")
}