package cn.liibang.pinoko.data

import cn.liibang.pinoko.http.MemberDO
import cn.liibang.pinoko.ui.support.gson
import com.tencent.mmkv.MMKV


fun isLogin() = DataStore.getToken()?.isNotEmpty() ?: false



object DataStore {
    private val dataStore = MMKV.defaultMMKV()

    fun putMember(memberDO: MemberDO) {
        dataStore.encode("member", gson.toJson(memberDO))
    }

    fun getToken(): String? = dataStore.decodeString("token")

    fun saveToken(tokenValue: String) {
        dataStore.encode("token", tokenValue)
    }


    fun isPullData() = dataStore.decodeBool("isPullData", false)

    fun checkPullDataStatus() = dataStore.encode("isPullData", true)

    fun clearToken() {
        dataStore.remove("token")
    }

    fun getMemberInfo(): MemberDO {
        return dataStore.decodeString("member").let {
            gson.fromJson(it, MemberDO::class.java)
        }
    }


    // 同步相关
    fun getSyncTimestamp() {
        dataStore.decodeLong("sync_timestamp")
    }

    fun putSyncTimestamp(timestamp: Long) {
        dataStore.decodeLong("sync_timestamp", timestamp)
    }

}