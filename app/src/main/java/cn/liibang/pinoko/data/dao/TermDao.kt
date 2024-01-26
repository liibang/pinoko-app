package cn.liibang.pinoko.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import cn.liibang.pinoko.data.StringUUID
import cn.liibang.pinoko.data.entity.TermPO
import kotlinx.coroutines.flow.Flow

@Dao
interface TermDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(po: TermPO)

    @Query("select * from term where id = :id")
    suspend fun selectById(id: StringUUID): TermPO?

    @Query("select * from term")
    fun selectList(): Flow<List<TermPO>>

}