package com.android.riafytest.di

import android.content.Context
import androidx.room.Room
import com.android.riafytest.BaseApplication
import com.android.riafytest.database.AppDatabase
import com.android.riafytest.database.AuthDataSourceImpl
import com.android.riafytest.database.AuthDatabaseSource
import com.android.riafytest.network.AuthNetworkDataSource
import com.android.riafytest.network.AuthNetworkDataSourceImpl
import com.android.riafytest.repository.MainRepository
import com.android.riafytest.repository.MainRepositoryImpl
import com.android.riafytest.retrofit.AuthService
import com.android.riafytest.util.AppPreferencesHelper
import com.android.riafytest.util.PreferencesHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton
import kotlin.annotation.AnnotationRetention.RUNTIME

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Qualifier
    @Retention(RUNTIME)
    annotation class RemoteAuthDataSource

    @Qualifier
    @Retention(RUNTIME)
    annotation class LocalAuthDataSource

    @Singleton
    @RemoteAuthDataSource
    @Provides
    fun authNetworkDataSource(authService: AuthService): AuthNetworkDataSource {
        return AuthNetworkDataSourceImpl(authService = authService)
    }

    @Singleton
    @LocalAuthDataSource
    @Provides
    fun provideAuthDatabaseSource(
        database: AppDatabase,
    ): AuthDatabaseSource {
        return AuthDataSourceImpl(
            listDao = database.listDao(),
        )
    }

    @Singleton
    @Provides
    fun provideApplication(@ApplicationContext app: Context): BaseApplication {
        return app as BaseApplication
    }

    @Singleton
    @Provides
    fun provideDataBase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "Tasks.db"
        ).build()
    }

    @Singleton
    @Provides
    fun provideAuthService(): AuthService {
        return AuthService.create()
    }

    @Singleton
    @Provides
    fun providePreferenceHelper(@ApplicationContext context: Context): AppPreferencesHelper {
        return PreferencesHelper(context)
    }

    /**
     * The binding for MainRepository is on its own module so that we can replace it easily in tests.
     */
    @Module
    @InstallIn(SingletonComponent::class)
    object MainRepositoryModule {
        @Singleton
        @Provides
        fun provideMainRepository(
            @AppModule.RemoteAuthDataSource remoteTasksDataSource: AuthNetworkDataSource,
            @AppModule.LocalAuthDataSource localTasksDataSource: AuthDatabaseSource
        ): MainRepository {
            return MainRepositoryImpl(
                remoteTasksDataSource, localTasksDataSource
            )
        }
    }
}
