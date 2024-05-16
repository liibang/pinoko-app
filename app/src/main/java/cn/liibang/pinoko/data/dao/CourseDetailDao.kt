package cn.liibang.pinoko.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cn.liibang.pinoko.data.StringUUID
import cn.liibang.pinoko.data.entity.CourseDetailPO
import cn.liibang.pinoko.data.entity.Operation
import cn.liibang.pinoko.data.entity.Table
import cn.liibang.pinoko.di.RecordChange

@Dao
interface CourseDetailDao {

//    @RecordChange(table = Table.COURSE_DETAIL, operation = Operation.INSERT)
    @Insert
    suspend fun insert(po: CourseDetailPO)

//    @RecordChange(table = Table.COURSE_DETAIL, operation = Operation.DELETE)
    @Query("delete from course_detail where course_id = :courseId ")
    suspend fun _delete(courseId: StringUUID)

//    @RecordChange(table = Table.COURSE_DETAIL, operation = Operation.DELETE)
    @Query("delete from course_detail where course_id in (:courseIds)")
    suspend fun _deleteInCourseIds(courseIds: List<String>)


    @Query("select * from course_detail where course_id  = :id")
    suspend fun selectById(id: String): CourseDetailPO


    @Query("select * from course_detail where course_id in (:courseIds) ")
    suspend fun selectListInCourseId(courseIds: List<String>): List<CourseDetailPO>

    @Query("select * from course_detail where course_id in (:courseIds) ")
    fun selectListInCourseIdByFlow(courseIds: List<String>): kotlinx.coroutines.flow.Flow<List<CourseDetailPO>>

    @Query("select * from course_detail where course_id = :courseId ")
    suspend fun selectListByCourseId(courseId: StringUUID): List<CourseDetailPO>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun batchInsert(courseDetails: List<CourseDetailPO>)


    @Query("delete from course_detail")
    suspend fun deleteAll()

}