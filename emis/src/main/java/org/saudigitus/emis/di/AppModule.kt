package org.saudigitus.emis.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import org.hisp.dhis.android.core.D2
import org.saudigitus.emis.data.Sdk
import org.saudigitus.emis.data.impl.AppConfigManagerImpl
import org.saudigitus.emis.data.impl.DataManagerImpl
import org.saudigitus.emis.data.local.AppConfigManager
import org.saudigitus.emis.data.local.DataManager
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesDataManager(
        @ApplicationContext context: Context,
        d2: D2
    ): DataManager = DataManagerImpl(context, d2)

    @Provides
    @Singleton
    fun providesAppConfigManager(
        @ApplicationContext context: Context,
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): AppConfigManager = AppConfigManagerImpl(context, ioDispatcher)
}