package com.example.grampet.repository

import com.example.grampet.remote.model.PostResponse

/**
 * Interface for the data layer that provides access to posts.
 * This abstraction allows us to switch implementations (e.g., for testing).
 */
interface PostRepository {
    suspend fun getPosts(): List<PostResponse>
}
