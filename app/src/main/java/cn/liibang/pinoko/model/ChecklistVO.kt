package cn.liibang.pinoko.model

enum class ChecklistType {
    SMART, USER
}

data class ChecklistVO(
    val checklistId: Long,
    val name: String,
    val icon: String?,
    val type: ChecklistType
)