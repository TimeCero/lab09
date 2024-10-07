package com.example.lab09.ui.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PostViewModel : ViewModel() {
    private val _posts = MutableLiveData<List<PostModel>>()
    val posts: LiveData<List<PostModel>> = _posts

    private val _selectedPost = MutableLiveData<PostModel?>()
    val selectedPost: LiveData<PostModel?> = _selectedPost

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://jsonplaceholder.typicode.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val service = retrofit.create(PostApiService::class.java)

    fun fetchPosts() {
        viewModelScope.launch {
            val fetchedPosts = service.getUserPosts()
            _posts.value = fetchedPosts
        }
    }

    fun fetchPostById(id: Int) {
        viewModelScope.launch {
            val post = service.getUserPostById(id)
            _selectedPost.value = post
        }
    }
}
