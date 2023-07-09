package GoRest;

import com.github.javafaker.Faker;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class GoRestUsersTests {

    Faker randomUretici = new Faker();
    int userID;

    RequestSpecification reqSpec;

    @BeforeClass
    public void setup() {

        //baseURI="https://gorest.co.in/public/v2/users/";    // baseUri RequestSpecification den önce tanımlanmalı.
        baseURI = "https://gorest.co.in/public/v2/users";


        reqSpec = new RequestSpecBuilder()
                .addHeader("Authorization", "Bearer d7f6a125a39dbc80146f812359c91dda6caa60cbdd7bcf7c6929e65d576e3e3b")
                .setContentType(ContentType.JSON)
                .setBaseUri(baseURI)
                .build();

    }


    @Test(enabled = false)
    public void createUserJson() {
        // POST https://gorest.co.in/public/v2/users
        // "Authorization: Bearer 523891d26e103bab0089022d20f1820be2999a7ad693304f560132559a2a152d"
        // {"name":"{{$randomFullName}}", "gender":"male", "email":"{{$randomEmail}}", "status":"active"}
        String rndFullname = randomUretici.name().fullName();
        String rndEmail = randomUretici.internet().emailAddress();

        userID =
                given()
                        .spec(reqSpec)
                        .body("{\"name\":\"" + rndFullname + "\", \"gender\":\"male\", \"email\":\"" + rndEmail + "\", \"status\":\"active\"}")
                        //.log().uri()
                        //.log().body()

                        .when()
                        .post("")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .contentType(ContentType.JSON)
                        .extract().path("id");
    }

    @Test
    public void createUserMap() {
        // POST https://gorest.co.in/public/v2/users
        // "Authorization: Bearer 523891d26e103bab0089022d20f1820be2999a7ad693304f560132559a2a152d"
        // {"name":"{{$randomFullName}}", "gender":"male", "email":"{{$randomEmail}}", "status":"active"}

        String rndFullName = randomUretici.name().fullName();
        String rndEmail = randomUretici.internet().emailAddress();

        Map<String, String> newUser = new HashMap<>();
        newUser.put("name", rndFullName);
        newUser.put("gender", "male");
        newUser.put("email", rndEmail);
        newUser.put("status", "active");


        userID =
                given()
                        .spec(reqSpec)
                        .body(newUser)
                        .log().uri()
                        .log().body()

                        .when()
                        .post("")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .contentType(ContentType.JSON)
                        .extract().path("id");

    }


    @Test(enabled = false)
    public void createUserClass() {
        // POST https://gorest.co.in/public/v2/users
        // "Authorization: Bearer 523891d26e103bab0089022d20f1820be2999a7ad693304f560132559a2a152d"
        // {"name":"{{$randomFullName}}", "gender":"male", "email":"{{$randomEmail}}", "status":"active"}

        String rndFullName = randomUretici.name().fullName();
        String rndEmail = randomUretici.internet().emailAddress();

        User newUser = new User();
        newUser.name = rndFullName;
        newUser.gender = "male";
        newUser.email = rndEmail;
        newUser.status = "active";


        userID =
                given()
                        .spec(reqSpec)
                        .body(newUser)
                        .log().uri()
                        .log().body()

                        .when()
                        .post("")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .contentType(ContentType.JSON)
                        .extract().path("id");

    }


    @Test(dependsOnMethods = "createUserMap")
    public void getUserByID() {
        given()
                .spec(reqSpec)

                .when()
                .get("" + userID)


                .then()
                .log().body()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo(userID))

        ;
    }

    @Test(dependsOnMethods = "getUserByID")
    public void updateUser() {

        Map<String, String> updateUser = new HashMap<>();
        updateUser.put("name", "random name2");

        given()
                .spec(reqSpec)
                .body(updateUser)
                .log().uri()


                .when()
                .put("" + userID)

                .then()
                .log().body()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo(userID))
                .body("name", equalTo("random name2"))

        ;

    }


    @Test (dependsOnMethods = "updateUser")
    public void deleteUser() {

        given()
                .spec(reqSpec)

                .when()
                .delete("" + userID)

                .then()
                .log().body()
                .statusCode(204)


        ;


    }


    @Test (dependsOnMethods = "deleteUser")
    public void deleteUserNegative() {

        given()
                .spec(reqSpec)

                .when()
                .delete("" + userID)

                .then()
                .log().body()
                .statusCode(404)


        ;


    }


}
