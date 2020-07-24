import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.util.List;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;


public class ApiMethodsTests {

    static ExtractableResponse<Response> doGetRequest(String endpoint, int id) {
        RestAssured.defaultParser = Parser.JSON;
        return
                given().contentType(ContentType.JSON)
                        .accept(ContentType.JSON)
                        .when().get(endpoint, id)
                        .then().contentType(ContentType.JSON).statusCode(200).extract();
    }

    @BeforeSuite
    void set_base_url() {
        RestAssured.baseURI = "https://jsonplaceholder.typicode.com";
    }

    @Test
    void
    posts_response_return_200_with_expected_all_id() {
        //не баг, а фича. Чтобы не писать интерфейс, использовал доступный endpoint
        Response response = doGetRequest(EndPoints.ALL_POSTS, 1).response();
        List<Integer> idList = response.jsonPath().getList("userId");
        List<Integer> uniqueElementsidList =
                idList
                        .stream()
                        .distinct()
                        .collect(Collectors.toList());
        System.out.println(uniqueElementsidList.size());
        for (int element : uniqueElementsidList) {
            response = doGetRequest(EndPoints.POSTS, element).response();
            assertThat(response.jsonPath().getInt("userId[0]"), is(element));
        }
    }

    @Test
    void
    posts_response_return_200_with_expected_id() {
        Response response = doGetRequest(EndPoints.POSTS, 1).response();
        List<String> jsonResponse = response.jsonPath().getList("$");
        assertThat(jsonResponse, hasSize(10));
        assertThat(response.jsonPath().getString("userId[0]"), is("1"));
    }

    //ToDo add Pojo model
    @Test
    void
    posts_response_return_model() {
//        ////        Assert.assertEquals(pojo, PostsPojo.class);
//        List<PostsPojo> postsList = (List<PostsPojo>) doGetRequest(EndPoints.POSTS, 1).body().as(PostsPojo.class, ObjectMapperType.GSON);
//        System.out.println(postsList);
//        PostsPojo[] postsListAr  = doGetRequest(EndPoints.POSTS, 1).body().as(PostsPojo[].class, ObjectMapperType.GSON);
//        System.out.println(postsListAr);
//        //PostsPojo test1 = response.as(PostsPojo.class, ObjectMapperType.GSON);
    }

}
