package cn.liibang.pinoko.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Update
import androidx.sqlite.db.SupportSQLiteQuery
import cn.liibang.pinoko.data.StringUUID
import cn.liibang.pinoko.data.entity.Operation
import cn.liibang.pinoko.data.entity.Table
import cn.liibang.pinoko.data.entity.TaskCategoryPO
import cn.liibang.pinoko.data.entity.TaskPO
import cn.liibang.pinoko.di.RecordChange
import cn.liibang.pinoko.model.TaskVO
import cn.liibang.pinoko.ui.screen.stats.TaskAchievementVO
import cn.liibang.pinoko.ui.screen.stats.TaskWeekStatVO

import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalDateTime

@Dao
interface TaskDao {

    @RecordChange(table = Table.TASK, operation = Operation.INSERT)
    @Insert
    suspend fun insert(taskPO: TaskPO)


    @RecordChange(table = Table.TASK, operation = Operation.UPDATE)
    @Update
    suspend fun update(taskPO: TaskPO)

    @RecordChange(table = Table.TASK, operation = Operation.UPDATE)
    @Query(
        """
        update task set completed = :completed where id = :taskId 
    """
    )
    suspend fun updateTaskCompleted(taskId: String, completed: Boolean)


    @RecordChange(table = Table.TASK, operation = Operation.UPDATE)
    @Query("update task set priority = :priorityCode where id = :id")
    suspend fun updatePriority(id: StringUUID, priorityCode: Int)


    @RecordChange(table = Table.TASK, operation = Operation.DELETE)
    @Query("delete from task where id = :id")
    suspend fun delete(id: String)


//    @RecordChange(table = Table.TASK, operation = Operation.DELETE)
    // 因为有关联关系， 所以就不同步了
    @Query("delete from task where category_id = :categoryId")
    suspend fun _deleteByCategoryId(categoryId: StringUUID)

    @Query("select id from task where category_id = :categoryId")
    suspend fun selectIdsByCategoryId(categoryId: StringUUID): List<String>

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
        t.createdAt as createdAt,
        t.sort
        from task t left join task_category c on t.category_id = c.id
        where t.due_date = :dueDate
        order by t.completed, t.due_date desc, t.due_time desc
    """
    )
    fun selectByDueDate(dueDate: Long): Flow<List<TaskVO>>




    @Query(
        """
        select id from task where due_date = :date limit 1 
    """
    )
    fun countTaskByDueDate(date: LocalDate): Flow<String?>

    @Query(
        """
        select due_date as date from task where due_date between :startAt and :endAt group by due_date  
    """
    )
    fun countAndGroupTaskByDueDate(startAt: LocalDate, endAt: LocalDate): Flow<List<LocalDate>>


    @Query(
        """
        select count(*) from task where due_date is null
    """
    )
    fun countUndatedTask(): Flow<Int>

    @RawQuery(observedEntities = [TaskPO::class, TaskCategoryPO::class])
    fun selectAll(query: SupportSQLiteQuery): Flow<List<TaskVO>>

    @Query(
        """
        SELECT * from task t
        where t.id = :id
    """
    )
    suspend fun selectById(id: String): TaskPO


    @Query(
        """
        SELECT * from task t
        where t.id in (:ids)
    """
    )
    suspend fun selectByIds(ids: List<String>): List<TaskPO>


    @Query("select * from task where due_date between :startDate and :endDate")
    fun selectByWeekRange(startDate: LocalDate, endDate: LocalDate): Flow<List<TaskPO>>


    @Query("select sum(completed = 1) as completedCount, count(id) as taskCount from task where due_date between :startAt and :endAt")
    fun countTaskAchievement(startAt: LocalDateTime, endAt: LocalDateTime): Flow<TaskAchievementVO>


    @Query("select sum(completed = 1) from task")
    fun countCompletedTaskCount(): Flow<Int>

    //  TODO
    @Query(
        """
        SELECT 
          CASE strftime('%w', datetime(due_date / 1000, 'unixepoch'))
            WHEN '0' THEN '7'
            WHEN '1' THEN '1'
            WHEN '2' THEN '2'
            WHEN '3' THEN '3'
            WHEN '4' THEN '4'
            WHEN '5' THEN '5'
            WHEN '6' THEN '6'
          END AS weekValue,
          COUNT(*) AS taskCount
        FROM 
          task
        WHERE 
          due_date IS NOT NULL
          AND
          due_date between :startAt AND :endAt
        GROUP BY 
          weekValue
        ORDER BY 
          CASE strftime('%w', datetime(due_date / 1000, 'unixepoch'))
            WHEN '0' THEN 0
            WHEN '1' THEN 1
            WHEN '2' THEN 2
            WHEN '3' THEN 3
            WHEN '4' THEN 4
            WHEN '5' THEN 5
            WHEN '6' THEN 6
          END;
    """
    )
    fun countWorkloadOnWeek(
        startAt: LocalDateTime,
        endAt: LocalDateTime
    ): Flow<List<TaskWeekStatVO>>

    @Query("select count(id) from focus_record")
    fun countTotalTomatoCount(): Flow<Int>

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
        t.createdAt as createdAt,
        t.sort
        from task t left join task_category c on t.category_id = c.id
        order by t.completed, t.due_date desc, t.due_time desc
    """
    )
    suspend fun selectList(): List<TaskVO>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun batchInsert(tasks: List<TaskPO>)


    @Query("delete from task")
    suspend fun deleteAll()

}