package com.newsta.android.ui.landing.viewmodel

import androidx.databinding.Observable
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.newsta.android.NewstaApp
import com.newsta.android.remote.data.NewsRequest
import com.newsta.android.remote.data.NewsSourceRequest
import com.newsta.android.repository.StoriesRepository
import com.newsta.android.responses.NewsSourceResponse
import com.newsta.android.utils.models.DataState
import com.newsta.android.utils.models.SavedStory
import com.newsta.android.utils.models.Story
import com.newsta.android.utils.prefrences.UserPrefrences
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat

class NewsViewModel
@ViewModelInject
constructor(private val newsRepository: StoriesRepository,
            private val preferences: UserPrefrences,
            @Assisted private val savedStateHandle: SavedStateHandle) : ViewModel(), Observable {

    private val _newsDataState = MutableLiveData<DataState<List<Story>>>()

    val newsDataState: LiveData<DataState<List<Story>>> = _newsDataState

    fun getAllNews(storyId: Int = 0, maxDateTime: Long) {

        val maxDate = SimpleDateFormat("dd-MM-yyyy").format(maxDateTime)

        viewModelScope.launch {
            val request = NewsRequest(NewstaApp.access_token!!, NewstaApp.ISSUER_NEWSTA, storyId, maxDate)
            newsRepository.getAllStories(newsRequest = request)
                    .onEach {
                        _newsDataState.value = it
                    }
                    .launchIn(viewModelScope)
        }

    }

    fun getNewsFromDatabase() {

        viewModelScope.launch {
            newsRepository.getNewsFromDatabase().onEach {
                _newsDataState.value = it
            }.launchIn(viewModelScope)
        }

    }

    private val _sources = MutableLiveData<NewsSourceResponse>()
    val sources: LiveData<NewsSourceResponse>
        get() = _sources

    fun getSources(storyId: Int, eventId: Int) {

        viewModelScope.launch {
            val request = NewsSourceRequest(NewstaApp.access_token!!, NewstaApp.ISSUER_NEWSTA, storyId, eventId)
            _sources.value = newsRepository.getSources(request)
        }

    }

    private val _saveNewsState = MutableLiveData<DataState<SavedStory>>()
    val saveNewsState: LiveData<DataState<SavedStory>>
        get() = _saveNewsState

    fun saveStory(story: SavedStory) {
        viewModelScope.launch {
            newsRepository.saveStory(story)
        }
    }

    fun changeDatabaseState(isDatabaseEmpty: Boolean) {
        viewModelScope.launch {
            preferences.isDatabaseEmpty(isDatabaseEmpty)
        }
    }

    //suspend fun insertNewsToDatabase(data: ArrayList<Data>) = newsRepository.insertNewsToDatabase(data)

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {}
    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {}

}
