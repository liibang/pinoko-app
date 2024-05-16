package cn.liibang.pinoko.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cn.liibang.pinoko.data.StringUUID
import cn.liibang.pinoko.data.entity.FocusRecordPO
import cn.liibang.pinoko.data.entity.Operation
import cn.liibang.pinoko.data.entity.Table
import cn.liibang.pinoko.di.RecordChange
import cn.liibang.pinoko.model.FocusRecordVO
import cn.liibang.pinoko.ui.screen.focus.FocusTodayData
import cn.liibang.pinoko.ui.screen.stats.FocusDurationWeekStatVO
import cn.liibang.pinoko.ui.screen.stats.TomatoFocusAchievementVO
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface FocusRecordDao {

    @RecordChange(table = Table.FOCUS_RECORD, operation = Operation.INSERT)
    @Insert
    suspend fun insert(po: FocusRecordPO)

    @RecordChange(table = Table.FOCUS_RECORD, operation = Operation.UPDATE)
    @Query("update focus_record set note = :note, task_id = :taskId where id = :id")
    suspend fun modify(id: StringUUID, note: String, taskId: StringUUID?)

    @RecordChange(table = Table.FOCUS_RECORD, operation = Operation.DELETE)
    @Query("delete from focus_record where id = :id")
    suspend fun delete(id: StringUUID)

    @Query("update focus_record set task_id = null where task_id = :taskId")
    suspend fun _unlinkTask(taskId: StringUUID)

    @Query("update focus_record set task_id = null where task_id in (:taskIds)")
    suspend fun _unlinkTaskByTaskIds(taskIds: List<String>)


    @Query("select * from focus_record where id = :id")
    suspend fun selectById(id: StringUUID): FocusRecordPO?

    @Query("select * from focus_record")
    fun selectList(): Flow<List<FocusRecordPO>>


    @Query("select count(id) as tomatoCount, sum(focus_duration) as totalDuration from focus_record where start_at > :today")
    fun countTodayData(today: LocalDateTime): Flow<FocusTodayData>


    @Query(
        """
        select r.id, r.start_at as startAt, 
                r.end_at as endAt, r.task_id as taskId, t.name as taskName,
                r.focus_duration as focusDuration, r.note 
        from focus_record r left join task t on R.task_id = t.id
    """
    )
    fun selectVOList(): Flow<List<FocusRecordVO>>


    @Query("select count(id) as tomatoCount, sum(focus_duration) as focusDuration from focus_record where start_at between :startAt and :endAt")
    fun countAchievement(
        startAt: LocalDateTime,
        endAt: LocalDateTime
    ): Flow<TomatoFocusAchievementVO>


    //  TODO
    @Query(
        """
        SELECT 
          CASE strftime('%w', datetime(start_at / 1000, 'unixepoch'))
            WHEN '0' THEN '7'
            WHEN '1' THEN '1'
            WHEN '2' THEN '2'
            WHEN '3' THEN '3'
            WHEN '4' THEN '4'
            WHEN '5' THEN '5'
            WHEN '6' THEN '6'
          END AS weekValue,
          sum(focus_duration) AS focusDuration
        FROM 
          focus_record
        WHERE 
          start_at IS NOT NULL
          AND
          start_at between :startAt AND :endAt
        GROUP BY 
          weekValue
        ORDER BY 
          CASE strftime('%w', datetime(start_at / 1000, 'unixepoch'))
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
    fun countFocusDurationOnWeek(
        startAt: LocalDateTime,
        endAt: LocalDateTime
    ): Flow<List<FocusDurationWeekStatVO>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun batchInsert(focusRecords: List<FocusRecordPO>)

    @Query("delete from focus_record")
    suspend fun deleteAll()

}