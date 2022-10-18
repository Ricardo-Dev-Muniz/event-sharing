package com.app.co.event_sharing

import android.util.Log
import androidx.lifecycle.*
import com.app.co.event_sharing.data.Event
import com.app.co.event_sharing.data.Person
import com.app.co.event_sharing.data.PostStatus
import br.com.sendevent.data.Status
import com.app.co.event_sharing.util.read
import com.app.co.event_sharing.repository.MainRepositoryUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(
    private val repository: MainRepositoryUseCase,
) : ViewModel() {

    private val _events: MutableLiveData<Array<Event?>?> = MutableLiveData()
    val events: LiveData<Array<Event?>?> = _events

    private val _event: MutableLiveData<Event?> = MutableLiveData()
    val event: LiveData<Event?> = _event

    private val _flowEvent: MutableLiveData<String> = MutableLiveData()
    val flowEvent: LiveData<String> = _flowEvent

    private val _isChecking: MutableLiveData<Boolean?> = MutableLiveData()
    val isChecking: LiveData<Boolean?> = _isChecking

    private val _isSubmit: MutableLiveData<Boolean> = MutableLiveData(false)
    val isSubmit: LiveData<Boolean> = _isSubmit

    private val _isScreen: MutableLiveData<Boolean?> = MutableLiveData(false)
    val isScreen: LiveData<Boolean?> = _isScreen

    private val _isStatus: MutableLiveData<Status> = MutableLiveData()
    val isState: LiveData<Status> = _isStatus

    private val _isStatusCheckIn: MutableLiveData<PostStatus> = MutableLiveData()
    val isStatusCheckIn: LiveData<PostStatus> = _isStatusCheckIn

    fun getEvents() = viewModelScope.launch {
        _isStatus.value = Status.LOADING
        withContext(Dispatchers.IO) {
            repository.getEvents()
        }.read({
            _events.value = it
            _isStatus.value = Status.SUCCESS
        }, {
            _isStatus.value = Status.FAILURE
            Log.e("viewModel", "error events load: ${it.message}")
        })
    }

    fun getEventsById(id: String) = viewModelScope.launch {
        _isStatus.value = Status.LOADING
        withContext(Dispatchers.IO) {
            repository.getEventsById(id)
        }.read({
            _event.value = it
            _isStatus.value = Status.SUCCESS
        }, {
            _isStatus.value = Status.FAILURE
            Log.e("viewModel", "error details: ${it.message}")
        })
    }

    fun setCheckIn(person: Person) = viewModelScope.launch {
        _isStatusCheckIn.value = PostStatus.LOADING
        withContext(Dispatchers.IO) {
            repository.posChecking(person)
        }.read({
            _isStatusCheckIn.value = PostStatus.SUCCESS
            if (it.toString().isNotBlank() && it != "")
                _isChecking.value = false
        }, {
            _isStatusCheckIn.value = PostStatus.FAILURE
            Log.e("viewModel", "error checkIn: ${it.message}")
        })
    }

    fun isSubmit() = submit(isSubmit = true)
    fun isNoSubmit() = submit(isSubmit = false)

    private fun submit(isSubmit: Boolean) {
        _isSubmit.value = isSubmit
    }

    fun flowEvent(eventId: String) {
        _flowEvent.value = eventId
    }

    fun setIsPostExecuted() {
        _isStatusCheckIn.value = PostStatus.SUCCESS
    }

    fun doneChecking(isDone: Boolean?) {
        _isChecking.value = isDone
    }

    fun isScreen(isOn: Boolean?) {
        _isScreen.value = isOn
    }
}



