package com.newsta.android.viewmodels

import android.annotation.SuppressLint
import android.util.Log
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.facebook.login.LoginManager
import com.newsta.android.NewstaApp
import com.newsta.android.remote.data.*
import com.newsta.android.repository.StoriesRepository
import com.newsta.android.responses.LogoutResponse
import com.newsta.android.responses.SearchStory
import com.newsta.android.utils.helpers.Indicator
import com.newsta.android.utils.models.*
import com.newsta.android.utils.prefrences.UserPrefrences
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class NewsViewModel
@ViewModelInject
constructor(private val newsRepository: StoriesRepository,
            val preferences: UserPrefrences,
            @Assisted private val savedStateHandle: SavedStateHandle) : ViewModel(), Observable {

    @Bindable
    val searchTerm = MutableLiveData<String>("")

    private val _selectedStoryList = MutableLiveData<List<Story>>()

    val selectedStoryList: LiveData<List<Story>>
    get() = _selectedStoryList

    fun setSelectedStoryList(stories: List<Story>){
        _selectedStoryList.value = stories
    }

    private val _newsDataState = MutableLiveData<DataState<List<Story>>>()
    val newsDataState: LiveData<DataState<List<Story>>> = _newsDataState

    private val _dbNewsDataState = MutableLiveData<DataState<List<Story>>>()
    val dbNewsDataState: LiveData<DataState<List<Story>>> = _dbNewsDataState

    private val _dbCategoryDataState = MutableLiveData<DataState<List<Category>?>>()
    val dbCategoryDataState: LiveData<DataState<List<Category>?>> = _dbCategoryDataState

    private val _categoryDataState = MutableLiveData<DataState<List<Category>?>>()
    val categoryDataState: LiveData<DataState<List<Category>?>> = _categoryDataState

    private val _sourcesDataState = MutableLiveData<DataState<List<NewsSource>?>>()
    val sourcesDataState: LiveData<DataState<List<NewsSource>?>> = _sourcesDataState

    private val _logoutDataState = MutableLiveData<Indicator<DataState<LogoutResponse?>>>()
    val logoutDataState: LiveData<Indicator<DataState<LogoutResponse?>>> = _logoutDataState

    private val _storyByIDDataState = MutableLiveData<Indicator<DataState<ArrayList<Story>?>>>()
    val storyByIDDataState: LiveData<Indicator<DataState<ArrayList<Story>?>>> = _storyByIDDataState

    private val _searchDataState = MutableLiveData<DataState<List<SearchStory>?>>()
    val searchDataState: LiveData<DataState<List<SearchStory>?>> = _searchDataState

    fun logOut() {
        viewModelScope.launch {
            val request = NewstaApp.access_token?.let { LogoutRequest(it, NewstaApp.ISSUER_NEWSTA) }
            if (request != null) {

                newsRepository.logout(logoutRequest = request).onEach {
                    _logoutDataState.value = Indicator(it)
                }.launchIn(viewModelScope)

            }
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            preferences.clearData()
            preferences.appInstalledJustNow(false)
            newsRepository.deleteAllStories(maxTime = System.currentTimeMillis())
            Log.i("MYTAG", "clearAllData: Aya hai")

        }
    }

    fun getSearchResults() {
        if(!searchTerm.value.isNullOrEmpty()) {
            viewModelScope.launch {
                val request =
                    SearchRequest(
                        NewstaApp.access_token!!,
                        NewstaApp.ISSUER_NEWSTA,
                        searchTerm.value!!
                    )
                newsRepository.getSearchResults(searchRequest = request)
                    .onEach {
                        _searchDataState.value = it
                    }
                    .launchIn(viewModelScope)
            }
        } else toast("Type something to search")
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


    fun getAllNews(storyId: Int = 0, maxDateTime: Long, isRefresh: Boolean = false) {

        viewModelScope.launch {
            val request = NewsRequest(NewstaApp.access_token!!, NewstaApp.ISSUER_NEWSTA, storyId, getMaxDate(maxDateTime))
            newsRepository.getAllStories(newsRequest = request, isRefresh = isRefresh)
                    .onEach {
                        _newsDataState.value = it
                    }
                    .launchIn(viewModelScope)
        }

    }

    private fun getNewsFromDatabase() {

        viewModelScope.launch {
            newsRepository.getNewsFromDatabase().onEach {
                println("NEWS FROM DB: $it")
                _dbNewsDataState.value = it
            }.launchIn(viewModelScope)
        }

    }

    private fun getCategoriesFromDatabase() {

        viewModelScope.launch {
            newsRepository.getCategoryFromDatabase().onEach {
                println("NEWS FROM DB: $it")
                _dbCategoryDataState.value = it
            }.launchIn(viewModelScope)
        }

    }

    fun getNewsOnInit() {

        if(NewstaApp.is_database_empty!!) {
            println("API ------>        ${NewstaApp.is_database_empty}")


            val days3 = System.currentTimeMillis() - (3 * 24 * 60 * 60 * 1000)

            //prefrences se idharr lenaa hai
            Log.i("MYTAG", "getNewsOnInit: Aaya biroo")
            getAllNews(0, days3)
        } else {
            println("DATABASE ------>        ${NewstaApp.is_database_empty}")
            Log.i("MYTAG", "getNewsOnInit:Nhi Aaya biroo")
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

    fun getSources(storyId: Int, eventId: Int) {

        viewModelScope.launch {
            val request = NewsSourceRequest(NewstaApp.access_token!!, NewstaApp.ISSUER_NEWSTA, storyId, eventId)
            newsRepository.getSources(request).onEach {
                _sourcesDataState.value = it
            }.launchIn(viewModelScope)
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

    fun getCategories() {

        viewModelScope.launch {
            println("NEWSTA APP: ${NewstaApp.access_token}")
            val request = CategoryRequest(NewstaApp.access_token!!, NewstaApp.ISSUER_NEWSTA)
            newsRepository.getCategories(request).onEach {
                _categoryDataState.value = it
            }.launchIn(viewModelScope)
        }

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

    fun deleteSavedStory(savedStory: List<SavedStory>) {
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

    private val _checkSavedStoryState = MutableLiveData<DataState<SavedStory>>()
    val checkSavedStoryState: LiveData<DataState<SavedStory>>
        get() = _checkSavedStoryState

    fun getSavedStory(storyId: Int) {

        viewModelScope.launch {
            newsRepository.getSavedStory(storyId)
                .onEach {
                    _checkSavedStoryState.value = it
                }.launchIn(viewModelScope)
        }

    }

    init {
        Log.i("TAG", "init viemodel ")
//        getCategories()
        getCategoriesFromDatabase()
        getNewsOnInit()
    }

    fun setFontScale(fontScale: Float) {
        viewModelScope.launch {
            preferences.setFontScale(fontScale)
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun getMaxDate(maxDateTime: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
        val dateIst = sdf.format(maxDateTime)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        val dateUtc = sdf.format(maxDateTime)
        println("DATE IST: $dateIst")
        println("DATE UTC: $dateUtc")
        return dateUtc
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {}
    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {}

    companion object {
        var stories = ArrayList<Story>()
        var isRefreshedByDefault = false
    }

}
