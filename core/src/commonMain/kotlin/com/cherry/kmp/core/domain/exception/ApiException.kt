package com.cherry.kmp.core.domain.exception

import io.ktor.http.HttpStatusCode

sealed class ApiException(message: String) : Exception(message) {
    class NotFoundException : ApiException("Content not found. Please try again later.")
    class UnauthorizedException : ApiException("Unable to access news. Please check your connection.")
    class BadRequestException : ApiException("Invalid request. Please try again.")
    class ServerErrorException : ApiException("News service temporarily unavailable. Please try again later.")
    class UnknownException(status: HttpStatusCode) : ApiException("Something went wrong. Please try again later.")
    class NetworkException : ApiException("No internet connection. Please check your network and try again.")
}