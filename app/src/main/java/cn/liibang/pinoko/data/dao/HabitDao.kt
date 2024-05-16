package cn.liibang.pinoko.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import cn.liibang.pinoko.data.StringUUID
import cn.liibang.pinoko.data.entity.CourseDetailPO
import cn.liibang.pinoko.data.entity.HabitPO
import cn.liibang.pinoko.data.entity.Operation
import cn.liibang.pinoko.data.entity.Table
import cn.liibang.pinoko.di.RecordChange
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface HabitDao {

    @RecordChange(table = Table.HABIT, operation = Operation.INSERT)
    @Insert
    suspend fun insert(po: HabitPO)

    @RecordChange(table = Table.HABIT, operation = Operation.UPDATE)
    @Update
    suspend fun update(po: HabitPO)

    @RecordChange(table = Table.HABIT, operation = Operation.DELETE)
    @Delete
    suspend fun delete(po: HabitPO)



    @Query("select * from habit where id = :id")
    suspend fun selectById(id: StringUUID): HabitPO?

    @Query("select * from habit where start_at <= :date")
    fun selectList(date: LocalDate): Flow<List<HabitPO>>


    @Query("select * from habit")
    fun selectAll(): Flow<List<HabitPO>>

    @Query("delete from habit")
    suspend fun deleteAll()


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun batchInsert(habits: List<HabitPO>)
}