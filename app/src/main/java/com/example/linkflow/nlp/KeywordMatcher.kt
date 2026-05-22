package com.example.linkflow.nlp

import com.example.linkflow.AppType

object KeywordMatcher {

    private val appKeywords = mapOf(
        AppType.TENCENT_MEETING to listOf(
            "腾讯会议", "会议", "开会", "meeting", "视频会议", "线上会议", "在线会议"
        ),
        AppType.BILIBILI to listOf(
            "B站", "bilibili", "b站", "视频", "BV号", "追番", "番剧", "弹幕", "Bv号", "bv号"
        ),
        AppType.ZHIHU to listOf(
            "知乎", "问题", "回答", "文章", "专栏"
        ),
        AppType.ALIPAY to listOf(
            "支付宝", "支付", "转账", "红包", "扫码", "付款"
        ),
        AppType.WECHAT to listOf(
            "微信", "wechat", "聊天", "朋友圈", "语音", "发消息"
        ),
        AppType.BROWSER to listOf(
            "网页", "浏览器", "搜索", "http", "www", "链接", "打开链接", "网址"
        )
    )

    fun matchAppType(content: String): AppType? {
        if (content.isBlank()) return null

        for ((appType, keywords) in appKeywords) {
            for (keyword in keywords) {
                if (content.contains(keyword, ignoreCase = true)) {
                    return appType
                }
            }
        }
        return null
    }
}
