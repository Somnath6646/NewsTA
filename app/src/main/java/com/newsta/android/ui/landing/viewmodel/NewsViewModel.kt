package com.newsta.android.ui.landing.viewmodel

import androidx.databinding.Observable
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.newsta.android.NewstaApp
import com.newsta.android.remote.data.CategoryRequest
import com.newsta.android.remote.data.NewsRequest
import com.newsta.android.remote.data.NewsSourceRequest
import com.newsta.android.repository.StoriesRepository
import com.newsta.android.responses.CategoryResponse
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

    private fun getNewsOnInit() {

        if(NewstaApp.is_database_empty!!) {
            println("API ------>        ${NewstaApp.is_database_empty!!}")
            val days3 = System.currentTimeMillis() - (3 * 24 * 60 * 60 * 1000)
            getAllNews(0, days3)
        } else {
            println("DATABASE ------>        ${NewstaApp.is_database_empty!!}")
            getNewsFromDatabase()
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
            newsRepository.saveStory(story).onEach {
                _saveNewsState.value = it
            }.launchIn(viewModelScope)
        }
    }

    fun changeDatabaseState(isDatabaseEmpty: Boolean) {
        viewModelScope.launch {
            preferences.isDatabaseEmpty(isDatabaseEmpty)
        }
    }

    private val _categoryResponse = MutableLiveData<CategoryResponse>()
    val categoryResponse: LiveData<CategoryResponse>
        get() = _categoryResponse

    fun getCategories() {

        viewModelScope.launch {
            _categoryResponse.value = newsRepository.getCategories(CategoryRequest(NewstaApp.access_token!!, NewstaApp.ISSUER_NEWSTA))
        }

    }

    private val _categoryState = MutableLiveData<Int>()
    val categoryState: LiveData<Int>
        get() = _categoryState

    fun setCategoryState(categoryState: Int) {
        _categoryState.value = categoryState
    }

    private val _savedStoriesState = MutableLiveData<DataState<List<SavedStory>>>()
    val savedStoriesState: LiveData<DataState<List<SavedStory>>>
        get() = _savedStoriesState

    fun getSavedStories() {
        viewModelScope.launch {
            newsRepository.getSavedStories().onEach {
                _savedStoriesState.value = it
            }.launchIn(viewModelScope)
        }
    }

    init {
        getNewsOnInit()
        setCategoryState(0)
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {}
    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {}

}
