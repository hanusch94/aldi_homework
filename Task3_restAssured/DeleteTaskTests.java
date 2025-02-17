package Task;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.equalTo;

@RunWith(SerenityRunner.class)
public class DeleteTaskTests {
    private final String taskName = "task name";
    private final String taskDescription = "task description";

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
    public void delete_task_happy_path() {
        RestAssured.delete("/tasks/"+taskId)
                .then()
                    .statusCode(200);

        RestAssured.get("/tasks/"+taskId)
                .then()
                    .statusCode(404);
    }

    @Test
    public void delete_task_not_existing_task() {
        RestAssured.delete("/tasks/notexistingtask")
                .then()
                    .statusCode(404);
    }
}