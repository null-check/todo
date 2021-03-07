package com.arjun.todo.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test

import java.util.*

class TimeUtilsKtTest {

    @Test
    fun getSecsFormatted_with0_isCorrect() {
        val result = getSecsFormatted(0)
        assertThat(result).isEqualTo("0 Seconds")
    }

    @Test
    fun getSecsFormatted_with1_isCorrect() {
        val result = getSecsFormatted(1)
        assertThat(result).isEqualTo("1 Second")
    }

    @Test
    fun getSecsFormatted_with5_isCorrect() {
        val result = getSecsFormatted(5)
        assertThat(result).isEqualTo("5 Seconds")
    }

    @Test
    fun getSecsFormatted_with60_isCorrect() {
        val result = getSecsFormatted(60)
        assertThat(result).isEqualTo("1 Min")
    }

    @Test
    fun getSecsFormatted_with100_isCorrect() {
        val result = getSecsFormatted(100)
        assertThat(result).isEqualTo("1 Min")
    }

    @Test
    fun getSecsFormatted_with600_isCorrect() {
        val result = getSecsFormatted(600)
        assertThat(result).isEqualTo("10 Mins")
    }

    @Test
    fun getSecsFormatted_with3600_isCorrect() {
        val result = getSecsFormatted(3600)
        assertThat(result).isEqualTo("1 Hour")
    }

    @Test
    fun getSecsFormatted_with3660_isCorrect() {
        val result = getSecsFormatted(3660)
        assertThat(result).isEqualTo("1 Hour 1 Min")
    }

    @Test
    fun getSecsFormatted_with7800_isCorrect() {
        val result = getSecsFormatted(7800)
        assertThat(result).isEqualTo("2 Hours 10 Mins")
    }

    @Test
    fun getNextTargetTime_with1200am_returnsFutureTime() {
        val currentTime = Calendar.getInstance()
        currentTime.set(Calendar.HOUR_OF_DAY, 0)
        currentTime.set(Calendar.MINUTE, 0)
        currentTime.set(Calendar.SECOND, 0)
        val result = getNextResetTime(currentTime)
        assertThat(result).isGreaterThan(0)
    }

    @Test
    fun getNextTargetTime_with1200am5sec_returnsFutureTime() {
        val currentTime = Calendar.getInstance()
        currentTime.set(Calendar.HOUR_OF_DAY, 0)
        currentTime.set(Calendar.MINUTE, 0)
        currentTime.set(Calendar.SECOND, 5)
        val result = getNextResetTime(currentTime)
        assertThat(result).isGreaterThan(0)
    }

    @Test
    fun getNextTargetTime_with1201am_returnsFutureTime() {
        val currentTime = Calendar.getInstance()
        currentTime.set(Calendar.HOUR_OF_DAY, 0)
        currentTime.set(Calendar.MINUTE, 1)
        currentTime.set(Calendar.SECOND, 0)
        val result = getNextResetTime(currentTime)
        assertThat(result).isGreaterThan(0)
    }

    @Test
    fun getNextTargetTime_with4pm_returnsFutureTime() {
        val currentTime = Calendar.getInstance()
        currentTime.set(Calendar.HOUR_OF_DAY, 16)
        currentTime.set(Calendar.MINUTE, 0)
        currentTime.set(Calendar.SECOND, 0)
        val result = getNextResetTime(currentTime)
        assertThat(result).isGreaterThan(0)
    }

    @Test
    fun getNextTargetTime_with1159pm_returnsFuture() {
        val currentTime = Calendar.getInstance()
        currentTime.set(Calendar.HOUR_OF_DAY, 23)
        currentTime.set(Calendar.MINUTE, 59)
        currentTime.set(Calendar.SECOND, 0)
        val result = getNextResetTime(currentTime)
        assertThat(result).isGreaterThan(0)
    }

    @Test
    fun getNextTargetTime_with1159pm_returnsLessThan1Min() {
        val currentTime = Calendar.getInstance()
        currentTime.set(Calendar.HOUR_OF_DAY, 23)
        currentTime.set(Calendar.MINUTE, 59)
        currentTime.set(Calendar.SECOND, 0)
        val result = getNextResetTime(currentTime)
        assertThat(result).isLessThan(1000 * 61)
    }

    @Test
    fun getNextTargetTime_with1159pm55sec_returnsFutureTime() {
        val currentTime = Calendar.getInstance()
        currentTime.set(Calendar.HOUR_OF_DAY, 23)
        currentTime.set(Calendar.MINUTE, 59)
        currentTime.set(Calendar.SECOND, 55)
        val result = getNextResetTime(currentTime)
        assertThat(result).isGreaterThan(0)
    }

    @Test
    fun getNextTargetTime_with1159pm55sec_returnsGreaterThan24Hours() {
        val currentTime = Calendar.getInstance()
        currentTime.set(Calendar.HOUR_OF_DAY, 23)
        currentTime.set(Calendar.MINUTE, 59)
        currentTime.set(Calendar.SECOND, 55)
        val result = getNextResetTime(currentTime)
        assertThat(result).isGreaterThan(1000 * 60 * 24)
    }
}