package Task;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.equalTo;

@RunWith(SerenityRunner.class)
public class PostTaskTests {
    private final String taskName = "task name";
    private final String taskDescription = "task description";

    private String taskId;

    @Before
    public void setBaseURLs() {
        RestAssured.baseURI = "something.us";
    }

    @Test
    public void post_task_happy_path() {
        Response response = RestAssured.given()
                .params("name", taskName, "description", taskDescription)
                .post("/tasks");
        response.then()
                .statusCode(200)
                    .assertThat().body("id", equalTo(taskId))
                    .assertThat().body("name", equalTo(taskName))
                    .assertThat().body("desc", equalTo(taskDescription));
        taskId = response.getBody().jsonPath().getString("id");

        RestAssured.get("/tasks/"+taskId)
                .then()
                    .statusCode(200)
                    .assertThat().body("id", equalTo(taskId))
                    .assertThat().body("name", equalTo(taskName))
                    .assertThat().body("desc", equalTo(taskDescription));
    }

    @Test
    public void post_task_bad_request() {
        Response response = RestAssured.given()
                .params("name", taskName)
                .post("/tasks");
        response.then().statusCode(400);
        taskId = response.getBody().jsonPath().getString("id");

        RestAssured.get("/tasks/"+taskId)
                .then()
                .statusCode(404);
    }
}