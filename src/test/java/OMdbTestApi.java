import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import io.restassured.response.Response;
import org.testng.annotations.Test;
// Assert kütüphanesi
import static org.hamcrest.MatcherAssert.assertThat;
import static org.testng.AssertJUnit.assertNotNull;

public class OMdbTestApi {
    // Gelmesi gereken error mesajı.

    public static String errorMessage = "No API key provided";

    @Test
    public void shouldSearchByTitleAndYear(){
        Response response = RestAssured.given()
                //t="Harry Potter" query parametre olduğu için.
                .queryParam("t","Harry Potter")
                .queryParam("y","2011")
                .queryParam("apiKey" , "ebfba94c")
                .get("http://www.omdbapi.com")
                .then()
                .statusCode(200)
                .extract().response();
        // response çıktısı.
       // response.prettyPeek();
        // json path --> Title içinde "Harry Potter" string içeriyor mu?
        assertThat(response.getBody().jsonPath().getString("Title"),Matchers.containsString("Harry Potter"));

    }

    @Test
    public void shouldSearchById() {
        Response byIdOrTitle = RestAssured.given()
                .queryParam("t", "Harry Potter")
                .queryParam("y", 2011)
                .queryParam("apiKey", "ebfba94c")
                .get("http://www.omdbapi.com")
                .then()
                .statusCode(200)
                .extract()
                .response();

        String imdbID = byIdOrTitle.getBody().jsonPath().getString("imdbID");
        // Imdb Id boş mu ?
        assertNotNull(imdbID);

        Response bySearch = RestAssured.given()
                .queryParam("i", imdbID)
                .queryParam("apiKey", "ebfba94c")
                .get("http://www.omdbapi.com")
                .then()
                .extract().response();

        assertThat(bySearch.getStatusCode(), Matchers.is(200));
        // Id ve (title ve year) aramalarında  Title eşleşiyor mu ?
        assertThat(byIdOrTitle.getBody().jsonPath().getString("Title"), Matchers.is(bySearch.getBody().jsonPath().getString("Title")));
    }

    @Test
    public void shouldNotGetResponseWithoutApiKey(){
        Response response = RestAssured.given()
                .queryParam("t", "Harry Potter")
                .get("http://www.omdbapi.com")
                .then()
                .statusCode(401)
                .extract().response();
        // api key olmadan açılan sayfa doğru errorMessage veriyor mu ?
        assertThat(response.getBody().jsonPath().getString("Error"), Matchers.containsString(errorMessage));
    }

}
