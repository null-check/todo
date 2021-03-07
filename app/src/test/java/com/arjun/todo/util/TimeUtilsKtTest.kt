package com.arjun.todo.util

import org.junit.Test

import org.junit.Assert.*
import java.util.*

class TimeUtilsKtTest {

    @Test
    fun test_getSecsFormatted_with0_isCorrect() {
        val result = getSecsFormatted(0)
        assertEquals("0 Seconds", result)
    }

    @Test
    fun test_getSecsFormatted_with1_isCorrect() {
        val result = getSecsFormatted(1)
        assertTrue("1 Second" == result)
    }

    @Test
    fun test_getSecsFormatted_with5_isCorrect() {
        val result = getSecsFormatted(5)
        assertEquals("5 Seconds", result)
    }

    @Test
    fun test_getSecsFormatted_with60_isCorrect() {
        val result = getSecsFormatted(60)
        assertEquals("1 Min", result)
    }

    @Test
    fun test_getSecsFormatted_with100_isCorrect() {
        val result = getSecsFormatted(100)
        assertEquals("1 Min", result)
    }

    @Test
    fun test_getSecsFormatted_with600_isCorrect() {
        val result = getSecsFormatted(600)
        assertEquals("10 Mins", result)
    }

    @Test
    fun test_getSecsFormatted_with3600_isCorrect() {
        val result = getSecsFormatted(3600)
        assertEquals("1 Hour", result)
    }

    @Test
    fun test_getSecsFormatted_with3660_isCorrect() {
        val result = getSecsFormatted(3660)
        assertEquals("1 Hour 1 Min", result)
    }

    @Test
    fun test_getSecsFormatted_with7800_isCorrect() {
        val result = getSecsFormatted(7800)
        assertEquals("2 Hours 10 Mins", result)
    }

    @Test
    fun test_getNextTargetTime_with1200am_returnsFutureTime() {
        val currentTime = Calendar.getInstance()
        currentTime.set(Calendar.HOUR_OF_DAY, 0)
        currentTime.set(Calendar.MINUTE, 0)
        currentTime.set(Calendar.SECOND, 0)
        val result = getNextResetTime(currentTime)
        assertTrue(result > 0)
    }

    @Test
    fun test_getNextTargetTime_with1200am5sec_returnsFutureTime() {
        val currentTime = Calendar.getInstance()
        currentTime.set(Calendar.HOUR_OF_DAY, 0)
        currentTime.set(Calendar.MINUTE, 0)
        currentTime.set(Calendar.SECOND, 5)
        val result = getNextResetTime(currentTime)
        assertTrue(result > 0)
    }

    @Test
    fun test_getNextTargetTime_with1201am_returnsFutureTime() {
        val currentTime = Calendar.getInstance()
        currentTime.set(Calendar.HOUR_OF_DAY, 0)
        currentTime.set(Calendar.MINUTE, 1)
        currentTime.set(Calendar.SECOND, 0)
        val result = getNextResetTime(currentTime)
        assertTrue(result > 0)
    }

    @Test
    fun test_getNextTargetTime_with4pm_returnsFutureTime() {
        val currentTime = Calendar.getInstance()
        currentTime.set(Calendar.HOUR_OF_DAY, 16)
        currentTime.set(Calendar.MINUTE, 0)
        currentTime.set(Calendar.SECOND, 0)
        val result = getNextResetTime(currentTime)
        assertTrue(result > 0)
    }

    @Test
    fun test_getNextTargetTime_with1159pm_returnsFuture() {
        val currentTime = Calendar.getInstance()
        currentTime.set(Calendar.HOUR_OF_DAY, 23)
        currentTime.set(Calendar.MINUTE, 59)
        currentTime.set(Calendar.SECOND, 0)
        val result = getNextResetTime(currentTime)
        assertTrue(result > 0)
    }

    @Test
    fun test_getNextTargetTime_with1159pm_returnsLessThan1Min() {
        val currentTime = Calendar.getInstance()
        currentTime.set(Calendar.HOUR_OF_DAY, 23)
        currentTime.set(Calendar.MINUTE, 59)
        currentTime.set(Calendar.SECOND, 0)
        val result = getNextResetTime(currentTime)
        assertTrue(result <= 1000 * 61)
    }

    @Test
    fun test_getNextTargetTime_with1159pm55sec_returnsFutureTime() {
        val currentTime = Calendar.getInstance()
        currentTime.set(Calendar.HOUR_OF_DAY, 23)
        currentTime.set(Calendar.MINUTE, 59)
        currentTime.set(Calendar.SECOND, 55)
        val result = getNextResetTime(currentTime)
        assertTrue(result > 0)
    }

    @Test
    fun test_getNextTargetTime_with1159pm55sec_returnsGreaterThan24Hours() {
        val currentTime = Calendar.getInstance()
        currentTime.set(Calendar.HOUR_OF_DAY, 23)
        currentTime.set(Calendar.MINUTE, 59)
        currentTime.set(Calendar.SECOND, 55)
        val result = getNextResetTime(currentTime)
        assertTrue(result > 1000 * 60 * 24)
    }
}