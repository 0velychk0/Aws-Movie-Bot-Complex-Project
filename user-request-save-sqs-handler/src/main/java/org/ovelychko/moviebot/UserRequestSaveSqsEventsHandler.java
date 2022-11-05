package org.ovelychko.moviebot;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.lambda.runtime.events.SQSEvent.SQSMessage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class UserRequestSaveSqsEventsHandler implements RequestHandler<SQSEvent, String> {

    private final AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
    private final DynamoDBMapper dynamoDBMapper = new DynamoDBMapper(client);
    private final Gson gson = new GsonBuilder().create();

    @Override
    public String handleRequest(SQSEvent event, Context context) {
        LambdaLogger logger = context.getLogger();

        for (SQSMessage msg : event.getRecords()) {
            try {
                UserRequestData data = gson.fromJson(msg.getBody(), UserRequestData.class);
                dynamoDBMapper.save(data);
            } catch (Exception ex) {
                logger.log("Exception in handling: " + msg.getBody());
            }
        }
        return "SQS - Done";
    }
}
