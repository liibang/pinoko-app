package cn.liibang.pinoko.di

import android.content.Context
import android.os.Build
import android.os.VibratorManager
import androidx.annotation.RequiresApi
import cn.liibang.pinoko.service.AlarmScheduler
import cn.liibang.pinoko.service.AlarmSchedulerImpl
import cn.liibang.pinoko.data.AppDatabase
import cn.liibang.pinoko.service.FocusNotifier
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAlarmScheduler(@ApplicationContext context: Context): AlarmScheduler {
        return AlarmSchedulerImpl(context)
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.init(context)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    @Provides
    @Singleton
    fun provideVibrator(@ApplicationContext context: Context): FocusNotifier {
        return FocusNotifier(
            context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager,
            context
        )
    }



}


