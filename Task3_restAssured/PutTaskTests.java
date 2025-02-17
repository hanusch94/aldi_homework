package Task;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.equalTo;

@RunWith(SerenityRunner.class)
public class PutTaskTests {
    private final String taskName = "task name";
    private final String taskDescription = "task description";
    private final String newTaskName = "new task name";
    private final String newTaskDescription = "new task description";

    private String taskId;

    @BeforeEach
    public void setBaseURLs() {
        RestAssured.baseURI = "something.us";
        Response response = RestAssured.given()
                .params("name", taskName, "description", taskDescription)
                .post("/tasks");
        response.then().statusCode(200);
        taskId = response.getBody().jsonPath().getString("id");
    }

    @Test
    public void put_happy_path() {
        RestAssured.given()
                .params("name", newTaskName, "description", newTaskDescription)
                .put("/tasks/"+taskId)
                .then()
                    .statusCode(200)
                    .assertThat().body("id", equalTo(taskId))
                    .assertThat().body("name", equalTo(newTaskName))
                    .assertThat().body("desc", equalTo(newTaskDescription));

        RestAssured.get("/tasks/"+taskId)
                .then()
                    .statusCode(200)
                    .assertThat().body("id", equalTo(taskId))
                    .assertThat().body("name", equalTo(newTaskName))
                    .assertThat().body("desc", equalTo(newTaskDescription));
    }

    @Test
    public void put_task_bad_request() {
        RestAssured.given()
                .params("name", newTaskName)
                .put("/tasks/"+taskId)
                .then()
                    .statusCode(400);

        RestAssured.get("/tasks/"+taskId)
                .then()
                    .statusCode(200)
                    .assertThat().body("id", equalTo(taskId))
                    .assertThat().body("name", equalTo(taskName))
                    .assertThat().body("desc", equalTo(taskDescription));
    }

    @Test
    public void put_task_not_existing_task() {
        RestAssured.given()
                .params("name", newTaskName, "description", newTaskDescription)
                .put("/tasks/notexistingtask")
                .then()
                    .statusCode(404);

        RestAssured.get("/tasks/"+taskId)
                .then()
                    .statusCode(200)
                    .assertThat().body("id", equalTo(taskId))
                    .assertThat().body("name", equalTo(taskName))
                    .assertThat().body("desc", equalTo(taskDescription));
    }
}