package cn.liibang.pinoko.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import cn.liibang.pinoko.data.entity.Operation
import cn.liibang.pinoko.data.entity.SettingPO
import cn.liibang.pinoko.data.entity.Table
import cn.liibang.pinoko.di.RecordChange
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingDao {

    @RecordChange(table = Table.SETTING, operation = Operation.UPDATE)
    @Update
    suspend fun update(po: SettingPO)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(po: SettingPO)

    @Query("select * from setting limit 1")
    fun selectOneByFlow(): Flow<SettingPO>

    @Query("select * from setting limit 1")
    suspend fun selectOne(): SettingPO


    @Query("delete from setting")
    suspend fun deleteAll()
}