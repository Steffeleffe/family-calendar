package org.steffeleffe.calendarservice

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEmpty
import com.nhaarman.mockitokotlin2.*
import org.junit.jupiter.api.Test
import org.steffeleffe.calendarimport.GoogleCalendarImporter
import java.util.*

class CalendarEventCacheTest {

    @Test
    fun `should get events`() {
        val testEvent = CalendarEvent("test", "description", EventTimeRange(Date(123), Date(345)))
        val calendarId = "testCalenderUrl"
        val importerMock : GoogleCalendarImporter = mock()
        whenever(importerMock.importCalender(eq(calendarId), any())).thenReturn(mutableListOf(testEvent))
        val cache = CalendarEventCache(importerMock)

        val get = cache.get(calendarId)

        verify(importerMock).importCalender(eq(calendarId), any())
        assertThat(get).containsExactly(testEvent)
    }

    @Test
    fun `should get no events`() {
        val testEvent = CalendarEvent("test", "description", EventTimeRange(Date(123), Date(345)))
        val calendarId = "testCalenderUrl"
        val importerMock : GoogleCalendarImporter = mock()
        whenever(importerMock.importCalender(eq(calendarId), any())).thenReturn(mutableListOf(testEvent))
        val cache = CalendarEventCache(importerMock)

        val get = cache.get("nonExistingCalendarUrl")

        assertThat(get).isEmpty()
    }

    @Test
    fun `should use cached events`() {
        val testEvent = CalendarEvent("test", "description", EventTimeRange(Date(123), Date(345)))
        val calendarId = "testCalenderUrl"
        val importerMock : GoogleCalendarImporter = mock()
        whenever(importerMock.importCalender(eq(calendarId), any())).thenReturn(mutableListOf(testEvent))
        val cache = CalendarEventCache(importerMock)

        // Call twice
        cache.get(calendarId)
        cache.get(calendarId)

        verify(importerMock).importCalender(eq(calendarId), any())
    }

    @Test
    fun `get after invalidate`() {
        val testEvent = CalendarEvent("test", "description", EventTimeRange(Date(123), Date(345)))
        val calendarId = "testCalenderUrl"
        val importerMock : GoogleCalendarImporter = mock()
        whenever(importerMock.importCalender(eq(calendarId), any())).thenReturn(mutableListOf(testEvent))
        val cache = CalendarEventCache(importerMock)

        // Call twice
        cache.get(calendarId)
        cache.invalidateAll()
        cache.get(calendarId)

        verify(importerMock, times(2)).importCalender(eq(calendarId), any())
    }

}