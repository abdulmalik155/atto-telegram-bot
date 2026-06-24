package api.auto.generate.table.Bot;

import api.auto.generate.table.Bot.DtoForBot.UserSessionDTO;
import api.auto.generate.table.Bot.SessionManagers.BotSessionManager;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class AttoBot extends TelegramLongPollingBot {
    private final Map<Long, UserSessionDTO> activeSessions = new HashMap<>();
    private final BotSessionManager sessionManager = new BotSessionManager();

    private static final int TARGET_THREAD_COUNT = Runtime.getRuntime().availableProcessors() * 4;

    // The correct, globally scalable scheduler instance
    private static final ScheduledExecutorService globalUiScheduler = Executors.newScheduledThreadPool(TARGET_THREAD_COUNT);

    public static ScheduledExecutorService getUiScheduler() {
        return globalUiScheduler;
    }
    public AttoBot() {
        super("8590435837:AAGLcMAwtlnwNxF_vPdrZ_Pc_aC-OiKavlo");
        System.out.println("https://t.me/contactino_bot");
    }

    @Override
    public void onUpdateReceived(Update update) {
        long chatId;
        String text = "";

        if (update.hasMessage()) {
            chatId = update.getMessage().getChatId();
            if (update.getMessage().hasText()) {
                text = update.getMessage().getText().trim();
            }
        } else if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getMessage().getChatId();
            text = update.getCallbackQuery().getData().trim();
        } else {
            return;
        }
        try {
            AnswerCallbackQuery answer =
                    new org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery();
            answer.setCallbackQueryId(update.getCallbackQuery().getId());
            execute(answer); // Tells Telegram the button click was handled successfully
        } catch (Exception e) {
            System.out.println("Failed to unlock inline interface: " + e.getMessage());
        }

        if (text.equals("/start") ) {
            UserSessionDTO session = activeSessions.get(chatId);
            if (session != null) {
                session.setCurrentStep("/start");
                session.setLoginCount(0);
            }
            startMenu(chatId);
            return;
        }

        if (!activeSessions.containsKey(chatId)) {
            activeSessions.put(chatId, new UserSessionDTO());
        }

        UserSessionDTO userSession = activeSessions.get(chatId);

        Message currentMessage = update.hasMessage() ? update.getMessage() : (Message) update.getCallbackQuery().getMessage();

        if (update.hasCallbackQuery()) {
            currentMessage.setText(text);
        }

        sessionManager.processWorkflow(userSession, currentMessage, this, update);
    }

    @Override
    public String getBotUsername() {
        return "@contactino_bot";
    }


    public void startMenu(long chatId) {
        Stack<String> buttons = new Stack<>();
        buttons.push("Login");
        buttons.push("Register");
        String messageText = "Assalomu alaykooom boy ota \uD83D\uDE0A";

        try {
            execute(getButtons(chatId,
                    buttons,
                    2,
                    2,
                    messageText,
                    false,
                    null));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void showMyProfile(long chatId) {
        Stack<String> buttons = new Stack<>();
        buttons.push("Show my profile");
        buttons.push("No");
        try {
            execute(getButtons(chatId,
                    buttons,
                    1,
                    2,
                    "Do you want to see your profile?",
                    false,
                    null));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public SendMessage getButtons(long chatId,
                                  Stack<String> buttonNames,
                                  int row,
                                  int col,
                                  String messageText,
                                  boolean sendContact,
                                  String inputFieldPlaceHolder) {
        List<KeyboardRow> rows = new ArrayList<>();
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId);
        sendMessage.setText(messageText);

        for (int i = 0; i < row; i++) {
            KeyboardRow keyboardRow = new KeyboardRow();
            for (int j = 0; j < col; j++) {
                if (!buttonNames.isEmpty()) {
                    KeyboardButton keyboardButton = new KeyboardButton(buttonNames.pop());
                    if (sendContact && row == 1 && col == 1) {
                        keyboardButton.setRequestContact(true);
                    }
                    keyboardRow.add(keyboardButton);
                }
            }
            rows.add(keyboardRow);
        }
        ReplyKeyboardMarkup replyKeyboardMarkup = getReplyKeyboardMarkup(inputFieldPlaceHolder, rows);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        return sendMessage;
    }

    private static ReplyKeyboardMarkup getReplyKeyboardMarkup(String inputFieldPlaceHolder, List<KeyboardRow> rows) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setKeyboard(rows);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        replyKeyboardMarkup.setIsPersistent(true);
        if (inputFieldPlaceHolder != null) {
            replyKeyboardMarkup.setInputFieldPlaceholder(inputFieldPlaceHolder);
        } else {
            replyKeyboardMarkup.setInputFieldPlaceholder("So'ylang birodar");
        }
        return replyKeyboardMarkup;
    }

    public SendMessage getInlineButtons(long chatId,
                                        Stack<String> buttonNames,
                                        int row,
                                        int col,
                                        String messageText,
                                        Stack<String> callBackData) {
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId);
        sendMessage.setText(messageText);

        if (callBackData != null && callBackData.size() == buttonNames.size()) {
            for (int i = 0; i < row; i++) {
                List<InlineKeyboardButton> rows = new ArrayList<>();
                for (int j = 0; j < col; j++) {
                    if (!buttonNames.isEmpty()) {
                        InlineKeyboardButton inlineButton = new InlineKeyboardButton();
                        inlineButton.setText(buttonNames.pop());
                        inlineButton.setCallbackData(callBackData.pop());
                        rows.add(inlineButton);
                    }
                }
                rowList.add(rows);
            }
        }
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(rowList);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        return sendMessage;
    }

    public void send(Long chatId, String str) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(str);
        sendTypingStatus(chatId);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            send(chatId, "Nimadir bo'ldi shu sizga xabar jo'natvotganimda!");
        }
    }

    public void shareContact(Long chatId) {
        Stack<String> shareButton = new Stack<>();
        shareButton.push("Share Contact");

        try {
            execute(getButtons(chatId,
                    shareButton,
                    1,
                    1,
                    "Share Contact <- shuni bosing!",
                    true,
                    null));
        } catch (Exception e) {
            send(chatId, "Error: uzur muammocha paydo bo'ldi! `share contact`!`");
        }
    }

    public void oneFunctionButton(Long chatId, String function, String messageText, boolean sendContact) {
        Stack<String> shareButton = new Stack<>();
        shareButton.push(function);

        try {
            execute(getButtons(chatId,
                    shareButton,
                    1,
                    1,
                    messageText,
                    sendContact,
                    null));
        } catch (Exception e) {
            send(chatId, "Error: uzur muammocha paydo bo'ldi! `oneFunction`!`");
        }
    }

    public void sendTypingStatus(Long chatId) {
        SendChatAction sendChatAction = new SendChatAction();
        sendChatAction.setChatId(chatId);
        sendChatAction.setAction(ActionType.TYPING);

        try {
            execute(sendChatAction);
        } catch (TelegramApiException e) {
            send(chatId, "Error: uzur muammocha paydo bo'ldi! `typingStatus`!`");
        }
    }

    public void getMenu(String text, Message message, int row, int col, Stack<String> buttons, Stack<String> callBackData) {
        SendMessage sendMessage = getInlineButtons(message.getChatId(),
                buttons,
                row,
                col,
                    text,
                callBackData);
        try {
            execute(sendMessage);
        } catch (Exception e) {
            send(message.getChatId(), "Nimadir bo'ldi shu sizga xabar jo'natvotganimda!");
        }
    }
}
