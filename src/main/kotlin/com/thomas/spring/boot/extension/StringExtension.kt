package com.thomas.spring.boot.extension

const val EMPTY_STRING = ""

fun Any?.toStringOrEmpty(): String = this?.toString() ?: EMPTY_STRING

fun String.substringTrimmed(startIndex: Int): String = this.substring(startIndex).trim()
