package com.e.osu_id_app

import java.util.*

data class SessionCard(var title: String, var lstModDt: Long, var dateStr: String, var progressStr: String)

fun selector(p: SessionCard): Long = p.lstModDt
