package cn.liibang.pinoko.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import cn.liibang.pinoko.data.StringUUID
import cn.liibang.pinoko.data.entity.CoursePO
import cn.liibang.pinoko.data.entity.Operation
import cn.liibang.pinoko.data.entity.Table
import cn.liibang.pinoko.di.RecordChange
import kotlinx.coroutines.flow.Flow



@Dao
interface CourseDao {

    @RecordChange(table = Table.COURSE, operation = Operation.INSERT)
    @Insert
    suspend fun insert(po: CoursePO)

    @RecordChange(table = Table.COURSE, operation = Operation.UPDATE)
    @Update
    suspend fun update(po: CoursePO)

    @RecordChange(table = Table.COURSE, operation = Operation.DELETE)
    @Query("delete from course where id = :courseId")
    suspend fun deleteById(courseId: StringUUID)

//    @RecordChange(table = Table.COURSE, operation = Operation.DELETE)
    @Query("delete from course where term_id = :termId")
    suspend fun _deleteByTermId(termId: StringUUID)





    @Query("select * from course where id = :id")
    suspend fun selectById(id: StringUUID): CoursePO?

    @Query("select * from course")
    fun selectList(): Flow<List<CoursePO>>

    @Query("select * from course where term_id = :termId")
    suspend fun selectByTermId(termId: String): List<CoursePO>

    @Query("select * from course where term_id = :termId")
    fun selectFlowByTermId(termId: String): Flow<List<CoursePO>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun batchInsert(courses: List<CoursePO>)



    @Query("delete from course")
    suspend fun deleteAll()

}