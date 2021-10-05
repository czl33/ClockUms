package com.newczl.clockwidget.utils

import java.util.*

object TimeUtil {

    /**
     * 获取时间信息
     */
    fun getCurrentMinute(): Int =
        (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) * 60) + Calendar.getInstance().get(
            Calendar.MINUTE
        )

    fun getTime(time: Int): String = "${time / 60}:${time % 60}"

    fun formatTimeString(): String {
        return "${
            if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) > 12) {
                "下午"
            } else {
                "上午"
            }
        } ${
            Calendar.getInstance().run {
                "${
                    get(Calendar.HOUR_OF_DAY) % 24
                }:${
                    "${
                        if (get(Calendar.MINUTE) < 10) {
                            "0"
                        } else {
                            ""
                        }
                    }${get(Calendar.MINUTE)}"
                }"
            }
        }"
    }
}