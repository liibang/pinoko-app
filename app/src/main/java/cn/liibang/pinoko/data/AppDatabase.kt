package cn.liibang.pinoko.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import cn.liibang.pinoko.data.dao.TaskCategoryDao
import cn.liibang.pinoko.data.dao.TaskDao

import cn.liibang.pinoko.data.entity.TaskCategoryPO
import cn.liibang.pinoko.data.entity.TaskPO

@Database(
    entities =
    [
        TaskPO::class, TaskCategoryPO::class
    ], version = 5
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun taskCategoryDao(): TaskCategoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase() = INSTANCE!!

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