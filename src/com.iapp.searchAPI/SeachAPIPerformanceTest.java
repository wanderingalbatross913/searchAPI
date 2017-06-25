package com.iapp.searchAPI;

import org.testng.annotations.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.lessThan;

/**
 * Created by katthy on 6/25/17.
 */
public class SeachAPIPerformanceTest {

    @Test
    public void searchAPIPerformanceTest(){
        ExecutorService executor = Executors.newFixedThreadPool(500);
        Runnable task = new Runnable() {
            @Override
            public void run() {
                given().
                        pathParam("term", "piano").
                        pathParam("country", "").
                        pathParam("media", "").
                        pathParam("limit", "").
                        when().
                        get("https://itunes.apple.com/search?term={term}&country={country}&media={media}&limit={limit}").
                        then().assertThat().statusCode(200).and().time(lessThan(100l));
            }
        };

        for(int i=0; i<5000000; i++){
            executor.execute(task);
        }
        executor.shutdown();
    }
}
