package com.thread.Thread.controllers;

import com.thread.Thread.models.TwitterModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import twitter4j.*;

import java.util.List;
import java.util.Base64;

@RestController
@RequestMapping("/twitter")
public class TwitterController extends Controller {
    private Twitter twitter;

    @Override
    public ResponseEntity<List<?>> search(@RequestParam String q) {
        //Set up decoder to decode encoded query
        byte[] encodedBytes = Base64.getEncoder().encode(q.getBytes());
        byte[] decodedBytes = Base64.getDecoder().decode(encodedBytes);
        String decodedString = new String(decodedBytes);

        //Twitter4j Setup
        twitter = TwitterFactory.getSingleton();
        Query query = new Query("source: " + decodedString);

        //Search tweets related to the query
        QueryResult result = null;
        try {
            result = twitter.search(query);
        } catch (TwitterException e) {
            e.printStackTrace();
        }

        //Print tweets related to query
        List<Status> tweets = result.getTweets();
        for (Status status : tweets) {
            System.out.println("@" + status.getUser().getScreenName() + ":" + status.getText());
        }

        return new ResponseEntity<>(tweets, HttpStatus.OK);
    }

    @PostMapping("/share")
    public ResponseEntity<TwitterModel> share(@RequestBody TwitterModel body) {
        //Twitter4j Setup
        twitter = TwitterFactory.getSingleton();

        //Retweet status based on id given in request body
        try {
            twitter.retweetStatus(body.getId());
        } catch (TwitterException e) {
            e.printStackTrace();
        }

        System.out.println(body.getId());
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @PostMapping("/react")
    public ResponseEntity<TwitterModel> react(@RequestBody TwitterModel body) {
        //Twitter4j Setup
        twitter = TwitterFactory.getSingleton();

        //Comment status based on id given in request body
        try {
            //Get status based on id given in request body
            Status status = twitter.showStatus(body.getId());
            //Reply to that status with a comment
            Status reply = twitter.updateStatus(
                    new StatusUpdate(" @" + status.getUser().getScreenName() + " " + body.getMessage()).inReplyToStatusId(body.getId())
            );
        } catch (TwitterException e) {
            e.printStackTrace();
        }


        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @PostMapping("/comment")
    public ResponseEntity<TwitterModel> comment(@RequestBody TwitterModel body) {
        twitter = TwitterFactory.getSingleton();

        //Comment status based on id given in request body
        try {
            //Get status based on id given in request body
            Status status = twitter.showStatus(body.getId());
            //Reply to that status with a comment
            Status reply = twitter.updateStatus(
                    new StatusUpdate(" @" + status.getUser().getScreenName() + " " + body.getMessage()).inReplyToStatusId(body.getId())
            );
        } catch (TwitterException e) {
            e.printStackTrace();
        }

        return new ResponseEntity<>(body, HttpStatus.OK);
    }

}