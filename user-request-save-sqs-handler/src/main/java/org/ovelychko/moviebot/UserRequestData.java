package org.ovelychko.moviebot;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@DynamoDBTable(tableName = "UserRequestData")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserRequestData {
    @DynamoDBHashKey(attributeName = "id")
    private long id;
    @DynamoDBAttribute(attributeName = "telegramUserId")
    private long telegramUserId;
    @DynamoDBAttribute(attributeName = "viberUserId")
    private String viberUserId;
    @DynamoDBAttribute(attributeName = "request")
    private String request;
}
