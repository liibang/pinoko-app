package cn.liibang.pinoko.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Update
import androidx.sqlite.db.SupportSQLiteQuery
import cn.liibang.pinoko.data.entity.TaskCategoryPO
import cn.liibang.pinoko.data.entity.TaskPO
import cn.liibang.pinoko.model.TaskVO

import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface TaskDao {

    @Query(
        """
        SELECT
        t.id as id,
        t.name as name,
        t.due_date as dueDate,
        t.due_time as dueTime,
        t.reminder_time as remindTime,
        c.name as categoryName,
        c.color as categoryColor,
        t.priority,
        t.completed,
        t.created_at as createdAt,
        t.sort
        from task t left join task_category c on t.category_id = c.id
        where t.due_date = :dueDate
    """
    )
    fun selectByDueDate(dueDate: Long): Flow<List<TaskVO>>


    @Insert
    suspend fun insert(taskPO: TaskPO)

    @Update
    suspend fun update(taskPO: TaskPO)

    @Query("""
        update task set completed = :completed where id = :taskId 
    """)
    suspend fun updateTaskCompleted(taskId: String, completed: Boolean)

    @Query("""
        select id from task where due_date = :date limit 1 
    """)
    fun countTaskByDueDate(date: LocalDate): Flow<String?>

    @Query("""
        select count(*) from task where due_date is null
    """)
    fun countUndatedTask(): Flow<Int>

    @RawQuery(observedEntities = [TaskPO::class, TaskCategoryPO::class])
    fun selectAll(query: SupportSQLiteQuery): Flow<List<TaskVO>>

    @Query("""
        SELECT * from task t
        where t.id = :id
    """)
    suspend fun selectById(id: String): TaskPO


    @Query("delete from task where id = :id")
    suspend fun delete(id: String)
}