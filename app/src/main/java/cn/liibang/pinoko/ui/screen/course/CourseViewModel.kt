package cn.liibang.pinoko.ui.screen.course

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.liibang.pinoko.data.StringUUID
import cn.liibang.pinoko.data.dao.CourseDao
import cn.liibang.pinoko.data.dao.CourseDetailDao
import cn.liibang.pinoko.data.dao.SettingDao
import cn.liibang.pinoko.data.dao.TaskDao
import cn.liibang.pinoko.data.entity.CourseDetailPO
import cn.liibang.pinoko.data.entity.CoursePO
import cn.liibang.pinoko.ui.support.generateUUID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class CourseViewModel @Inject constructor(
    private val taskDao: TaskDao,
    private val courseDao: CourseDao,
    private val courseDetailDao: CourseDetailDao,
    private val settingDao: SettingDao
) : ViewModel() {

//    val setting = settingDao().selectOne()
//        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    suspend fun fetchById(id: StringUUID): CoursePO {
        return courseDao.selectById(id)!! // TODO 做防御性编程
    }

    suspend fun fetchCourseDetails(id: StringUUID): List<CourseDetailPO> {
        return courseDetailDao.selectListByCourseId(id)
    }



    fun saveOrUpdate(course: CoursePO, courseDetailList: List<CourseDetailPO>) {
        viewModelScope.launch {
            val needInsertDetailList = courseDetailList.map {
                it.copy(
                    id = generateUUID(),
                    createdAt = LocalDateTime.now(),
                    updatedAt = LocalDateTime.now()
                )
            }
            if (course.id.isEmpty()) {
                // 新增
                val courseId = generateUUID()
                courseDao.insert(
                    course.copy(
                        id = courseId,
                        createdAt = LocalDateTime.now(),
                        updatedAt = LocalDateTime.now()
                    )
                )
                needInsertDetailList.forEach {
                    courseDetailDao.insert(it.copy(courseId = courseId))
                }
            } else {
                // 删除
                courseDao.update(course.copy(updatedAt = LocalDateTime.now()))
                courseDetailDao._delete(course.id)
                needInsertDetailList.forEach { courseDetailDao.insert(it.copy(courseId = course.id)) }
            }
        }
    }

    fun deleteCourseByCourseId(courseId: StringUUID) {
        viewModelScope.launch {
            courseDao.deleteById(courseId)
            courseDetailDao._delete(courseId)
        }
    }


}