package org.steffeleffe.calendarimport

import io.quarkus.test.Mock
import org.steffeleffe.calendarservice.CalendarEvent
import org.steffeleffe.calendarservice.EventTimeRange
import java.util.*
import javax.enterprise.context.ApplicationScoped

@Mock
@ApplicationScoped
class MockCalendarImporter : GoogleCalendarImporter() {
    override fun importCalender(calendarId: String, numberOfDaysToImport: Int): List<CalendarEvent> {
        return listOf(CalendarEvent("testId", "testDescription", EventTimeRange(Date(123), Date(345))))
    }
}

