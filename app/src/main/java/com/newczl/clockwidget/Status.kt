package com.newczl.clockwidget

import com.newczl.clockwidget.utils.pref

object Status {

    var isCharacterPlaying by pref(false)

    var isStartTimeReminder by pref(false)

    var isStartAlarmReminder by pref(false)

    var isScreenOff by pref(false)

    var userId by pref("17469")


}