package cn.liibang.pinoko.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import cn.liibang.pinoko.data.dao.TaskCategoryDao
import cn.liibang.pinoko.data.dao.TaskDao
import cn.liibang.pinoko.data.dao.TermDao

import cn.liibang.pinoko.data.entity.TaskCategoryPO
import cn.liibang.pinoko.data.entity.TaskPO
import cn.liibang.pinoko.data.entity.TermPO

@Database(
    entities =
    [
        TaskPO::class, TaskCategoryPO::class, TermPO::class
    ], version = 6
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun taskCategoryDao(): TaskCategoryDao
    abstract fun termDao(): TermDao

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