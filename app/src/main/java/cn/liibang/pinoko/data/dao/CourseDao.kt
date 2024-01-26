package cn.liibang.pinoko.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import cn.liibang.pinoko.data.StringUUID
import cn.liibang.pinoko.data.entity.CoursePO
import cn.liibang.pinoko.data.entity.TermPO
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(po: CoursePO)

    @Query("select * from course where id = :id")
    suspend fun selectById(id: StringUUID): CoursePO?

    @Query("select * from course")
    fun selectList(): Flow<List<CoursePO>>

}