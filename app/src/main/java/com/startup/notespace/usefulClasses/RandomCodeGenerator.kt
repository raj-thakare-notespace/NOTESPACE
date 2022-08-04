package com.startup.notespace.usefulClasses

import java.lang.StringBuilder
import java.util.*

class RandomCodeGenerator {
    private val LETTERS = "abcdefghijklmnopqrstuvwxyz"
    private val NUMBER = "0123456789"
    private val ALPHANUMERIC = (LETTERS + LETTERS.toUpperCase() + NUMBER).toCharArray()
    fun generateAlphaNumeric(length: Int): String {
        val result = StringBuilder()
        for (i in 0 until length) {
            result.append(ALPHANUMERIC[Random().nextInt(ALPHANUMERIC.size)])
        }
        return result.toString()
    }
}