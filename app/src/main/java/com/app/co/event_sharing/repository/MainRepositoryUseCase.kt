package com.app.co.event_sharing.repository

import com.app.co.event_sharing.util.ResponseAny
import com.app.co.event_sharing.data.Event
import com.app.co.event_sharing.data.Person

interface MainRepositoryUseCase {
    suspend fun getEvents(): ResponseAny<Array<Event?>?>
    suspend fun getEventsById(id: String): ResponseAny<Event?>
    suspend fun posChecking(person: Person): ResponseAny<Any>
}