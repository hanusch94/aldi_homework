package Task;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.*;

@RunWith(SerenityRunner.class)
public class GetTaskTests {
    private final String taskName = "task name";
    private final String taskDescription = "task description";

    private String taskId;

    @Before
    public void setBaseURLs() {
        RestAssured.baseURI = "something.us";
        Response response = RestAssured.given()
                .params("name", taskName, "description", taskDescription)
                .post("/tasks");
        response.then().statusCode(200);
        taskId = response.getBody().jsonPath().getString("id");
    }

    @Test
    public void get_task_happy_path() {
        RestAssured.get("/tasks/"+taskId)
                .then()
                .statusCode(200)
                    .assertThat().body("id", equalTo(taskId))
                    .assertThat().body("name", equalTo(taskName))
                    .assertThat().body("desc", equalTo(taskDescription));
    }

    @Test
    public void get_task_not_existing_task() {
        RestAssured.get("/tasks/notexisttask")
                .then()
                    .statusCode(404);
    }
}