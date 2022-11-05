package org.ovelychko.moviebot;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@DynamoDBTable(tableName = "TelegramUserData")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class TelegramUserData {
    @DynamoDBHashKey(attributeName = "id")
    private long id;
    @DynamoDBAttribute(attributeName = "first_name")
    private String firstName;
    @DynamoDBAttribute(attributeName = "is_bot")
    private Boolean isBot;
    @DynamoDBAttribute(attributeName = "last_name")
    private String lastName;
    @DynamoDBAttribute(attributeName = "username")
    private String userName;
    @DynamoDBAttribute(attributeName = "language_code")
    private String languageCode;
    @DynamoDBAttribute(attributeName = "can_join_groups")
    private Boolean canJoinGroups;
    @DynamoDBAttribute(attributeName = "can_read_all_group_messages")
    private Boolean canReadAllGroupMessages;
    @DynamoDBAttribute(attributeName = "supports_inline_queries")
    private Boolean supportInlineQueries;
}
