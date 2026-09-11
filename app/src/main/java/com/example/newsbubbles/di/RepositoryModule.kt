package com.example.newsbubbles.di

import com.example.newsbubbles.data.repository.NewsRepositoryImpl
import com.example.newsbubbles.domain.repository.NewsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Binds the domain [NewsRepository] interface to its data-layer implementation.
 *
 * Using @Binds (instead of @Provides) lets Hilt avoid creating a wrapper object —
 * it just maps the interface type directly to the concrete class.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindNewsRepository(impl: NewsRepositoryImpl): NewsRepository
}
