package cn.liibang.pinoko.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import cn.liibang.pinoko.data.StringUUID
import cn.liibang.pinoko.data.entity.Operation
import cn.liibang.pinoko.data.entity.Table
import cn.liibang.pinoko.data.entity.TaskCategoryPO
import cn.liibang.pinoko.di.RecordChange
import cn.liibang.pinoko.model.TaskCategoryRecordVO
import cn.liibang.pinoko.model.TaskCategoryVO
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface TaskCategoryDao {

    @RecordChange(table = Table.TASK_CATEGORY, operation = Operation.UPDATE)
    @Update
    suspend fun update(category: TaskCategoryPO)

    @RecordChange(table = Table.TASK_CATEGORY, operation = Operation.INSERT)
    @Insert
    suspend fun save(taskCategoryPO: TaskCategoryPO)

    @RecordChange(table = Table.TASK_CATEGORY, operation = Operation.UPDATE)
    @Transaction
    @Query("update task_category set sort = :sort, updatedAt = :updateTime where id = :id")
    suspend fun updateSort(id: String, sort: Int, updateTime: LocalDateTime)

    @RecordChange(table = Table.TASK_CATEGORY, operation = Operation.DELETE)
    @Query("delete from task_category where id = :id")
    suspend fun delete(id: StringUUID)


//    @Query("select id, name, color, sort, createdAt as createdAt from task_category order by sort")
//    fun selectAll(): Flow<List<TaskCategoryVO>>


    @Query("""
        select c.id as id, c.name, c.color, c.sort, c.createdAt, count(t.id) as taskCount
        from task_category c left join task t on c.id = t.category_id
        group by c.id order by c.sort
    """)
    fun selectAndCountTask(): Flow<List<TaskCategoryVO>>

    @Query("SELECT MAX(sort) FROM task_category")
    suspend fun selectMaxSort(): Int

    @Query("select * from task_category where id = :id")
    suspend fun selectById(id: StringUUID): TaskCategoryPO

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun batchInsert(taskCategories: List<TaskCategoryPO>)



    @Query("delete from task_category")
    suspend fun deleteAll()
}