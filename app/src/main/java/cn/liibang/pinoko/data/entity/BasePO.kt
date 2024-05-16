package cn.liibang.pinoko.data.entity

import cn.liibang.pinoko.data.StringUUID
import java.time.LocalDateTime

interface BasePO {
    val id: StringUUID
    val createdAt: LocalDateTime
    val updatedAt: LocalDateTime
}