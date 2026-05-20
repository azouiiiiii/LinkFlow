package com.example.linkflow.jump

object DeepLinkRoutes {

    // -------------------------
    // 微信
    // -------------------------
    fun wechatHome(): String {
        return "weixin://"
    }

    // -------------------------
    // B站
    // -------------------------
    fun bilibiliHome(): String {
        return "bilibili://"
    }

    fun bilibiliVideo(videoId: String): String {
        return "bilibili://video/$videoId"
    }

    fun bilibiliUser(uid: String): String {
        return "bilibili://space/$uid"
    }

    // -------------------------
    // 知乎
    // -------------------------
    fun zhihuHome(): String {
        return "zhihu://"
    }

    fun zhihuQuestion(questionId: String): String {
        return "zhihu://question/$questionId"
    }

    fun zhihuUser(userId: String): String {
        return "zhihu://people/$userId"
    }

    // -------------------------
    // 支付宝
    // -------------------------
    fun alipayHome(): String {
        return "alipays://platformapi/startapp"
    }

    // -------------------------
    // 腾讯会议
    // -------------------------
    fun tencentMeetingHome(): String {
        return "wemeet://"
    }

    fun tencentMeeting(meetingCode: String): String {
        return "wemeet://page/inmeeting?meeting_code=$meetingCode"
    }
}