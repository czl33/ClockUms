import android.util.Log
import com.newczl.clockwidget.utils.TimeUtil
import com.newczl.clockwidget.utils.WorkState
import com.squareup.moshi.Json

data class TimeBean(
        @Json(name = "el")
        var el: El?,
        @Json(name = "err")
        var err: Int,
        @Json(name = "msg")
        var msg: Msg
)

data class El(
        @Json(name = "abandon")
        var abandon: Int?,
        @Json(name = "alertDescript")
        var alertDescript: String?,
        @Json(name = "alertReason")
        var alertReason: Int?,
        @Json(name = "alertStrHtmlStr")
        var alertStrHtmlStr: String?,
        @Json(name = "classId")
        var classId: Int?,
        @Json(name = "className")
        var className: String?,
        @Json(name = "forgetApplyOverwork")
        var forgetApplyOverwork: Int?,
        @Json(name = "forgetPunch")
        var forgetPunch: Int?,
        @Json(name = "hrDate")
        var hrDate: Int?,
        @Json(name = "id")
        var id: Int?,
        @Json(name = "intWeek")
        var intWeek: Int?,
        @Json(name = "isEarly")
        var isEarly: Int?,
        @Json(name = "isLater")
        var isLater: Int?,
        @Json(name = "isLocked")
        var isLocked: Boolean?,
        @Json(name = "leaveTime")
        var leaveTime: Int?,
        @Json(name = "monthDateType")
        var monthDateType: Int?,
        @Json(name = "monthTimeGroup")
        var monthTimeGroup: Boolean?,
        @Json(name = "moonwork1")
        var moonwork1: Int?,
        @Json(name = "moonwork2")
        var moonwork2: Int?,
        @Json(name = "moonwork3")
        var moonwork3: Int?,
        @Json(name = "overwork0")
        var overwork0: Int?,
        @Json(name = "overwork1")
        var overwork1: Int?,
        @Json(name = "overwork2")
        var overwork2: Int?,
        @Json(name = "pathStrHtmlStr")
        var pathStrHtmlStr: String?,
        @Json(name = "prolineId")
        var prolineId: Int?,
        @Json(name = "punchDataHtmlStr")
        var punchDataHtmlStr: String?,
        @Json(name = "PunchInt1")
        var punchInt1: String?,
        @Json(name = "PunchInt2")
        var punchInt2: String?,
        @Json(name = "PunchStr1")
        var punchStr1: String?,
        @Json(name = "PunchStr2")
        var punchStr2: String?,
        @Json(name = "splitLeaveId_0")
        var splitLeaveId0: Int?,
        @Json(name = "splitLeaveId_1")
        var splitLeaveId1: Int?,
        @Json(name = "splitLeaveId_10")
        var splitLeaveId10: Int?,
        @Json(name = "splitLeaveId_11")
        var splitLeaveId11: Int?,
        @Json(name = "splitLeaveId_12")
        var splitLeaveId12: Int?,
        @Json(name = "splitLeaveId_13")
        var splitLeaveId13: Int?,
        @Json(name = "splitLeaveId_14")
        var splitLeaveId14: Int?,
        @Json(name = "splitLeaveId_15")
        var splitLeaveId15: Int?,
        @Json(name = "splitLeaveId_2")
        var splitLeaveId2: Int?,
        @Json(name = "splitLeaveId_20")
        var splitLeaveId20: Int?,
        @Json(name = "splitLeaveId_3")
        var splitLeaveId3: Int?,
        @Json(name = "splitLeaveId_4")
        var splitLeaveId4: Int?,
        @Json(name = "splitLeaveId_5")
        var splitLeaveId5: Int?,
        @Json(name = "splitLeaveId_6")
        var splitLeaveId6: Int?,
        @Json(name = "splitLeaveId_7")
        var splitLeaveId7: Int?,
        @Json(name = "splitLeaveId_8")
        var splitLeaveId8: Int?,
        @Json(name = "splitLeaveId_9")
        var splitLeaveId9: Int?,
        @Json(name = "systemCountType")
        var systemCountType: Int?,
        @Json(name = "userDep")
        var userDep: String?,
        @Json(name = "userId")
        var userId: Int?,
        @Json(name = "validEnd")
        var validEnd: Int?,
        @Json(name = "validStart")
        var validStart: Int?,
        @Json(name = "workFromHome")
        var workFromHome: Int?,
        @Json(name = "workTime")
        var workTime: Int?
)

data class Msg(
        @Json(name = "dateType")
        var dateType: Int?,
        @Json(name = "dinnerTime")
        var dinnerTime: Int,
        @Json(name = "hourArray")
        var hourArray: List<Int>,
        @Json(name = "isWorking")
        var isWorking: Boolean,
        @Json(name = "lunchTime")
        var lunchTime: Int,
        @Json(name = "offWorkTime")
        var offWorkTime: Int,
        @Json(name = "TimeArray")
        var timeArray: List<String>,
        @Json(name = "week")
        var week: Int
)

