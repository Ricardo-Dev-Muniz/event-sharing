package com.app.co.event_sharing.repository

import com.app.co.event_sharing.data.Event
import com.app.co.event_sharing.util.ResponseAny
import com.app.co.event_sharing.util.safeApiCall
import com.app.co.event_sharing.data.Person

class RepositoryUseCaseImpl(private val api: Services): MainRepositoryUseCase {
    override suspend fun getEvents(): ResponseAny<Array<Event?>?> = safeApiCall { api.getEvents() }
    override suspend fun getEventsById(id: String): ResponseAny<Event?> = safeApiCall { api.getEventById(id) }
    override suspend fun posChecking(person: Person): ResponseAny<Any> =
        safeApiCall { api.posChecking(person =  person) }
}