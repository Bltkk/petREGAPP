package com.example.grampet.repository

import com.example.grampet.remote.model.PostResponse

/**
 * A fake implementation of [PostRepository] for use in tests.
 * This allows us to control the data returned without making real network calls.
 */
class FakePostRepository : PostRepository {

    private var posts: List<PostResponse> = emptyList()
    private var shouldReturnError = false

    fun setShouldReturnError(value: Boolean) {
        shouldReturnError = value
    }

    fun setPosts(postsToSet: List<PostResponse>) {
        posts = postsToSet
    }

    override suspend fun getPosts(): List<PostResponse> {
        if (shouldReturnError) {
            throw RuntimeException("Simulated network error")
        }
        return posts
    }
}
