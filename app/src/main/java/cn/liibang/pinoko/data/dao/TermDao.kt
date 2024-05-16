package cn.liibang.pinoko.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import cn.liibang.pinoko.data.StringUUID
import cn.liibang.pinoko.data.entity.Operation
import cn.liibang.pinoko.data.entity.Table
import cn.liibang.pinoko.data.entity.TermPO
import cn.liibang.pinoko.di.RecordChange
import kotlinx.coroutines.flow.Flow

@Dao
interface TermDao {

    @RecordChange(table = Table.TERM, operation = Operation.INSERT)
    @Insert
    suspend fun insert(po: TermPO)

    @RecordChange(table = Table.TERM, operation = Operation.UPDATE)
    @Update
    suspend fun update(po: TermPO)

    @RecordChange(table = Table.TERM, operation = Operation.DELETE)
    @Delete
    suspend fun delete(termPO: TermPO)


    @Query("select * from term where id = :id")
    suspend fun selectById(id: StringUUID): TermPO?

    @Query("select * from term")
    fun selectList(): Flow<List<TermPO>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun batchInsert(terms: List<TermPO>)

    @Query("delete from term")
    suspend fun deleteAll()
}