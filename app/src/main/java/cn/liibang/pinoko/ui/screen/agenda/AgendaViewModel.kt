package cn.liibang.pinoko.ui.screen.agenda

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.liibang.pinoko.data.AppDatabase
import cn.liibang.pinoko.data.dao.CourseDao
import cn.liibang.pinoko.data.dao.CourseDetailDao
import cn.liibang.pinoko.data.dao.SettingDao
import cn.liibang.pinoko.data.dao.TaskDao
import cn.liibang.pinoko.data.dao.TermDao
import cn.liibang.pinoko.data.entity.CourseDetailPO
import cn.liibang.pinoko.data.entity.CoursePO
import cn.liibang.pinoko.data.entity.SettingPO
import cn.liibang.pinoko.data.entity.TaskPO
import cn.liibang.pinoko.data.entity.TermPO
import cn.liibang.pinoko.model.CourseDetailVO
import cn.liibang.pinoko.model.CourseVO
import cn.liibang.pinoko.model.TaskVO
import cn.liibang.pinoko.ui.support.calculateWeekNumber
import cn.liibang.pinoko.ui.support.getWeekDates
import cn.liibang.pinoko.ui.support.toTimestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject


@HiltViewModel
class AgendaViewModel @Inject constructor(
    private val taskDao: TaskDao,
    private val settingDao: SettingDao,
    private val termDao: TermDao,
    private val courseDao: CourseDao,
    private val courseDetailDao: CourseDetailDao,
) :
    ViewModel() {

    private val _selectedDay = MutableStateFlow(LocalDate.now())
    val selectedDay = _selectedDay.asStateFlow()

    fun changeSelectedDay(day: LocalDate = selectedDay.value) {
        viewModelScope.launch { _selectedDay.value = day }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val tasksOfAWeek: StateFlow<List<TaskPO>> = _selectedDay
        .flatMapLatest {
            taskDao.selectByWeekRange(
                it.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)),
                it.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)),
            )
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, listOf())

    @OptIn(ExperimentalCoroutinesApi::class)
    val tasks: StateFlow<List<TaskVO>> = _selectedDay
        .flatMapLatest {
            taskDao.selectByDueDate(it.toTimestamp())
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, listOf())


    @OptIn(ExperimentalCoroutinesApi::class)
    val coursesOfAWeek: StateFlow<List<CourseVO>> =
        combine(_selectedDay, settingDao.selectOneByFlow())
        { currentSelectedDay, setting ->
            // 查询学期
            val term = fetchTerm(setting) ?: return@combine MutableStateFlow(listOf())
            // 查询课程流
            val coursesFlow = courseDao.selectFlowByTermId(term.id)
            // 查询课程详情流
            val courseDetailsFlow = fetchCourseDetailFlow(coursesFlow)
            // 解析课程时间获取对应的课程VO
            combineCourseAndCourseDetail(coursesFlow, courseDetailsFlow, term, currentSelectedDay)
        }.flatMapLatest{it}.stateIn(viewModelScope, SharingStarted.Eagerly, listOf())

    private fun combineCourseAndCourseDetail(
        coursesFlow: Flow<List<CoursePO>>,
        courseDetailsFlow: Flow<List<CourseDetailPO>>,
        term: TermPO,
        currentSelectedDay: LocalDate
    ) = combine(coursesFlow, courseDetailsFlow)
    { courses, courseDetails ->
        // 解析课程时间获取对应的课程VO
        val detailGroup = mapToCourseDetailGroup(term, currentSelectedDay, courseDetails)
        val courseVOs = covertToCourseVO(courses, detailGroup)
        courseVOs
    }

    private fun mapToCourseDetailGroup(
        term: TermPO,
        currentSelectedDay: LocalDate,
        courseDetails: List<CourseDetailPO>
    ): Map<String, List<CourseDetailPO>> {
        // 当前是第几周
        val currentWeekValue = calculateWeekNumber(term.startDate, currentSelectedDay)
        // 然后看看是不是在本周
        val filteredCourseDetailsInCurrentWeek = courseDetails.filter {
            currentWeekValue.toString() in it.weeks.split(",")
        }
        val detailGroup = filteredCourseDetailsInCurrentWeek.groupBy { it.courseId }
        return detailGroup
    }

    private suspend fun fetchTerm(setting: SettingPO): TermPO? {
        // 如果没有设置学期返回空
        val currentTermId = setting.termSetId
            ?: return null
        // 查询学期
        return termDao.selectById(currentTermId)
    }

    private suspend fun fetchCourseDetailFlow(coursesFlow: Flow<List<CoursePO>>): Flow<List<CourseDetailPO>> {
        val courseIds = coursesFlow.map { it -> it.map { it.id } }
        val courseDetailsFlow = courseIds.flatMapLatest { ids ->
            courseDetailDao.selectListInCourseIdByFlow(ids)
        }
        return courseDetailsFlow
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    val courses: StateFlow<List<CourseVO>> =
        combine(_selectedDay, settingDao.selectOneByFlow()) { currentSelectedDay, setting ->
            // 如果没有设置学期返回空
            val currentTermId = setting?.termSetId
                ?: return@combine MutableStateFlow(listOf())

            // 查询学期
            val term = termDao.selectById(currentTermId)
                ?: return@combine MutableStateFlow(listOf())

            val coursesFlow = courseDao.selectFlowByTermId(currentTermId)

            val courseIds = coursesFlow.map { it -> it.map { it.id } }

            val courseDetailsFlow = courseIds.flatMapLatest { ids ->
                courseDetailDao.selectListInCourseIdByFlow(ids)
            }

            combine(coursesFlow, courseDetailsFlow) { courses, courseDetails ->
                // 当前是星期几
                val dayOfWeekValue = currentSelectedDay.dayOfWeek.value
                // 当前是第几周
                val currentWeekValue = calculateWeekNumber(term.startDate, currentSelectedDay)
                // 过滤出 今天的
                val filteredCourseDetails = courseDetails.filter { it.dayOfWeek == dayOfWeekValue }
                // 然后看看是不是在本周
                val filteredCourseDetailsInCurrentWeek = filteredCourseDetails.filter {
                    currentWeekValue.toString() in it.weeks.split(",")
                }
                val detailGroup = filteredCourseDetailsInCurrentWeek.groupBy { it.courseId }

                val courseVOs = covertToCourseVO(courses, detailGroup)
                courseVOs
            }
        }
            .flatMapLatest { it }
            .stateIn(viewModelScope, SharingStarted.Eagerly, listOf())

    private fun covertToCourseVO(
        courses: List<CoursePO>,
        detailGroup: Map<String, List<CourseDetailPO>>
    ) = courses
        .filter { coursePO -> detailGroup[coursePO.id] != null }
        .map { coursePO ->
            val detailVOs = detailGroup[coursePO.id]!!
                .map {
                    CourseDetailVO(
                        id = it.id,
                        weeks = it.weeks,
                        dayOfWeek = it.dayOfWeek,
                        location = it.location,
                        teacher = it.teacher,
                        lessonStartAt = LocalTime.ofSecondOfDay(it.lessonStartAt.toLong()),
                        lessonEndAt = LocalTime.ofSecondOfDay(it.lessonEndAt.toLong()),
                        courseId = coursePO.id
                    )
                }
            CourseVO(
                id = coursePO.id,
                name = coursePO.name,
                color = coursePO.color,
                details = detailVOs
            )
        }


    val inboxSize: StateFlow<Int> = taskDao.countUndatedTask()
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)


    data class CountWeekDataVO(val date: LocalDate, val total: Int)

    @OptIn(ExperimentalCoroutinesApi::class)
    val hasDataDatesOnWeek = selectedDay
        .flatMapLatest {
            val weekDates = getWeekDates(it)
            taskDao.countAndGroupTaskByDueDate(weekDates.first(), weekDates.last())
        }.stateIn(viewModelScope, SharingStarted.Eagerly, listOf())

    fun isEventOnDayOfWeek(startAt: LocalDate, endAt: LocalDate): StateFlow<List<LocalDate>> {

        return taskDao.countAndGroupTaskByDueDate(startAt, endAt)
            .stateIn(viewModelScope, SharingStarted.Eagerly, listOf())


//        return appDatabase
//            .taskDao
//            .countTaskByDueDate(date)
//            .map { it != null }
//            .stateIn(viewModelScope, SharingStarted.Eagerly, false)
    }
}