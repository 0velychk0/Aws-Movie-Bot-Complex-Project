package org.ovelychko.awsbotsystem;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.Random;
import java.util.StringJoiner;

@Slf4j
public class TarotTelegramWebhookBot extends TelegramWebhookBot {
    private static final String CARD_FILE = "tarotcards.properties";
    private static final Random random = new Random();
    private static final String DEBUG_GET_NUM = "debug";
    private static final int CARDS_COUNT = 78;
    private static final String ENDL = "\n";
    private final Configures config = new Configures();

    public String getBotUsername() {
        return config.getUserName();
    }

    public String getBotToken() {
        return config.getBotToken();
    }

    public String getBotPath() {
        return config.getWebHookPath();
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {

        String searchText = update.getMessage().getText().trim();
        log.info("Search Text:{}", searchText);


        StringJoiner joiner = new StringJoiner(ENDL);
        if (update.hasMessage() && update.getMessage().hasText()) {
            log.info("onUpdateReceived from user '{}' the text message: '{}'", update.getMessage().getFrom().getFirstName(), update.getMessage().getText());

            if (update.getMessage().getFrom().getIsBot()) {
                log.info("Bot user is not supported");
                return null;
            }

            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(update.getMessage().getChatId().toString());

            int cardNum = -1;
            boolean debugValue = false;
            if (update.getMessage().getText().startsWith(DEBUG_GET_NUM)) {

                joiner.add("Request: " + update.getMessage().getText());

                try {
                    cardNum = Integer.parseInt(update.getMessage().getText().substring(DEBUG_GET_NUM.length()).trim());
                    debugValue = true;
                } catch (Exception ex) {
                    log.info("Exception: {}", ex.getMessage());
                }
            }
            if (cardNum < 0 || cardNum >= CARDS_COUNT) {
                cardNum = random.nextInt(CARDS_COUNT);
            }

            log.debug("user '{}' cardNum: {}", update.getMessage().getFrom().getFirstName(), cardNum);

            joiner.add(getCardDescription(cardNum, debugValue));

            try {
                SendPhoto message = new SendPhoto();
                message.setChatId(update.getMessage().getChatId().toString());
                message.setCaption(joiner.toString());
                message.setPhoto(new InputFile(getCardImageUrl(config.getImageSecurePictorialLink(), cardNum)));
                this.execute(message);
            } catch (Exception ex) {
                sendMessage.setText(joiner.toString());
                log.info(ex.getMessage());
            }
            return sendMessage;
        }
        return null;
    }

    private String getCardDescription(int cardNum, boolean debugValue) {
        StringJoiner joiner = new StringJoiner(ENDL);
        try {
            ClassLoader classLoader = TarotTelegramWebhookBot.class.getClassLoader();
            InputStream inputStream = classLoader.getResourceAsStream(CARD_FILE);
            Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);

            if (inputStream == null) {
                joiner.add("Sorry, unable to find " + CARD_FILE);
                log.info("Sorry, unable to find " + CARD_FILE);
                return joiner.toString();
            }

            Properties property = new Properties();
            property.load(reader);
            joiner.add(property.getProperty("greeting"));

            if (debugValue) joiner.add("debug value: " + cardNum);
            else joiner.add(property.getProperty("random_card"));

            String cardOfTheDay = property.getProperty("card_" + cardNum);
            joiner.add(cardOfTheDay);
        } catch (Exception ex) {
            joiner.add("Exception: " + ex.getMessage());
            log.info("Exception: {}", ex.getMessage());
        }
        return joiner.toString();
    }

    private String getCardImageUrl(String template, int cardNum) {
        String fileName = String.format(template, cardNum);
        return fileName;
    }
}
