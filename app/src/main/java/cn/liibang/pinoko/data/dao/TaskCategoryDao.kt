package cn.liibang.pinoko.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import cn.liibang.pinoko.data.entity.TaskCategoryPO
import cn.liibang.pinoko.model.TaskCategoryVO
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskCategoryDao {

    @Query("select id, name, color, sort, created_at as createdAt from task_category")
    fun selectAll(): Flow<List<TaskCategoryVO>>

    @Insert
    suspend fun save(taskCategoryPO: TaskCategoryPO)
}