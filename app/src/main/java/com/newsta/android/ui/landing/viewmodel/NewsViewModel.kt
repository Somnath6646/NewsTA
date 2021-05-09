package com.newsta.android.ui.landing.viewmodel

import android.annotation.SuppressLint
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.facebook.login.LoginManager
import com.newsta.android.NewstaApp
import com.newsta.android.remote.data.*
import com.newsta.android.repository.StoriesRepository
import com.newsta.android.responses.CategoryResponse
import com.newsta.android.responses.LogoutResponse
import com.newsta.android.responses.NewsSourceResponse
import com.newsta.android.responses.SearchStory
import com.newsta.android.utils.helpers.Indicator
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


    @Bindable
    val searchTerm = MutableLiveData<String>()

    private val _newsDataState = MutableLiveData<DataState<List<Story>>>()

    val newsDataState: LiveData<DataState<List<Story>>> = _newsDataState

    private val _logoutDataState = MutableLiveData<Indicator<DataState<LogoutResponse?>>>()

    val logoutDataState: LiveData<Indicator<DataState<LogoutResponse?>>> = _logoutDataState

    private val _storyByIDDataState = MutableLiveData<Indicator<DataState<ArrayList<Story>?>>>()

    val storyByIDDataState: LiveData<Indicator<DataState<ArrayList<Story>?>>> = _storyByIDDataState

    private val _searchDataState = MutableLiveData<DataState<List<SearchStory>?>>()

    val searchDataState: LiveData<DataState<List<SearchStory>?>> = _searchDataState


    fun logOut(){
        viewModelScope.launch {
            val request = NewstaApp.access_token?.let { LogoutRequest(it, NewstaApp.ISSUER_NEWSTA) }
            if (request != null) {

                newsRepository.logout(logoutRequest = request).onEach {
                    _logoutDataState.value = Indicator(it)
                }.launchIn(viewModelScope)
                LoginManager.getInstance().logOut();
                clearAllData()
            }
        }
    }

    suspend fun clearAllData(){
        preferences.clearData()
        newsRepository.deleteAllStories()
    }

    fun getSearchResults(){
        viewModelScope.launch {
            val request = SearchRequest(NewstaApp.access_token!!, NewstaApp.ISSUER_NEWSTA, searchTerm.value!!)
            newsRepository.getSearchResults(searchRequest = request)
                    .onEach {
                        _searchDataState.value = it
                    }
                    .launchIn(viewModelScope)
        }
    }

    fun getStoryByIDFromSearch(storyId: Int){
        viewModelScope.launch {
            val request = SearchByStoryIDRequest(NewstaApp.access_token!!, NewstaApp.ISSUER_NEWSTA, storyId)
            newsRepository.getStoryByIDFromSearch(request)
                    .onEach {
                        _storyByIDDataState.value = Indicator(it)
                    }
                    .launchIn(viewModelScope)
        }
    }


    fun getAllNews(storyId: Int = 0, maxDateTime: Long) {

        viewModelScope.launch {
            val request = NewsRequest(NewstaApp.access_token!!, NewstaApp.ISSUER_NEWSTA, storyId, getMaxDate(maxDateTime))
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

    fun getNewsOnInit() {

        if(NewstaApp.is_database_empty!!) {
            println("API ------>        ${NewstaApp.is_database_empty!!}")
            val days3 = System.currentTimeMillis() - (3 * 24 * 60 * 60 * 1000)
            getAllNews(0, days3)
        } else {
            println("DATABASE ------>        ${NewstaApp.is_database_empty!!}")
            getNewsFromDatabase()
        }

    }

    private val _newsUpdateState = MutableLiveData<DataState<List<Story>>>()
    val newsUpdateState: LiveData<DataState<List<Story>>>
        get() = _newsUpdateState

    fun updateNews(storyId: Int, maxDateTime: Long) {

        viewModelScope.launch {
            newsRepository.updateExistingStories(NewsRequest(NewstaApp.access_token!!, NewstaApp.ISSUER_NEWSTA, storyId, getMaxDate(maxDateTime))).onEach {
                _newsUpdateState.value = it
            }.launchIn(viewModelScope)
        }

    }

    private val _minMaxStoryState = MutableLiveData<DataState<List<Story>>>()
    val minMaxStoryState: LiveData<DataState<List<Story>>>
        get() = _minMaxStoryState

    fun getMaxAndMinStory() {

        viewModelScope.launch {
            newsRepository.getMaxAndMinStory().onEach {
                _minMaxStoryState.value = it
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

    private val _savedStoriesDeleteState = MutableLiveData<DataState<SavedStory>>()
    val savedStoriesDeleteState: LiveData<DataState<SavedStory>>
        get() = _savedStoriesDeleteState

    fun deleteSavedStory(savedStory: SavedStory) {
        viewModelScope.launch {
            newsRepository.deleteSavedStory(savedStory).onEach {
                _savedStoriesDeleteState.value = it
            }.launchIn(viewModelScope)
        }
    }

    private val _filteredStoriesState = MutableLiveData<DataState<List<Story>>>()
    val filteredStoriesState: LiveData<DataState<List<Story>>>
        get() = filteredStoriesState

    fun getFilteredStories(categoryState: Int) {

        viewModelScope.launch {
            newsRepository.getFilteredStories(categoryState).onEach {
                _filteredStoriesState.value = it
            }.launchIn(viewModelScope)
        }

    }

    private val _toast = MutableLiveData<Indicator<String>>()
    val toast: LiveData<Indicator<String>>
        get() = _toast

    fun toast(message: String) {
        _toast.value = Indicator(message)
    }

    init {
        getCategories()
        getNewsOnInit()
        setCategoryState(0)
    }

    fun setFontScale(fontScale: Float) {
        viewModelScope.launch {
            preferences.setFontScale(fontScale)
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun getMaxDate(maxDateTime: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
        val date = sdf.format(maxDateTime)
        println("DATE: $date")
        return date
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {}
    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {}

}
