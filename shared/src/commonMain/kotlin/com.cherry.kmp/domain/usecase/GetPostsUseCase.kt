package com.cherry.kmp.domain.usecase

import com.cherry.kmp.domain.model.Post
import com.cherry.kmp.domain.repository.Repository
import com.cherry.kmp.domain.usecase.base.UseCase
import kotlinx.coroutines.CoroutineDispatcher

class GetPostsUseCase(
    private val repository: Repository, dispatcher: CoroutineDispatcher
) : UseCase<Unit, List<Post>>(dispatcher) {

    override suspend fun execute(params: Unit): List<Post> {
        return repository.getPosts()
    }
}