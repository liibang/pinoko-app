package cn.liibang.pinoko.di

import android.app.Application
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.components.SingletonComponent

@HiltAndroidApp
class App: Application() {
}

@Module
@InstallIn(SingletonComponent::class)
object AppModule {}