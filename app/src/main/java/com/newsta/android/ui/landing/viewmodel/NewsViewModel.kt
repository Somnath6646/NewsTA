package com.newsta.android.ui.landing.viewmodel

import androidx.databinding.Observable
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.newsta.android.NewstaApp
import com.newsta.android.remote.data.NewsRequest
import com.newsta.android.remote.data.Resource
import com.newsta.android.repository.NewsRepository
import com.newsta.android.responses.NewsResponse
import com.newsta.android.utils.Event
import com.newsta.android.utils.models.Data
import com.newsta.android.utils.prefrences.UserPrefrences
import kotlinx.coroutines.launch


class NewsViewModel
    @ViewModelInject
    constructor(private val newsRepository: NewsRepository,
                private val preferences: UserPrefrences,
                @Assisted private val savedStateHandle: SavedStateHandle) : ViewModel(), Observable {

    val newsResponse: LiveData<Event<Resource<NewsResponse>>>
        get() = _newsResponse

    private val _newsResponse = MutableLiveData<Event<Resource<NewsResponse>>>()

    fun getAllNews() {

        viewModelScope.launch {
            _newsResponse.value = Event(newsRepository.getAllNews(NewsRequest(NewstaApp.access_token!!, "newsta", 10000, "2021-04-11")))
        }

    }

    //suspend fun insertNewsToDatabase(data: ArrayList<Data>) = newsRepository.insertNewsToDatabase(data)

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {}
    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {}

}
