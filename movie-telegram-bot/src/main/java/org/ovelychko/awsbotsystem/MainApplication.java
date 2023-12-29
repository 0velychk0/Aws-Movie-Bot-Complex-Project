package org.ovelychko.awsbotsystem;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
public class MainApplication implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final TelegramWebhookBot SENDER = new MovieTelegramWebhookBot();

    public MainApplication() {
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("handleRequest, body: " + event.getBody());

        try {
            Update update= MAPPER.readValue(event.getBody(), Update.class);
            logger.log("handleRequest, update: " + update);

            if (update == null) {
                return new APIGatewayProxyResponseEvent()
                        .withStatusCode(200)
                        .withBody("update mapper failed")
                        .withIsBase64Encoded(false);
            }
            if (!update.hasMessage()) {
                return new APIGatewayProxyResponseEvent()
                        .withStatusCode(200)
                        .withBody("message is missing")
                        .withIsBase64Encoded(false);
            }
            if (update.getMessage().getFrom().getIsBot()) {
                return new APIGatewayProxyResponseEvent()
                        .withStatusCode(200)
                        .withBody("telegram bot is not supported")
                        .withIsBase64Encoded(false);
            }

            SENDER.onWebhookUpdateReceived(update);

            // AwsLambdaCallUtil.saveUserData(update);

            // AwsLambdaCallUtil.saveUserRequestData(update);

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse update!", e);
        }

        return new APIGatewayProxyResponseEvent()
                .withStatusCode(200)
                .withBody("Done")
                .withIsBase64Encoded(false);
    }

}
