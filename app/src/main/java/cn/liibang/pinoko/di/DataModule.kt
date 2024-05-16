package cn.liibang.pinoko.di

import cn.liibang.pinoko.data.AppDatabase
import cn.liibang.pinoko.data.StringUUID
import cn.liibang.pinoko.data.dao.ChangeLogDao
import cn.liibang.pinoko.data.dao.CourseDao
import cn.liibang.pinoko.data.dao.CourseDetailDao
import cn.liibang.pinoko.data.dao.FocusRecordDao
import cn.liibang.pinoko.data.dao.HabitDao
import cn.liibang.pinoko.data.dao.SettingDao
import cn.liibang.pinoko.data.dao.TaskCategoryDao
import cn.liibang.pinoko.data.dao.TaskDao
import cn.liibang.pinoko.data.dao.TermDao
import cn.liibang.pinoko.data.entity.BasePO
import cn.liibang.pinoko.data.entity.ChangeLogPO
import cn.liibang.pinoko.data.entity.Operation
import cn.liibang.pinoko.data.entity.SettingPO
import cn.liibang.pinoko.data.entity.Table
import cn.liibang.pinoko.data.entity.TaskSortMode
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.reflect.Proxy
import java.time.Duration
import java.time.LocalDateTime
import javax.inject.Singleton

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class RecordChange(val table: Table, val operation: Operation)


/**
 * 数据库依赖模块
 */
@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideChangeLogDao(database: AppDatabase): ChangeLogDao {
        return database.changeLogDao()
    }

    @Provides
    @Singleton
    fun provideTaskDao(database: AppDatabase, changeLogDao: ChangeLogDao): TaskDao {
        return wrapperDaoProxy(database.taskDao(), changeLogDao)
    }

    @Provides
    @Singleton
    fun provideTaskCategoryDao(database: AppDatabase, changeLogDao: ChangeLogDao): TaskCategoryDao {
        return wrapperDaoProxy(database.taskCategoryDao(), changeLogDao)
    }

    @Provides
    @Singleton
    fun provideTermDao(database: AppDatabase, changeLogDao: ChangeLogDao): TermDao {
        return wrapperDaoProxy(database.termDao(), changeLogDao)
    }

    @Provides
    @Singleton
    fun provideCourseDao(database: AppDatabase, changeLogDao: ChangeLogDao): CourseDao {
        return wrapperDaoProxy(database.courseDao(), changeLogDao)
    }

    @Provides
    @Singleton
    fun provideCourseDetailDao(database: AppDatabase, changeLogDao: ChangeLogDao): CourseDetailDao {
        return wrapperDaoProxy(database.courseDetailDao(), changeLogDao)
    }

    @Provides
    @Singleton
    fun provideFocusRecordDao(database: AppDatabase, changeLogDao: ChangeLogDao): FocusRecordDao {
        return wrapperDaoProxy(database.focusRecordDao(), changeLogDao)
    }

    @Provides
    @Singleton
    fun provideSettingDao(database: AppDatabase, changeLogDao: ChangeLogDao): SettingDao {
        return wrapperDaoProxy(database.settingDao(), changeLogDao)
    }


    @Provides
    @Singleton
    fun provideHabitDao(database: AppDatabase, changeLogDao: ChangeLogDao): HabitDao {
        return wrapperDaoProxy(database.habitDao(), changeLogDao)
    }

    private inline fun <reified T : Any> wrapperDaoProxy(instance: T, changeLogDao: ChangeLogDao): T {
        return Proxy.newProxyInstance(
            T::class.java.classLoader,
            arrayOf(T::class.java)
        ) { _, method, args ->

            // 判断其他方法！？
            val result = method.invoke(instance, *(args ?: arrayOf()))

            // 异步插入 ?: 异常情况怎么处理？
            CoroutineScope(Dispatchers.IO).launch {
                // 注解拦截
                method.getAnnotation(RecordChange::class.java)?.let {
                    // TODO 需要判断参数类型
                    val targetUUID = when(val param = args[0]) {
                        is StringUUID -> param
                        is BasePO -> param.id
                        else -> throw RuntimeException("插入同步记录失败，无法获取正确的UUID，请检查配置是否正确")
                    }
                    // 针对不同的表记录数据的操作情况
                    // error is here
                    changeLogDao.insert(
                        ChangeLogPO(
                            uuid = targetUUID,
                            timestamp = System.currentTimeMillis(),
                            table = it.table,
                            operation = it.operation
                        )
                    )
                }
            }
            return@newProxyInstance result
        } as T
    }
}











