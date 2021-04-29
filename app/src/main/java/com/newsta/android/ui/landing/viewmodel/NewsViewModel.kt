package com.newsta.android.ui.landing.viewmodel

import androidx.databinding.Observable
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.newsta.android.NewstaApp
import com.newsta.android.remote.data.NewsRequest
import com.newsta.android.repository.StoryRepository
import com.newsta.android.utils.models.DataState
import com.newsta.android.utils.models.Story
import com.newsta.android.utils.prefrences.UserPrefrences
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class NewsViewModel
@ViewModelInject
constructor(private val newsRepository: StoryRepository,
            private val preferences: UserPrefrences,
            @Assisted private val savedStateHandle: SavedStateHandle) : ViewModel(), Observable {

    private val _newsDataState = MutableLiveData<DataState<List<Story>>>()

    val newsDataState: LiveData<DataState<List<Story>>> = _newsDataState

    fun getAllNews() {

        viewModelScope.launch {
            val request = NewsRequest(NewstaApp.access_token!!, "newsta", 3000, "2021-04-11")
            newsRepository.getAllStories(newsRequest = request)
                    .onEach {

                        _newsDataState.value = it
                    }
                    .launchIn(viewModelScope)
        }
    }

    init {
        getAllNews()
    }

    //suspend fun insertNewsToDatabase(data: ArrayList<Data>) = newsRepository.insertNewsToDatabase(data)

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {}
    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {}

}
