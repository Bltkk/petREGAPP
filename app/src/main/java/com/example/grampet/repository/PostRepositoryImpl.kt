package com.example.grampet.repository

import com.example.grampet.remote.RetrofitInstance
import com.example.grampet.remote.model.PostResponse

/**
 * Default implementation of [PostRepository].
 * This class is responsible for fetching posts from the network.
 */
class PostRepositoryImpl : PostRepository {
    override suspend fun getPosts(): List<PostResponse> {
        return RetrofitInstance.apiService.getPosts()
    }
}
