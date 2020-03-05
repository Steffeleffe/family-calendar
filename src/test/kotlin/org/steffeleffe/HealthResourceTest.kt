package org.steffeleffe

import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import org.hamcrest.CoreMatchers.containsString
import org.junit.jupiter.api.Test

@QuarkusTest
class HealthResourceTest {

    @Test
    fun `test health endpoint`() {
        given()
          .`when`().get("/health")
          .then()
             .statusCode(200)
             .body(containsString("UP"))
    }

}