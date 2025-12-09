package com.example.grampet.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.grampet.R
import com.example.grampet.repository.PostRepository
import com.example.grampet.repository.PostRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class Post(
    val id: Int,
    val username: String,
    val model: Any, // Can be a URL (String) or a Bitmap
    val description: String
)

class PostsViewModel(private val postRepository: PostRepository) : ViewModel() {

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts = _posts.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private var nextPostId = 0

    init {
        loadPosts()
    }

    fun loadPosts() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val postResponses = postRepository.getPosts()
                val convertedPosts = postResponses.map {
                    Post(
                        id = it.id,
                        username = it.username,
                        model = it.imageUrl,
                        description = it.description
                    )
                }
                _posts.value = convertedPosts
                nextPostId = (convertedPosts.maxOfOrNull { it.id } ?: 0) + 1
            } catch (e: Exception) {
                loadFallbackPosts()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun loadFallbackPosts() {
        val fallbackPosts = listOf(
            Post(1, "baltazar_dev", R.drawable.img_5, "¡Mi primera foto en Grampet!"),
            Post(2, "michi_aventurero", R.drawable.img_2, "Explorando nuevos horizontes."),
            Post(3, "firulais_viajero", R.drawable.img_1, "El mejor día en el parque."),
        )
        _posts.value = fallbackPosts
        nextPostId = fallbackPosts.maxOf { it.id } + 1
    }

    fun addPost(bitmap: Bitmap, description: String) {
        val newPost = Post(
            id = nextPostId++,
            username = "baltazar_dev", // Or get from a user session
            model = bitmap,
            description = description
        )
        _posts.value = listOf(newPost) + _posts.value
    }

    // Factory to create PostsViewModel with its dependencies
    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                // Create the real repository
                val repository = PostRepositoryImpl()
                return PostsViewModel(repository) as T
            }
        }
    }
}
