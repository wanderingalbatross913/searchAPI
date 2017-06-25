package com.iapp.searchAPI;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Created by katthy on 6/24/17.
 */
public class SearchAPITest {
/*
    Test:
    Positive scenarios:
        1) mandatory param, check the result matches with the mandatory param, and check the default values of the optional params
        2) full params with non default values, check the result matches the input params
        3) loop each valid media value
        4) limit = 1, 200


    Negative scenarios:
        1) without term param - 200 code
        2) term = URL not encoded - 200 code
        3) term contains "., -, _, *" - 200 code
        4) limit = 0, 201 - 200 code

        5) country = invalid Country Code - 400 code
        6) media = invalid media value - 400 code

    Performance Response Time check:
        <100ms
        500 thread, 500,000 tasks
*/

    @Test(dataProvider = "inputData")
    public void searchAPIPosTest(String term, String country, String media, String limit, String countryResult, String mediaResult ) {
        given().
                pathParam("term", term).
                pathParam("country", country).
                pathParam("media", media).
                pathParam("limit", limit).
        when().
                get("https://itunes.apple.com/search?term={term}&country={country}&media={media}&limit={limit}").
        then().assertThat().statusCode(200).
                body("results.size()", lessThanOrEqualTo(Integer.valueOf(limit.isEmpty() ? "50" : limit))).
                body("results[0].kind", equalTo(mediaResult)).
                body("results[0].country", equalTo(countryResult));
    }


    @Test(dataProvider = "inputErrorData200")
    public void searchAPINegTest(String term, String country, String media, String limit, int statusCode, int count ){
        given().
                pathParam("term", term).
                pathParam("country", country).
                pathParam("media", media).
                pathParam("limit", limit).
        when().
                get("https://itunes.apple.com/search?term={term}&country={country}&media={media}&limit={limit}").
        then().assertThat().statusCode(statusCode).
        body("resultCount", lessThanOrEqualTo(count));
    }

    @Test(dataProvider = "inputErrorData400")
    public void searchAPINegErrorTest(String term, String country, String media, String limit, int statusCode, String errorMessage ){
        given().
                pathParam("term", term).
                pathParam("country", country).
                pathParam("media", media).
                pathParam("limit", limit).
        when().
                get("https://itunes.apple.com/search?term={term}&country={country}&media={media}&limit={limit}").
        then().assertThat().statusCode(statusCode).
                body("errorMessage", equalTo(errorMessage));
    }

    @DataProvider
    public static Object[][] inputData() {
        return new Object[][]{
                /*mandatory param*/{"piano", "", "", "", "USA","song"},

                /* valid media type: movie, podcast, music, musicVideo, audiobook, shortFilm, tvShow, software, ebook*/
                /*full params*/{"piano", "", "musicVideo", "25", "USA", "music-video"},

                /*each media param*/{"piano", "", "movie", "25", "USA", "feature-movie"},
                 /*each media param*/{"piano", "", "podcast", "25", "USA", "podcast"},
                 /*each media param*/{"piano", "", "music", "25", "USA", "song"},
                 /*each media param*/{"book", "", "audiobook", "25", "USA", null},
                 /*each media param*/{"film", "", "shortFilm", "25", "USA", "feature-movie"},
                 /*each media param*/{"piano", "", "tvShow", "25", "USA", "tv-episode"},
                 /*each media param*/{"game", "", "software", "25", null, "software"},
                 /*each media param*/{"book", "", "ebook", "25", null, "ebook"},

                /*limit = 1*/{"piano", "", "", "1", "USA", "song"},
                /*limit = 200*/{"piano", "", "", "200", "USA", "song"},
        };
    }

    @DataProvider
    public static Object[][] inputErrorData200() {
        return new Object[][]{
            /*without term param*/{"", "", "","", 200, 0},
            /*term = URL not encoded*/{"piano   game", "", "","", 200, 50},
            /*term contains "., -, _, *", empty result */ {"piano._-*game", "", "", "", 200, 0},
            /*limit = 0*/{"piano", "", "", "0", 200, 200},
            /*limist=201*/{"piano", "", "", "201", 200, 200}
        };
    }

    @DataProvider
    public static Object[][] inputErrorData400() {
        return new Object[][]{
            /*country = invalid Country Code*/{"piano", "UU", "","", 400, "Invalid value(s) for key(s): [country]"},
            /*media = invalid media value*/{"piano", "", "media","", 400, "Invalid value(s) for key(s): [mediaType]"},
        };
    }

}
