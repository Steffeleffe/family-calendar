package org.steffeleffe

import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured
import org.hamcrest.CoreMatchers
import org.junit.jupiter.api.Test

@QuarkusTest
class CalendarResourceTest {

    @Test
    fun `test get all calendars endpoint`() {
        RestAssured.given()
                .`when`().get("/calendar")
                .then()
                .statusCode(200)
                .body(CoreMatchers.containsString("\"id\":\"testId\""))
    }

    @Test
    fun `test calendar refresh endpoint`() {
        RestAssured.given()
                .`when`().get("/calendar/refresh")
                .then()
                .statusCode(200)
                .body(CoreMatchers.containsString("Caches are refreshed"))
    }

}
