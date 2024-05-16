package cn.liibang.pinoko.http

import cn.liibang.pinoko.data.entity.CourseDetailPO
import cn.liibang.pinoko.data.entity.CoursePO
import cn.liibang.pinoko.data.entity.FocusRecordPO
import cn.liibang.pinoko.data.entity.HabitPO
import cn.liibang.pinoko.data.entity.SettingPO
import cn.liibang.pinoko.data.entity.TaskCategoryPO
import cn.liibang.pinoko.data.entity.TaskPO
import cn.liibang.pinoko.data.entity.TermPO
import cn.liibang.pinoko.ui.screen.focus.FeedbackDTO
import cn.liibang.pinoko.ui.screen.main.ChangeLogDTO
import cn.liibang.pinoko.ui.screen.member.ChangePasswordDTO
import cn.liibang.pinoko.ui.screen.sign.ForgetPasswordDTO
import cn.liibang.pinoko.ui.screen.sign.LoginDTO
import cn.liibang.pinoko.ui.screen.sign.RegisterDTO
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import java.time.LocalDateTime


data class MemberDO(
    val id: String,
    val avatar: String,
    val nickname: String,
    val email: String,
    val created: LocalDateTime,
)

data class LoginResult(val memberDO: MemberDO, val token: String, val settingPO: SettingPO)

data class GetCodeDTO(val email: String, val requestUUID: String, val type: Int)

interface AppApi {

    @POST("login")
    suspend fun login(@Body loginDTO: LoginDTO): ResultVO<LoginResult>

    @POST("getCode")
    suspend fun getCode(@Body dto: GetCodeDTO): ResultVO<Unit>

    @POST("register")
    suspend fun register(@Body dto: RegisterDTO): ResultVO<Unit>

    @POST("forgetThenChangePassword")
    suspend fun forgetThenChangePassword(@Body dto: ForgetPasswordDTO): ResultVO<Unit>

    @POST("changePassword")
    suspend fun changePassword(@Body changePasswordDTO: ChangePasswordDTO): ResultVO<Unit>

    @POST("feedback/submit")
    suspend fun feedback(@Body feedbackDTO: FeedbackDTO): ResultVO<FeedbackDTO>

    @Multipart
    @PUT("updateProfile")
    suspend fun updateProfile(
        @Part avatar: MultipartBody.Part,
        @Part("nickname") nickname: RequestBody
    ): ResultVO<MemberDO>

    @POST("sync")
    suspend fun syncData(@Body data: ChangeLogDTO): ResultVO<Unit>

    @POST("fetchData")
    suspend fun fetchData(): ResultVO<ServerData>
}


data class ServerData(// // 这里设置唯一索引
    val tasks: List<TaskPO> = listOf(),
    val taskCategories: List<TaskCategoryPO> = listOf(),
    val terms: List<TermPO> = listOf(),
    val courses: List<CoursePO> = listOf(),
    val courseDetails: List<CourseDetailPO> = listOf(),
    val focusRecords: List<FocusRecordPO> = listOf(),
    val habits: List<HabitPO> = listOf(),
)