/**
 * 获取加班时间,与当前时间比较
 */
internal fun Msg.getCurrentAddWorkTime(): Int {
    var countTime = 0
    val currentMinute = TimeUtil.getCurrentMinute()
    countTime = when {
        currentMinute in 1050..1095 -> {
            val gapMinute = currentMinute - 1050
            currentMinute - offWorkTime - gapMinute
        }
        currentMinute >= 1095 -> {
            currentMinute - offWorkTime - 45
        }
        else -> {
            currentMinute - offWorkTime
        }
    }
    return countTime
}

/**
 * 下班后，获取加班时间
 */
internal fun Msg.getAddWorkTime(): Int {
    var countTime = 0
    val lastTime = hourArray[hourArray.lastIndex]
    if(offWorkTime >= 1095){
        countTime = lastTime - offWorkTime
    }else if(offWorkTime <= 1050) {
        //计算下班时间到 1050 差多少;
        //差多少直接加上
        countTime +=  1050 - offWorkTime
        if(lastTime <= 1095){
            //如果小于直接return出去，代表没有超过1050
            return countTime
        }else if(lastTime > 1095){
            //如果超过1095 ，返回值在加上最后下班时间与1095的差值
            countTime += lastTime - 1095
            return countTime
        }
    }
    return countTime
}


internal fun Msg.getWorkState(): WorkState {
    if (hourArray.isEmpty()) {
        return WorkState.UN_PUNCH
    } else if (dateType == 3 || dateType == 4) {
        //只有加班
        return WorkState.NO_WORK
    }
    return if (isWorking) WorkState.WORKING else WorkState.OF_WORK
}

/**
 * 中间文本
 */
internal fun Msg.getCenterMsg(): String {
    return when (getWorkState()) {
        WorkState.NO_WORK -> {
            ""
        }
        WorkState.OF_WORK -> {
            val offTime = hourArray[hourArray.lastIndex]
            return when {
                offWorkTime < offTime -> {
                    val addWorkTime = getAddWorkTime()
                    //在加班了
                    "${addWorkTime / 60}小时${(addWorkTime % 60) - (addWorkTime % 15)}分"
                }
                offWorkTime == offTime -> {
                    "准点下班！"
                }
                else -> {
                    "打卡数据异常"
                }
            }
        }
        WorkState.WORKING -> {
            //当前的分钟数
            val currentMinute = TimeUtil.getCurrentMinute()
            return if (currentMinute < offWorkTime) {
                //还在上班
                val timeGap = offWorkTime - currentMinute
                "${timeGap / 60}小时${timeGap % 60}分"
            } else {
                val currentAddWorkTime = getCurrentAddWorkTime()
                //在加班了
                "${currentAddWorkTime / 60}小时${(currentAddWorkTime % 60) - (currentAddWorkTime % 15)}分"
            }
        }
        WorkState.UN_PUNCH -> {
            "\uD83D\uDE48今日未打卡"
        }
    }
}

/**
 * 顶部文字
 */
internal fun Msg.getTopMsg(): String {
    return when (getWorkState()) {
        WorkState.NO_WORK -> {
            "加班中："
        }
        WorkState.OF_WORK -> {
            "已下班，今日加班："
        }
        WorkState.WORKING -> {
            val currentMinute = TimeUtil.getCurrentMinute()
            return if (currentMinute < offWorkTime) {
                //还在上班
                "距离下班："
            } else {
                //在加班了
                "已加班："
            }
        }
        WorkState.UN_PUNCH -> {
            ""
        }
    }
}

/**
 * 底部文字
 */
internal fun Msg.getBottomMsg(): String {
    return when (getWorkState()) {
        WorkState.NO_WORK -> {
            ""
        }
        WorkState.OF_WORK -> {
            ""
        }
        WorkState.WORKING -> {
            val currentMinute = TimeUtil.getCurrentMinute()
            return if (currentMinute >= offWorkTime) {
                //加班时长
                val currentAddWorkTime = getCurrentAddWorkTime()
                Log.i("getAddWorkTime","currentAddWorkTime : $currentAddWorkTime")
                Log.i("getAddWorkTime","currentMinute : $currentMinute")
                //对当前加班时长除余数
                val gapTime = currentAddWorkTime % 15
                Log.i("getAddWorkTime","gapTime : $gapTime")
                //剩余时间
                val excessTime = currentMinute + (15 - gapTime)
                val sumTime = if (excessTime in 1050..1095) {
                    1095 + gapTime
                } else {
                    excessTime
                }
                Log.i("getAddWorkTime","gapTime : $sumTime")
                "最近15分钟在：${TimeUtil.getTime(sumTime)}"
            } else {
                "下班时间：${TimeUtil.getTime(offWorkTime)}"
            }
        }
        WorkState.UN_PUNCH -> {
            ""
        }
    }
}

