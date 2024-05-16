package cn.liibang.pinoko.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import cn.liibang.pinoko.data.dao.ChangeLogDao
import cn.liibang.pinoko.data.dao.CourseDao
import cn.liibang.pinoko.data.dao.CourseDetailDao
import cn.liibang.pinoko.data.dao.FocusRecordDao
import cn.liibang.pinoko.data.dao.HabitDao
import cn.liibang.pinoko.data.dao.SettingDao
import cn.liibang.pinoko.data.dao.TaskCategoryDao
import cn.liibang.pinoko.data.dao.TaskDao
import cn.liibang.pinoko.data.dao.TermDao
import cn.liibang.pinoko.data.entity.ChangeLogPO
import cn.liibang.pinoko.data.entity.CourseDetailPO
import cn.liibang.pinoko.data.entity.CoursePO
import cn.liibang.pinoko.data.entity.FocusRecordPO
import cn.liibang.pinoko.data.entity.HabitPO
import cn.liibang.pinoko.data.entity.SettingPO

import cn.liibang.pinoko.data.entity.TaskCategoryPO
import cn.liibang.pinoko.data.entity.TaskPO
import cn.liibang.pinoko.data.entity.TermPO

@Database(
    entities =
    [
        TaskPO::class,
        TaskCategoryPO::class,
        TermPO::class,
        CoursePO::class,
        CourseDetailPO::class,
        ChangeLogPO::class,
        FocusRecordPO::class,
        SettingPO::class,
        HabitPO::class,
    ], version = 27
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun taskCategoryDao(): TaskCategoryDao
    abstract fun termDao(): TermDao
    abstract fun courseDao(): CourseDao
    abstract fun changeLogDao(): ChangeLogDao
    abstract fun courseDetailDao(): CourseDetailDao
    abstract fun focusRecordDao(): FocusRecordDao
    abstract fun settingDao(): SettingDao
    abstract fun habitDao(): HabitDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun init(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pinoko"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}