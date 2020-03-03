package org.steffeleffe

import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured
import org.hamcrest.CoreMatchers
import org.junit.jupiter.api.Test

@QuarkusTest
class HtmlResourceTest {

    @Test
    fun `test index html endpoint`() {
        RestAssured.given()
                .`when`().get("/index.html")
                .then()
                .statusCode(200)
                .body(CoreMatchers.containsString("<head>"))
                .body(CoreMatchers.containsString("<div class=\"calendar\">"))
    }

}