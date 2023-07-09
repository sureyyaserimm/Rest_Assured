import Model.Location;
import Model.Place;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import io.restassured.response.Response;

import java.util.List;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class ZippoTest {

    @Test
    public void test() {

        given()
                // Hazırlık işlemleri : (token,send body, parametreler)

                .when()
                // endpoint (url), metodu

                .then()
        // assertion, test, data işlemleri
        ;
    }

    @Test
    public void statusCodeTest() {

        given()

                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
                .log().body()    // dönen body json datat sı,   log().all()
                .statusCode(200) // dönüş kodu 200 mü
        ;
    }

    @Test
    public void contentTypeTest() {

        given()

                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
                .log().body()    // dönen body json datat sı,   log.all()
                .statusCode(200) // dönüş kodu 200 mü
                .contentType(ContentType.JSON)  //dönen sonuç JSON mı
        ;
    }

    @Test
    public void checkCountryInResponseBody() {

        given()

                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
                .log().body()    // dönen body json datat sı,   log.all()
                .statusCode(200) // dönüş kodu 200 mü
                .body("country", equalTo("United States")) //bodynin country değişkeni "United States" e eşit mi?

        ;
    }


    @Test
    public void checkStateInResponseBody() {

        given()

                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
                .log().body()    // dönen body json datat sı,   log.all()
                .statusCode(200) // dönüş kodu 200 mü
                .body("places[0].state", equalTo("California"))

        ;
    }

    @Test
    public void checkHasItem() {

        given()

                .when()
                .get("http://api.zippopotam.us/tr/01000")

                .then()
                .log().body()
                .statusCode(200)
                .body("places.'place name'", hasItem("Dörtağaç Köyü"))
        //bütün place namelerin içinde dörtağaç köyü var mı?
        ;
    }


    @Test
    public void bodyArrayHasSizeTest() {

        given()

                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
                .log().body()
                .statusCode(200)
                .body("places", hasSize(1))
        ;
    }


    @Test
    public void combiningTest() {

        given()

                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
                .log().body()
                .statusCode(200)
                .body("places", hasSize(1))  //size 1 mi?
                .body("places.state", hasItem("California"))  //verien pathdeki list bu iteme sahip mi?
                .body("places[0].'place name'", equalTo("Beverly Hills"))  //verilen pathdeki değer buna eşit mi?
        ;
    }


    //http://api.zippopotam.us/us/90210    path PARAM
    //
    //https://sonuc.osym.gov.tr/Sorgu.aspx?SonucID=9617  Query PARAM


    @Test
    public void pathParamTest() {

        given()
                .pathParam("ulke", "us")
                .pathParam("postaKod", 90210)
                .log().uri()

                .when()
                .get("http://api.zippopotam.us/{ulke}/{postaKod}")

                .then()
                // .log().body()
                .statusCode(200)
        ;
    }

    @Test
    public void queryParamTest() {

        given()
                .param("page", 1)
                .log().uri() //request link

                .when()
                .get("https://gorest.co.in/public/v1/users")//?page=1

                .then()
                .log().body()
                .statusCode(200)
        ;
    }

    @Test
    public void queryParamTest2() {

        // https://gorest.co.in/public/v1/users?page=3
        // bu linkteki 1 den 10 kadar sayfaları çağırdığınızda response daki donen page degerlerinin
        // çağrılan page nosu ile aynı olup olmadığını kontrol ediniz.

        for (int i = 1; i < 10; i++) {

            given()
                    .param("page", i)
                    .log().uri() //request link

                    .when()
                    .get("https://gorest.co.in/public/v1/users")//?page=1

                    .then()
                    .statusCode(200)
                    //.log().body()
                    .body("meta.pagination.page", equalTo(i))
            ;
        }

    }

    RequestSpecification requestSpec;
    ResponseSpecification responseSpec;

    @BeforeClass
    public void Setup() {
        baseURI = "https://gorest.co.in/public/v1";

        requestSpec = new RequestSpecBuilder()
                .log(LogDetail.URI)
                .setContentType(ContentType.JSON)
                .build();

        responseSpec = new ResponseSpecBuilder()
                .expectContentType(ContentType.JSON)
                .expectStatusCode(200)
                .log(LogDetail.BODY)
                .build();
    }

    @Test
    public void requestResponseSpecification() {
        // https://gorest.co.in/public/v1/users?page=3

        given()
                .param("page", 1)
                .spec(requestSpec)

                .when()
                .get("/users")

                .then()
                .spec(responseSpec)
        ;
    }

    @Test
    public void extractingJsonPath() {

        String countryName =
                given()

                        .when()
                        .get("http://api.zippopotam.us/us/90210")

                        .then()
                        .log().body()
                        .extract().path("country");

        System.out.println("countryName = " + countryName);
        Assert.assertEquals(countryName, "United States");
    }

    @Test
    public void extractingJsonPath2() {
        //placeName

        String placeName =
                given()

                        .when()
                        .get("http://api.zippopotam.us/us/90210")

                        .then()
                        .log().body()
                        .extract().path("places[0].'place name'");

        System.out.println("placeName = " + placeName);
        Assert.assertEquals(placeName, "Beverly Hills");
    }


    @Test
    public void extractingJsonPath3() {

        // https://gorest.co.in/public/v1/users  dönen değerdeki limit bilgisini yazdırınız.

        int limit =
                given()

                        .when()
                        .get("https://gorest.co.in/public/v1/users")

                        .then()
                        .log().body()
                        .extract().path("meta.pagination.limit");

        System.out.println("limit = " + limit);
    }

    @Test
    public void extractingJsonPath4() {

        // https://gorest.co.in/public/v1/users  dönen değerdeki limit bilgisini yazdırınız.

        List<Integer> idList =
                given()

                        .when()
                        .get("https://gorest.co.in/public/v1/users")

                        .then()
                        .statusCode(200)
                        .log().body()
                        .extract().path("data.id");

        System.out.println("idList = " + idList);
    }


    @Test
    public void extractingJsonPathRresponseAll() {

        // https://gorest.co.in/public/v1/users  dönen değerdeki limit bilgisini yazdırınız.

        Response donenData =
                given()

                        .when()
                        .get("https://gorest.co.in/public/v1/users")

                        .then()
                        .statusCode(200)
                        //.log().body()
                        .extract().response();

        List<Integer> id = donenData.path("data.id");
        List<String> names = donenData.path("data.name");
        int limit = donenData.path("meta.pagination.limit");

        System.out.println("limit = " + limit);
        System.out.println("names = " + names);
        System.out.println("id = " + id);

        Assert.assertTrue(names.contains("Miss Datta Bhattacharya"));
        Assert.assertTrue(id.contains(1440177));
        Assert.assertEquals(limit, 10, "test sonucu hatalı");

    }


    @Test
    public void extractJsonAll_POJO() {

        Location locationNesnesi =
                given()


                        .when()
                        .get("http://api.zippopotam.us/us/90210")


                        .then()
                        .log().body()
                        .extract().body().as(Location.class);

        System.out.println("locationNesnesi.getCountry() = " + locationNesnesi.getCountry());

        for (Place p : locationNesnesi.getPlaces())
            System.out.println("p = " + p);

        System.out.println("locationNesnesi.getPlaces() = "
                + locationNesnesi.getPlaces().get(0).getPlacename());

    }

    @Test
    public void extractPOJO_Soru() {
        // aşağıdaki endpointte(link)  Dörtağaç Köyü ait diğer bilgileri yazdırınız

        Location adana =
                given()
                        .when()
                        .get("http://api.zippopotam.us/tr/01000")

                        .then()
                        .log().body()
                        .statusCode(200)
                        .extract().body().as(Location.class);

        for (Place p : adana.getPlaces()) {
            if (p.getPlacename().equalsIgnoreCase("Dörtağaç Köyü")) {
                System.out.println("p = " + p);
            }
        }
    }


}




