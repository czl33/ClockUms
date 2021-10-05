package com.newczl.clockwidget.utils

enum class WorkState(i: Int) {
    //一次打卡，正在工作中
    WORKING(0),
    //已完成双次打卡
    OF_WORK(1),
    //未打开
    UN_PUNCH(2),
    //无需工作，纯加班
    NO_WORK(3)
}