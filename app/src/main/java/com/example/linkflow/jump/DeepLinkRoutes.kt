package com.example.linkflow.jump

object DeepLinkRoutes {

    // B站
    // -------------------------

    fun bilibiliVideo(videoId: String): String {
        return "bilibili://video/$videoId"
    }

    fun bilibiliUser(uid: String): String {
        return "bilibili://space/$uid"
    }

    // -------------------------

    // 知乎
    // -------------------------

    fun zhihuQuestion(questionId: String): String {
        return "zhihu://question/$questionId"
    }

    fun zhihuUser(userId: String): String {
        return "zhihu://people/$userId"
    }

    // -------------------------

    // 腾讯会议
    // -------------------------
    fun tencentMeeting(meetingCode: String): String {
        return "wemeet://page/inmeeting?meeting_code=$meetingCode"
    }
}