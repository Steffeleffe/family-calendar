package org.steffeleffe

import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured
import org.hamcrest.CoreMatchers
import org.junit.jupiter.api.Test

@QuarkusTest
class CssResourceTest {

    @Test
    fun `test style css endpoint`() {
        RestAssured.given()
                .`when`().get("/style.css")
                .then()
                .statusCode(200)
                .body(CoreMatchers.containsString("body {"))
                .body(CoreMatchers.containsString("grid-template-columns: [timeSlot] auto"))
    }
}