package cn.liibang.pinoko.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cn.liibang.pinoko.data.entity.ChangeLogPO
import cn.liibang.pinoko.data.entity.CoursePO
import java.sql.Timestamp

@Dao
interface ChangeLogDao {


//    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @Insert
    suspend fun insert(po: ChangeLogPO)

    @Query("delete from change_log where timestamp <= :oldTimestamp")
    suspend fun delete(oldTimestamp: Long)

    @Query("select * from change_log where timestamp <= :timestamp")
    suspend fun selectNeedSyncData(timestamp: Long): List<ChangeLogPO>


    @Query("delete from change_log")
    suspend fun deleteAll()

}