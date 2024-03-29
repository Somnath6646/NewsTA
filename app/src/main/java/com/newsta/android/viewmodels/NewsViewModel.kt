package com.newsta.android.viewmodels

import android.annotation.SuppressLint
import android.util.Log
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.newsta.android.MainActivity
import com.newsta.android.MainActivity.Companion.extras
import com.newsta.android.MainActivity.Companion.maxStory
import com.newsta.android.NewstaApp
import com.newsta.android.interfaces.DetailsBottomNavInterface
import com.newsta.android.remote.data.*
import com.newsta.android.repository.StoriesRepository
import com.newsta.android.responses.LogoutResponse
import com.newsta.android.responses.SearchStory
import com.newsta.android.utils.helpers.Indicator
import com.newsta.android.utils.models.*
import com.newsta.android.utils.prefrences.UserPrefrences
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.max

class NewsViewModel
@ViewModelInject
constructor(private val newsRepository: StoriesRepository,
            val preferences: UserPrefrences,
            @Assisted private val savedStateHandle: SavedStateHandle) : ViewModel(), Observable {

    @Bindable
    val searchTerm = MutableLiveData<String>("")

    @Bindable
    var refreshState = MutableLiveData<Boolean>(false)

    var urlToRequest: String = "http://13.235.50.53/"

    val days3 = System.currentTimeMillis() - (3 * 24 * 60 * 60 * 1000)


    private val _selectedStoryList = MutableLiveData<List<Story>>()

    val selectedStoryList: LiveData<List<Story>>
    get() = _selectedStoryList

    fun setIsDarkMode(isDarkMode: Boolean){
        viewModelScope.launch {
            preferences.setIsDarkMode(isDarkMode)
        }
    }

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

    private val _notifyStoriesLiveData = MutableLiveData<List<Payload>?>(arrayListOf())
    val notifyStoriesLiveData: LiveData<List<Payload>?> = _notifyStoriesLiveData

    var notifyStories = ArrayList<SavedStory>()

    private val _storiesLiveData = MutableLiveData<Map<Int, List<Story>>>()
    val storiesLiveData: LiveData<Map<Int, List<Story>>> = _storiesLiveData

    private val _sourcesDataState = MutableLiveData<DataState<List<NewsSource>?>>()
    val sourcesDataState: LiveData<DataState<List<NewsSource>?>> = _sourcesDataState

    private val _logoutDataState = MutableLiveData<Indicator<DataState<LogoutResponse?>>>()
    val logoutDataState: LiveData<Indicator<DataState<LogoutResponse?>>> = _logoutDataState

    private val _storyByIDDataState = MutableLiveData<Indicator<DataState<ArrayList<Story>?>>>()
    val storyByIDDataState: LiveData<Indicator<DataState<ArrayList<Story>?>>> = _storyByIDDataState

    private val _searchDataState = MutableLiveData<DataState<List<SearchStory>?>>()
    val searchDataState: LiveData<DataState<List<SearchStory>?>> = _searchDataState

    private val _unauthorizedLiveData = MutableLiveData<Indicator<Boolean>>()
    val unauthorizedLiveData: LiveData<Indicator<Boolean>> = _unauthorizedLiveData

    private fun checkIfUnauthorized(data: DataState.Error) {
        if (NewstaApp.UNAUTHORIZED_STATUS_CODES.contains(data.statusCode)) {
            _unauthorizedLiveData.value = Indicator(true)
        } else
            toast(data.exception)
    }

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

    val recommendedStoriesLiveData: LiveData<List<RecommendedStory>>
    get() = newsRepository.recommendedStories

    val presentRecommendedStoryIds: List<Int>?
        get() = recommendedStoriesLiveData.value?.map { recommendedStory ->
        recommendedStory.storyId
    }

    fun getRecommendedStories(){
        viewModelScope.launch {
            if(NewstaApp.access_token != null) {
                var req = RecommendedStoriesRequest(
                    NewstaApp.access_token!!,
                    NewstaApp.ISSUER_NEWSTA,
                    listOf()
                )
                if (presentRecommendedStoryIds != null) {
                    req = RecommendedStoriesRequest(
                        NewstaApp.access_token!!,
                        NewstaApp.ISSUER_NEWSTA,
                        presentRecommendedStoryIds!!
                    )
                }

                newsRepository.getAllRecommendedStories(req).onEach {
                    when (it) {
                        is DataState.Success -> {
                            toast("Success Recommended")
                        }
                        is DataState.Loading -> {
                            toast("Loading Recommended")
                        }
                        is DataState.Error -> {
                            toast("Error in Recommended ${it.exception}")
                        }
                    }
                }.launchIn(viewModelScope)
            }
        }
    }

    fun updateRecommendedStoryReadStatus(storyId: Int, hasRead: Boolean ){
        viewModelScope.launch {
            newsRepository.updateRecommendedStoryReadStatus(storyId, hasRead)
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


    fun getNewStories(storyId: Int = 0, _maxDateTime: Long, isRefresh: Boolean = false) {

        urlToRequest  = "http://13.235.50.53/new"

        println("GET ALL NEWS MEIN AAYA")

        viewModelScope.launch {
            if(NewstaApp.access_token != null) {

                val maxDateTime = getMaxDate(_maxDateTime)

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
                        when (it) {
                            is DataState.Success -> {

                                debugToast("newsDataState:  success")
                                refreshState.value = false
                                changeDatabaseState(isDatabaseEmpty = false)


                                _storiesLiveData.value = getMapOfStories(
                                    oldStories = stories,
                                    newStories = ArrayList(it.data)
                                )
                                println("it.data ----> ${it.data}")

                                val list = arrayListOf<Payload>()
                                val origList = notifyStoriesLiveData.value
                                println("notifystory onupdate $origList")
                                if (origList != null) {
                                    val stories = it.data

                                    origList.forEach {
                                        if (stories.checkIfHasThisStoryId(it.storyId)) {
                                            list.add(
                                                Payload(
                                                    storyId = it.storyId,
                                                    read = ArticleState.UNREAD
                                                )
                                            )
                                        } else {
                                            list.add(it)
                                        }
                                    }

                                }
                                println("notifystory afterupdate $list")
                                saveNotifyStories(list, "new update")

                            }
                            is DataState.Error -> {
                                Log.i("newsDataState", "errror ${it.exception}")

                                debugToast("newsDataState:  errror ${it.exception}")
                                refreshState.value = false
                            }
                            is DataState.Loading -> {
                                Log.i("newsDataState", " loding")
                                debugToast("newsDataState:  loading.....")
                                refreshState.value = true
                            }
                            is DataState.Extra -> {
                                try {
                                    if (it.data != null) {
                                        maxStory = it.data
                                        extras = arrayListOf(it.data)
                                    }
                                } catch (e: java.lang.Exception) {
                                    debugToast("Min Max error")
//                        Toast.makeText(requireContext(), "Min Max error", Toast.LENGTH_SHORT).show()
                                    e.printStackTrace()
                                }
                                Log.i(
                                    "newsDataState",
                                    " EXTRA MAX ${maxStory.storyId} ${maxStory.updatedAt} "
                                )
                            }
                        }
                    }
                    .launchIn(viewModelScope)
            }
        }

    }

    private fun getMapOfStories(oldStories: ArrayList<Story>, newStories: ArrayList<Story>): Map<Int, List<Story>> {
        val categoryStories = mutableMapOf<Int, List<Story>>()
        categoryLiveData.value?.forEach { category ->

            var _oldStories = oldStories.filter { story -> story.category == category.categoryId }
            _oldStories = _oldStories.sortedByDescending {
                it.updatedAt
            }

            var _newStories = newStories.filter { story -> story.category == category.categoryId }
            _newStories = _newStories.sortedByDescending {
                it.updatedAt
            }

            val catStories = _newStories + _oldStories
            categoryStories.put(category.categoryId, catStories)

        }
        return categoryStories
    }

    fun getNewsFromDatabase() {
        viewModelScope.launch {
            newsRepository.getNewsFromDatabase().onEach {
                when (it) {
                    is DataState.Success<List<Story>?> -> {
                        Log.i("DBnewsDataState", " success")
                        debugToast("dBnewsDataState: success")
                       refreshState.value = false
                        changeDatabaseState(isDatabaseEmpty = false)
                        stories = ArrayList(it.data)

                        _storiesLiveData.value = getMapOfStories(oldStories = stories, newStories = arrayListOf())

                        /*val filteredStories =
                            stories.filter { story: Story -> story.category == categoryState }
                        if (filteredStories.isNullOrEmpty()) {
                            NewstaApp.is_database_empty = true
                            viewModel.changeDatabaseState(true)
                            viewModel.getNewsOnInit()
                            NewstaApp.setIsDatabaseEmpty(true)
                        }
                        println("FilteredStories  $filteredStories")

                        val stories = ArrayList<Story>(filteredStories)
                        adapter.addAll(stories)*/
                    }
                    is DataState.Error -> {
                        Log.i("dBnewsDataState", " errror ${it.exception}")
                        debugToast("dBnewsDataState:  errror ${it.exception}")

                       refreshState.value = false
                    }
                    is DataState.Loading -> {
                        Log.i("dBnewsDataState", " loding")
                        debugToast("dBnewsDataState: loading")

                       refreshState.value = true
                    }
                    is DataState.Extra -> {
                        try {
                            println("EXTRA DB DATA ---> ${it.data}")
                            if (it.data != null) {
                                maxStory = it.data
                                extras = arrayListOf(it.data)
                                Log.i(
                                    "newsDataState",
                                    " EXTRA MAX ${maxStory.storyId} ${maxStory.updatedAt}"
                                )
                                if(!isRefreshedByDefault) {
                                    isRefreshedByDefault = true
                                    getNewStories(maxStory.storyId, max(days3, maxStory.updatedAt))
                                }
                            }
                        } catch (e: Exception) {
                            debugToast("Min max error")
                            e.printStackTrace()
                        }
                    }
                }
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

    fun setSavedStoryIds(savedStoryIds: ArrayList<Int>) {
        _savedStoryIdLiveData.value = savedStoryIds
    }

    fun setNotifyStories(_notifyStories: List<Payload>) {
        println("12245 notify api req from setNotify")
        _notifyStoriesLiveData.value = _notifyStories.sortedBy { it.storyId }
    }

    fun getUserPreferences() {
        viewModelScope.launch {
            newsRepository.getUserPrefrences().onEach {
                when (it) {
                    is DataState.Success -> {
                         val userPreferences = it.data
                        Log.i("getUserPreferences", "${it.data.categories}")
                        _userCategoryLiveData.value = userPreferences.categories
                        _savedStoryIdLiveData.value = userPreferences.saved
                        println("saved stories2 userpref ${_savedStoryIdLiveData.value}")
                        _notifyStoriesLiveData.value = userPreferences.notify
                        if(userPreferences.notify !=null)
                        _notifyStoriesLiveData.value = userPreferences.notify.sortedBy {it.storyId  }

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
        getNewStories(0, days3)
    }

    private val _newsUpdateState = MutableLiveData<DataState<List<Story>>>()
    val newsUpdateState: LiveData<DataState<List<Story>>>
        get() = _newsUpdateState

    inline fun List<Story>.checkIfHasThisStoryId(storyId: Int): Boolean = this.contains(Story(category = 0, storyId = storyId,  updatedAt = 0, events = listOf(), viewCount = 0))


    fun updateNews(storyId: Int, maxDateTime: Long) {
        urlToRequest  = "http://13.235.50.53/existing"
        viewModelScope.launch {
            newsRepository.updateExistingStories(NewsRequest(NewstaApp.access_token!!, NewstaApp.ISSUER_NEWSTA, storyId, getMaxDate(maxDateTime))).onEach {
                _newsUpdateState.value = it

                when(it){
                    is DataState.Success -> {

                        val updatedStories = it.data
                        val allStories = stories.toMutableList()

                        refreshState.value = false
                        changeDatabaseState(isDatabaseEmpty = false)
                        if(updatedStories!=null) {
                            updatedStories.forEach {
                                val indexOfUpdatedStory =
                                    allStories.toList().getIndexByStoryId(it.storyId)
                                if (indexOfUpdatedStory > -1) {
                                    allStories.removeAt(indexOfUpdatedStory)
                                }

                            }
                        }


                        if(allStories.isNotEmpty())
                            stories = allStories as ArrayList<Story>



                        _storiesLiveData.value = getMapOfStories(stories, ArrayList(updatedStories))
                            val list = arrayListOf<Payload>()
                        val origList = notifyStoriesLiveData.value
                        println("notifystory onupdate $origList")
                        if(origList != null){
                            val stories = it.data

                            origList.forEach {
                                if(stories.checkIfHasThisStoryId(it.storyId)){
                                    list.add(Payload(storyId = it.storyId, read = ArticleState.UNREAD))
                                }else{
                                    list.add(it)
                                }
                            }

                        }
                        println("notifystory afterupdate $list")
                        saveNotifyStories(list, "existing update")
                    }
                    is DataState.Error -> {
                        refreshState.value = false
                        Log.i("newsDataState", " errror ${it.exception}")
                    }
                    is DataState.Loading -> {
                        refreshState.value = true
                        Log.i("newsUpdateDataState", " loding")
                        debugToast("newsUpdateDataState: loading")
                    }
                    is DataState.Extra -> {
                        refreshState.value = false
                        try {
                            println("EXTRA DB DATA ---> ${it.data}")
                            if (it.data != null)
                                maxStory = it.data
                            extras = arrayListOf(it.data)
                            Log.i(
                                "newsDataState",
                                " EXTRA MAX ${maxStory.storyId} ${maxStory.updatedAt}"
                            )
                            if (!isRefreshedByDefault) {
                                isRefreshedByDefault = true
                                getNewStories(maxStory.storyId, max(days3, maxStory.updatedAt))
                            }
                        } catch (e: Exception) {
                            debugToast("Min max error")
                            e.printStackTrace()
                        }
                    }
                }

            }.launchIn(viewModelScope)
        }
    }

    private val _minMaxStoryState = MutableLiveData<DataState<MaxStoryAndUpdateTime>>()
    val minMaxStoryState: LiveData<DataState<MaxStoryAndUpdateTime>>
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
        Log.i("12245 sources call", "done")
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

    fun shownSwipeLeftDialog(shownSwipeLeftDialog: Boolean) {
        viewModelScope.launch {
            preferences.shownSwipeLeftDialog(shownSwipeLeftDialog)
        }
    }

    fun getCategories() {

        viewModelScope.launch {
            println("NEWSTA APP: ${NewstaApp.access_token}")
            val request = CategoryRequest(NewstaApp.access_token!!, NewstaApp.ISSUER_NEWSTA)
            newsRepository.getCategories(request).onEach {
                when (it) {
                    is DataState.Success -> {
                        Log.i("TAG", "onActivityCreated: CategoryDatState Success")
                        var newCategories = it.data as ArrayList<Category>
                        _categoryLiveData.value = newCategories
                        println("CATEGORY KAA NAYA VALUE --> ${categoryLiveData.value}")
                    }
                    is DataState.Error -> {
                        Log.i("TAG", "onActivityCreated: CategoryDatState Error")
                        checkIfUnauthorized(it)
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
                        if(list.isEmpty() && !_savedStoryIdLiveData.value.isNullOrEmpty()){
                            val list = ArrayList<Int>()
                            savedStoryIdLiveData.value?.let { it1 -> list.addAll(it1) }
                           getStoriesByIds(list, true){
                               Log.i("saved stories", " aya hai value set hone k liye")
                               _savedStoriesList.value = it
                           }
                        }else{
                        _savedStoriesList.value = it.data
                        }
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

    fun getStoriesByIds(ids: ArrayList<Int>, shouldInsertInSavedStoriesDB: Boolean = false, assignTask: (ArrayList<SavedStory>) -> Unit ){
        viewModelScope.launch {
            newsRepository.getStoriesByIds(savedStoryIds = ids,shouldInsertInSavedStoriesDB = shouldInsertInSavedStoriesDB).onEach {
                when(it){
                    is DataState.Success -> {
                        assignTask(it.data)
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
        Log.i("TOAST", message)
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


    fun saveNotifyStories(notifyStories: ArrayList<Payload>, from: String) {
        var intialNotifyStories = notifyStoriesLiveData.value

        if(intialNotifyStories.isNullOrEmpty()) intialNotifyStories = arrayListOf()

        println("12245 notify api req from $from"+ "$notifyStories")

        if(true){
        if(MainActivity.isConnectedToNetwork) {
            viewModelScope.launch {
                newsRepository.saveNotifyStoriesInServer(notifyStories).onEach {
                    when (it) {
                        is DataState.Success -> {
                            setNotifyStories(notifyStories)
                        }
                        is DataState.Error -> {
                            Log.i("TAG", "onActivityCreated: UserCategoryDatState Error ON SAVE ---> ${it.exception}")
                            if (it.statusCode == 101)
                                debugToast("Cannot save notify story")
                        }
                        is DataState.Loading -> {
                            Log.i("TAG", "onActivityCreated: SavedStoryDatState ON SAVE loading")
                        }
                    }
                }.launchIn(viewModelScope)
            }
        } else {
            toast("Please connect to network to save changes.")
        }}else{
            Log.i("12245 NotifyStories","Not any change orig: $intialNotifyStories and newList $notifyStories")
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
    private fun getMaxDate(_maxDateTime: Long): String {
        var maxDateTime =  _maxDateTime

        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val dateIst = sdf.format(maxDateTime)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        val dateUtc = sdf.format(maxDateTime)
        println("DATE IST: $dateIst")
        println("DATE UTC: $dateUtc")
        return dateUtc
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {}
    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {}

    fun List<Story>.getIndexByStoryId(storyId: Int): Int = this.indexOf(Story(category = 0, storyId = storyId,  updatedAt = 0, events = listOf(), viewCount = 0))

    var selectedDetailsPageData: DetailsPageData = DetailsPageData(0,0)

    fun setDetailsBottomNavInterface(detailsBottomNavInterface: DetailsBottomNavInterface) {
        newsRepository.detailsBottomNavInterface = detailsBottomNavInterface
    }

    companion object {
        var stories = ArrayList<Story>()
        var isRefreshedByDefault = false
    }

}
