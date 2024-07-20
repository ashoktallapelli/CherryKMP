package com.cherry.kmp.data.network

import com.cherry.kmp.domain.exception.ApiException
import io.ktor.client.call.body
import io.ktor.client.statement.*
import io.ktor.http.*

object ResponseHandler {
    suspend inline fun <reified T> handleResponse(response: HttpResponse): T {
        return when (response.status) {
            HttpStatusCode.OK -> response.body()
            HttpStatusCode.BadRequest -> throw ApiException.BadRequestException()
            HttpStatusCode.Unauthorized -> throw ApiException.UnauthorizedException()
            HttpStatusCode.NotFound -> throw ApiException.NotFoundException()
            HttpStatusCode.InternalServerError -> throw ApiException.ServerErrorException()
            else -> throw ApiException.UnknownException(response.status)
        }
    }
}