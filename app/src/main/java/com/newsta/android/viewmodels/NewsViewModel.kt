package com.newsta.android.viewmodels

import android.annotation.SuppressLint
import android.util.Log
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.newsta.android.MainActivity
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

    var urlToRequest: String = "http://13.235.50.53/"

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

    private val _dbNewsLiveData = MutableLiveData<ArrayList<Story>>()
    val dbNewsLiveData: LiveData<ArrayList<Story>> = _dbNewsLiveData

    private val _userPreferencesDataState = MutableLiveData<DataState<UserPreferences>?>()
    val userPreferencesDataState: LiveData<DataState<UserPreferences>?> = _userPreferencesDataState

    private val _dbCategoryDataState = MutableLiveData<DataState<List<Category>?>>()
    val dbCategoryDataState: LiveData<DataState<List<Category>?>> = _dbCategoryDataState

    private val _categoryDataState = MutableLiveData<DataState<List<Category>?>>()
    val categoryDataState: LiveData<DataState<List<Category>?>> = _categoryDataState

    private val _categoryLiveData = MutableLiveData<List<Category>?>()
    val categoryLiveData: LiveData<List<Category>?> = _categoryLiveData

    private val _userCategoryLiveData = MutableLiveData<List<Int>?>()
    val userCategoryLiveData: LiveData<List<Int>?> = _userCategoryLiveData.distinctUntilChanged()

    private val _savedStoryIdLiveData = MutableLiveData<List<Int>?>(arrayListOf())
    val savedStoryIdLiveData: LiveData<List<Int>?> = _savedStoryIdLiveData

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

        urlToRequest  = "http://13.235.50.53/new"

        println("GET ALL NEWS MEIN AAYA")

        viewModelScope.launch {
            val maxDateTime  = getMaxDate(maxDateTime)
                val request = NewsRequest(
                    NewstaApp.access_token!!,
                    NewstaApp.ISSUER_NEWSTA,
                    storyId,
                    maxDateTime
                )
                debugToast("storyId : ${storyId} \n maxDateTime: ${maxDateTime} ")
                newsRepository.getAllStories(newsRequest = request, isRefresh = isRefresh)
                    .onEach {
                        _newsDataState.value = it
                    }
                    .launchIn(viewModelScope)
        }

    }

    fun getNewsFromDatabase() {

        viewModelScope.launch {
            newsRepository.getNewsFromDatabase().onEach {
//                println("NEWS FROM DB: $it")
//                _dbNewsDataState.value = it
//                when(it) {
//                    is DataState.Success -> {
                      _dbNewsDataState.value = it
                    /*}
                    is DataState.Error -> {
                        if(NewstaApp.is_database_empty) {
                            println("DB SE BAAR BAAR CALL HO RAHA HAI")
                            hasGotNews = true
                            getNewsFirstTime()
                        }
                    }
                }*/
                /*when (it) {
                    is DataState.Success<List<Story>?> -> {
                        Log.i("DBnewsDataState", " success")
                        debugToast("dBnewsDataState: success")
//                        binding.refreshLayout.isRefreshing = false
                        changeDatabaseState(isDatabaseEmpty = false)
                        _dbNewsLiveData.value = it.data as ArrayList<Story>
                        *//*stories = ArrayList(it.data)
                        val filteredStories =
                            stories.filter { story: Story -> story.category == StoriesDisplayFragment.categoryState }
                        if (filteredStories.isNullOrEmpty()) {
                            NewstaApp.is_database_empty = true
                            viewModel.changeDatabaseState(true)
                            viewModel.getNewsOnInit()
                            NewstaApp.setIsDatabaseEmpty(true)
                        }
                        println("FilteredStories  $filteredStories")*//*

                        *//*val stories = ArrayList<Story>(filteredStories)
                        stories.sortByDescending {
                                story ->  story.updatedAt
                        }*//*
//                        adapter.addAll(stories)
                    }
                    is DataState.Error -> {
                        Log.i("dBnewsDataState", " errror ${it.exception}")
                        if(NewstaApp.is_database_empty) {
                            println("DB SE BAAR BAAR CALL HO RAHA HAI")
                            hasGotNews = true
                            getNewsFirstTime()
                        }
                        *//*viewModel.debugToast("dBnewsDataState:  errror ${it.exception}")

                        binding.refreshLayout.isRefreshing = false*//*
                    }
                    is DataState.Loading -> {
                        Log.i("dBnewsDataState", " loding")
                        *//*viewModel.debugToast("dBnewsDataState: loading")

                        binding.refreshLayout.isRefreshing = true*//*
                    }
                    is DataState.Extra<List<Story>?> -> {
                        try {
                            println("EXTRA DB DATA ---> ${it.data}")
                            if (!it.data.isNullOrEmpty()) {
                                MainActivity.maxStory = it.data.first()
                                MainActivity.minStory = it.data.last()
                                MainActivity.extras = ArrayList(it.data)
                                Log.i(
                                    "newsDataState",
                                    " EXTRA MAX ${MainActivity.maxStory.storyId} ${MainActivity.maxStory.updatedAt} ${MainActivity.maxStory.category} ${MainActivity.maxStory.events}"
                                )
                                *//*if(!isRefreshedByDefault) {
                                    isRefreshedByDefault = true
                                    viewModel.getAllNews(MainActivity.maxStory.storyId, MainActivity.maxStory.updatedAt)
                                }*//*
                            }
                        } catch (e: Exception) {
                            debugToast("Min max error")
                            e.printStackTrace()
                        }
                    }
                }*/
            }.launchIn(viewModelScope)
        }

    }

    private fun getCategoriesFromDatabase() {

        urlToRequest  = "DataBase"
        viewModelScope.launch {
            newsRepository.getCategoryFromDatabase().onEach {
                println("CATEGORIES FROM DB: $it")
                    when (it) {
                        is DataState.Success -> {
                            Log.i("TAG", "onActivityCreated: CategoryDatState Success DB")
                            val dbCategories = it.data as ArrayList<Category>
                            getCategories()
                            println("CATEGORY LIVE DATA VALUE ---> ${categoryLiveData.value}")
                            if (categoryLiveData.value.isNullOrEmpty()) {
                                _categoryLiveData.value = dbCategories
                            }
                        }
                        is DataState.Error -> {
                            Log.i("TAG", "onActivityCreated: CategoryDatState Error")

                        }
                        is DataState.Loading -> {
                            Log.i("TAG", "onActivityCreated: CategoryDatState logading")
                        }
                    }
            }.launchIn(viewModelScope)
        }

    }

    fun setUserCategoryLiveData(userCategories: ArrayList<Int>) {
        _userCategoryLiveData.value = userCategories
    }

    fun setSavedStoryIds(userCategories: ArrayList<Int>) {
        _savedStoryIdLiveData.value = userCategories
    }

    fun getUserPreferences() {
        viewModelScope.launch {
            newsRepository.getUserPrefrences().onEach {
//                _userPreferencesDataState.value = it
                when (it) {
                    is DataState.Success -> {
                        val userPreferences = it.data
                        Log.i("getUserPreferences", "${it.data.categories}")
                        _userCategoryLiveData.value = (userPreferences.categories)
                        _savedStoryIdLiveData.value = userPreferences.saved
                    }
                    is DataState.Error -> {
                        Log.i("TAG", "onActivityCreated: CategoryDatState Error")
//                        checkIfUnauthorized(it)
                    }
                    is DataState.Loading -> {
                        Log.i("TAG", "onActivityCreated: CategoryDatState logading")
                    }
                }
            }.launchIn(viewModelScope)
        }

    }

    fun getNewsOnInit() {

        if(NewstaApp.is_database_empty) {
            getNewsFirstTime()
        } else {
            println("DATABASE ------>        ${NewstaApp.is_database_empty}")
            Log.i("MYTAG", "getNewsOnInit:Nhi Aaya biroo")
            getNewsFromDatabase()
        }

    }

    private fun getNewsFirstTime() {
        println("IS DB EMPTY FIRST TIME ------>        ${NewstaApp.is_database_empty}")
        val days3 = System.currentTimeMillis() - (3 * 24 * 60 * 60 * 1000)
        Log.i("MYTAG", "getNewsOnInit: Aaya biroo")
        getAllNews(0, days3)
    }

    private val _newsUpdateState = MutableLiveData<DataState<List<Story>>>()
    val newsUpdateState: LiveData<DataState<List<Story>>>
        get() = _newsUpdateState

    fun updateNews(storyId: Int, maxDateTime: Long) {
        urlToRequest  = "http://13.235.50.53/existing"
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
        urlToRequest  = "http://13.235.50.53/sources"
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
            newsRepository.saveStoryInDB(story).onEach {
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
//                _categoryDataState.value = it
                when (it) {
                    is DataState.Success -> {
                        Log.i("TAG", "onActivityCreated: CategoryDatState Success")
                        var newCategories = it.data as ArrayList<Category>
                        _categoryLiveData.value = newCategories
                        println("CATEGORY KAA NAYA VALUE --> ${categoryLiveData.value}")
                    }
                    is DataState.Error -> {
                        Log.i("TAG", "onActivityCreated: CategoryDatState Error")
//                        checkIfUnauthorized(it)
                    }
                    is DataState.Loading -> {
                        Log.i("TAG", "onActivityCreated: CategoryDatState logading")
                    }
                }
            }.launchIn(viewModelScope)
        }

    }

    private val _savedStoriesList = MutableLiveData<List<SavedStory>>()
    val savedStoriesList: LiveData<List<SavedStory>>
        get() = _savedStoriesList

    fun getSavedStories() {
        viewModelScope.launch {
            newsRepository.getSavedStoriesFromDB().onEach {
                when(it){
                    is DataState.Success -> {
                        val list = it.data
                        if(list.isEmpty()){
                           getStoriesByIds()
                        }
                        _savedStoriesList.value = it.data
                    }
                    is DataState.Loading -> {
                        Log.i("Saved stories by ids", "loading")
                    }
                    is DataState.Error -> {
                        Log.i("Saved Stories by ids", "error")
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun getStoriesByIds(){
        viewModelScope.launch {
            newsRepository.getStoriesByIds(savedStoryIds = savedStoryIdLiveData.value as ArrayList<Int>).onEach {
                when(it){
                    is DataState.Success -> {
                        _savedStoriesList.value = it.data
                    }
                    is DataState.Loading -> {
                        Log.i("Stories by ids", "loading")
                    }
                    is DataState.Error -> {
                        Log.i("Stories by ids", "error")
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    private val _savedStoriesDeleteState = MutableLiveData<DataState<SavedStory>>()
    val savedStoriesDeleteState: LiveData<DataState<SavedStory>>
        get() = _savedStoriesDeleteState

    fun deleteSavedStory(savedStory: SavedStory) {
        viewModelScope.launch {
            newsRepository.deleteSavedStoryInDB(savedStory).onEach {
                _savedStoriesDeleteState.value = it
            }.launchIn(viewModelScope)
        }
    }

    fun deleteSavedStory(savedStory: List<SavedStory>) {
        viewModelScope.launch {
            newsRepository.deleteSavedStoryInDB(savedStory).onEach {
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

    private val _debugToast = MutableLiveData<Indicator<String>>()
    val debugToast: LiveData<Indicator<String>>
        get() = _debugToast

    fun debugToast(message: String) {
        _debugToast.value = Indicator(message)
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

    private val _userCategoriesSaveDataState = MutableLiveData<DataState<ArrayList<Int>?>>()
    val userCategoriesSaveDataState: LiveData<DataState<ArrayList<Int>?>> = _userCategoriesSaveDataState

    fun saveUserCategories(userCategories: ArrayList<Int>) {
        if(MainActivity.isConnectedToNetwork) {
            viewModelScope.launch {
                newsRepository.saveUserCategoriesInServer(userCategories).onEach {
                    _userCategoriesSaveDataState.value = it
                }.launchIn(viewModelScope)
            }
        } else {
            toast("Please connect to network to save changes.")
        }
    }

    private val _userSavedStorySaveDataState = MutableLiveData<DataState<ArrayList<Int>?>>()
    val userSavedStorySaveDataState: LiveData<DataState<ArrayList<Int>?>> = _userSavedStorySaveDataState

    fun saveSavedStoryIds(savedStoryIds: ArrayList<Int>) {
        if(MainActivity.isConnectedToNetwork) {
            viewModelScope.launch {
                newsRepository.saveSavedStoryIdsInServer(savedStoryIds).onEach {
                    _userSavedStorySaveDataState.value = it
                }.launchIn(viewModelScope)
            }
        } else {
            toast("Please connect to network to save changes.")
        }
    }

    fun setUserPreferencesState(userPreferences: UserPreferences) {
        _userPreferencesDataState.value = DataState.Success(userPreferences)
    }

    fun changeUserPreferencesState(hasChanged: Boolean) {
        viewModelScope.launch {
            preferences.hasChangedPreferences(hasChanged)
        }
    }

    init {
        Log.i("TAG", "init viemodel ")
//        getCategories()
        getCategoriesFromDatabase()
        getUserPreferences()
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
