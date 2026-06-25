package api.auto.generate.table.Bot.SessionManagers;

import api.auto.generate.table.Bot.AttoBot;
import api.auto.generate.table.Bot.BotHandler;
import api.auto.generate.table.Bot.DtoForBot.UserSessionDTO;
import api.auto.generate.table.controller.ProfileController;
import api.auto.generate.table.dto.CardRefill;
import api.auto.generate.table.dto.PaymentRequest;
import api.auto.generate.table.dto.RequestForCard;
import api.auto.generate.table.entity.Card;
import api.auto.generate.table.entity.Terminal;
import api.auto.generate.table.entity.Transaction;
import api.auto.generate.table.repository.TerminalRepository;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.TimeUnit;

public class BotUserSession implements BotHandler {
    private final ProfileController profileController = new ProfileController();

    @Override
    public void processWorkflow(UserSessionDTO session, Message message, AttoBot bot, Update update) {
        if (message == null || session == null) return;

        long chatId = message.getChatId();
        String text = message.hasText() ? message.getText().trim() : null;

        if (session.getCurrentStep().equals("USER")) {
            session.setCurrentStep("USER_MENU");
        }
        if (session.getCurrentStep().startsWith("USER_MENU") ||
                (text != null && text.startsWith("USER_MENU"))) {
            manageUserActions(session, message, bot, update);
        }

        if (session.getCurrentStep().startsWith("USER_card_section") ||
                (text != null && text.startsWith("USER_card_section"))) {
            handleCardSection(session, message, bot, update);
        }

        if (session.getCurrentStep().startsWith("USER_transaction_section") ||
                (text != null && text.startsWith("USER_transaction_section"))) {
            handleTransactionSection(session, message, bot, update);
        }


    }

    private void manageUserActions(UserSessionDTO session, Message message, AttoBot bot, Update update) {
        if (message == null || session == null) {
            return;
        }

        long chatId = message.getChatId();
        String text = message.hasText() ? message.getText().trim() : null;

        String currentStep = session.getCurrentStep();
        if (update.hasCallbackQuery()) {
            currentStep = getCurrentStepIfCallBackQuery(message, bot, update, currentStep);
            session.setCurrentStep(currentStep);
        }
        switch (currentStep) {
            case "USER_MENU" -> {
                Stack<String> buttons = new Stack<>();
                buttons.push("🏠 Back to Main Menu");
                buttons.push("📊 Transaction Section");
                buttons.push("💳 Card Section");

                Stack<String> callBackDataForButtons = new Stack<>();
                callBackDataForButtons.push("/start");
                callBackDataForButtons.push("USER_transaction_section");
                callBackDataForButtons.push("USER_card_section");
                bot.getMenu("User menuga xush kelibsiz shef \uD83E\uDEE1",
                        message,
                        3,
                        1,
                        buttons,
                        callBackDataForButtons);
            }
            case "USER_MENU_card_section" -> {
                bot.send(message.getChatId(), "    Card Section");
                session.setCurrentStep("USER_card_section");
                handleCardSection(session, message, bot, update);
            }
            case "USER_MENU_transaction_section" -> {
                bot.send(message.getChatId(), "transaction Section");
                session.setCurrentStep("USER_transaction_section");
                handleTransactionSection(session, message, bot, update);
            }
            case "/start" -> {
                bot.send(message.getChatId(), "Back to Main Menu");
                session.setCurrentStep("/start");
            }
        }
    }

    private void handleCardSection(UserSessionDTO session, Message message, AttoBot bot, Update update) {
        if (message == null || session == null) {
            return;
        }

        long chatId = message.getChatId();
        String text = message.hasText() ? message.getText().trim() : null;

        String currentStep = session.getCurrentStep();
        if (update.hasCallbackQuery()) {
            currentStep = getCurrentStepIfCallBackQuery(message, bot, update, currentStep);
            session.setCurrentStep(currentStep);
        }
        switch (currentStep) {
            case "USER_card_section" -> cardSectionMenu(message, bot);
            case "USER_card_section_refill" -> {
                session.setCurrentStep("USER_card_section_refill_card_number");
                cardInput(session, message, bot, chatId);
            }
            case "USER_card_section_refill_card_number" -> {
                if (text != null) {
                    session.setCurrentStep("USER_card_section_refill_amount");
                    amountInput(session, message, bot, text);
                }
            }
            case "USER_card_section_refill_amount" -> {
                if (text != null) {
                    CardRefill cardRefill = new CardRefill(
                            session.getAuthenticatedUser(),
                            session.getCardNumber(),
                            Double.parseDouble(text.trim()));
                    Card card = profileController.reFillForBot(cardRefill);
                    if (card != null) {
                        if (card.getCardNumber().equals(session.getCardNumber())) {
                            String successMsg = String.format(
                                    "✅ *Refill Successful!*\n" +
                                            "\n" +
                                            "💰 *Amount Added:* +`%,.0f` UZS\n" +
                                            "💳 *Target Card:* `" + card.getCardNumber() + "`\n" +
                                            "\n" +
                                            "📊 *New Balance:* `%,.0f` UZS\n" +
                                            "\n" +
                                            "🎉 Your balance has been updated.\n",
                                    Double.parseDouble(text.trim()),
                                    card.getBalance()
                            );
                            bot.send(chatId, successMsg);
                            session.setCurrentStep("USER_card_section");
                            cardSectionMenu(message, bot);
                        }
                    } else {
                        String failMsg = "❌ *Transaction Failed*\n" +
                                "──────────────────────────────\n" +
                                "We could not process your refill request. \n" +
                                "\n" +
                                "Please check that:\n" +
                                "• The card number is correct\n" +
                                "• The amount contains only numbers\n" +
                                "\n" +
                                "\uD83D\uDD04 Please try the operation again.\n" +
                                "──────────────────────────────";
                        bot.send(chatId, failMsg);
                        session.setCurrentStep("USER_card_section");
                        cardSectionMenu(message, bot);
                    }
                }
            }
            case "USER_card_section_delete_card" -> {
                cardInput(session, message, bot, chatId);
                session.setCurrentStep("USER_card_section_delete_card_card_number");
            }
            case "USER_card_section_delete_card_card_number" -> {
                if (text != null) {

                    session.setCurrentStep("USER_card_section_delete_card_exp_date");
                    session.setCardNumber(text);
                    Stack<String> buttons = new Stack<>();
                    buttons.push("/cancel");
                    Stack<String> callBackDataForButtons = new Stack<>();
                    callBackDataForButtons.push("USER_card_section");

                    String step2Msg = "⏳ Enter Expiry Date [YYYY-MM-DD] ✨ ❯ ";
                    bot.getMenu(step2Msg,
                            message,
                            1,
                            1,
                            buttons,
                            callBackDataForButtons);
                }
            }
            case "USER_card_section_delete_card_exp_date" -> {
                if (text != null) {
                    boolean result = profileController.deleteCard(session.getCardNumber(), text);
                    if (result) {
                        bot.send(chatId,
                                "\n       CARD REMOVED \uD83D\uDDD1\uFE0F\n" +
                                        "✔️ Success! The card has been permanently deleted.\n"
                        );
                        session.setCurrentStep("USER_card_section");
                    } else {
                        bot.send(chatId,
                                "\n       REMOVAL FAILED ⚠️\n" +
                                        "❌ Error! The card could not be deleted.\n"
                        );
                    }
                    session.setCurrentStep("USER_card_section");
                    cardSectionMenu(message, bot);
                }
            }
            case "USER_card_section_card_list" -> {
                bot.send(chatId, "Marhamat sizzi kartalariz \uD83D\uDC47");
                cardList(session, bot, chatId);
                session.setCurrentStep("USER_card_section");
                cardSectionMenu(message, bot);
            }
            case "USER_card_section_add_card" -> {
                Card card = profileController.addCard(session.getAuthenticatedUser());
                if (card != null) {
                    bot.send(chatId, "Card Added ✅");
                    bot.send(chatId, String.valueOf(card));
                } else {
                    String earlyClickMsg = "❌ Card Generation Incomplete\n\n" +
                            "We couldn't find a completed card request for your profile yet.\n\n" +
                            "💡 Reason: The 1-minute background card creator hasn't run yet.\n" +
                            "⌛ Please wait a moment and try pushing the button again!";
                    bot.send(chatId, earlyClickMsg);
                }
                session.setCurrentStep("USER_card_section");
                cardSectionMenu(message, bot);
            }
            case "USER_card_section_card_request" -> {
                cardRequest(message, bot);
            }
            case "USER_card_section_request_UzCard" -> {

                RequestForCard request = new RequestForCard(session.getAuthenticatedUser(), "1");
                if (profileController.cardRequest(request)) {
                    String msg = "⏳ *Request Received!*\n\n" +
                            "Our banking automation system is generating your card.\n" +
                            "⚠️ *Please wait 1 minute* before pushing the *Add Card* button.";
                    bot.send(chatId, msg);
                    startCardGenerationTimer(message, bot, 60);
                } else {
                    bot.send(chatId, "Card Request Failed ❌");
                }
                session.setCurrentStep("USER_card_section");
            }
            case "USER_card_section_request_Humo" -> {
                RequestForCard request = new RequestForCard(session.getAuthenticatedUser(), "2");
                if (profileController.cardRequest(request)) {
                    String msg = "⏳ *Request Received!*\n\n" +
                            "Our banking automation system is generating your card.\n" +
                            "⚠️ *Please wait 1 minute* before pushing the *Add Card* button.";
                    bot.send(chatId, msg);
                    startCardGenerationTimer(message, bot, 60);
                } else {
                    bot.send(chatId, "Card Request Failed ❌");
                }
                session.setCurrentStep("USER_card_section");
            }
        }
    }

    private void handleTransactionSection(UserSessionDTO session, Message message, AttoBot bot, Update update) {
        if (message == null || session == null) return;

        long chatId = message.getChatId();
        String text = message.hasText() ? message.getText().trim() : null;
        String currentStep = session.getCurrentStep();

        // 1. Button interactions update the step tracking map
        if (update.hasCallbackQuery()) {
            currentStep = getCurrentStepIfCallBackQuery(message, bot, update, currentStep);
            session.setCurrentStep(currentStep);
        }

        // 2. CRITICAL INTERCEPT FOR TYPED CARD NUMBER TEXT INPUTS
        if ("USER_transaction_section_card_number".equals(currentStep) && text != null) {
            session.setCardNumber(text);

            List<Terminal> terminalList = new ArrayList<>(new api.auto.generate.table.repository.TerminalRepository().read());
            if (!terminalList.isEmpty()) {
                Collections.shuffle(terminalList);
                Terminal terminal = terminalList.getLast();
                bot.send(chatId, terminal.toString());
            }

            terminalCodeInput(session, message, bot, chatId);
            session.setCurrentStep("USER_transaction_section_terminal_code");
            return; // Break processing loop successfully
        }

        // 3. CRITICAL INTERCEPT FOR TYPED TERMINAL CODES TEXT INPUTS
        if ("USER_transaction_section_terminal_code".equals(currentStep) && text != null) {
            api.auto.generate.table.dto.PaymentRequest paymentRequest = new api.auto.generate.table.dto.PaymentRequest(session.getCardNumber(), text);
            String result = profileController.makePayment(paymentRequest, session.getAuthenticatedUser());

            if (result != null) {
                if (result.equals("Success")) {
                    bot.send(chatId, "✅ " + result);
                } else {
                    bot.send(chatId, "❌ " + result + " Try again!");
                }
                session.setCurrentStep("USER_transaction_section");
                transactionSectionMenu(message, bot);
            }
            return; // Break processing loop successfully
        }

        // 4. Standard Inline Action Router menu paths
        switch (currentStep) {
            case "USER_transaction_section" -> transactionSectionMenu(message, bot);

            case "USER_transaction_section_transaction_list" -> {
                List<Transaction> transactions = profileController.transactionList(session.getAuthenticatedUser());
                if (transactions.isEmpty()) {
                    bot.send(chatId, "No transactions found!");
                } else {
                    for (Transaction transaction : transactions) {
                        bot.send(chatId, String.valueOf(transaction));
                    }
                }
                session.setCurrentStep("USER_transaction_section");
                transactionSectionMenu(message, bot);
            }

            case "USER_transaction_section_make_payment" -> {
                cardInput(session, message, bot, chatId);
                session.setCurrentStep("USER_transaction_section_card_number");
            }
        }
    }



 /*   private void handleTransactionSection(UserSessionDTO session, Message message, AttoBot bot, Update update) {
        if (message == null || session == null) return;

        long chatId = message.getChatId();
        String text = message.hasText() ? message.getText().trim() : null;

        String currentStep = session.getCurrentStep();
        if (update.hasCallbackQuery()) {
            currentStep = getCurrentStepIfCallBackQuery(message, bot, update, currentStep);
            session.setCurrentStep(currentStep);
        }
        switch (currentStep) {
            case "USER_transaction_section" -> transactionSectionMenu(message, bot);
            case "USER_transaction_section_transaction_list" -> {
                List<Transaction> transactions = profileController.transactionList(session.getAuthenticatedUser());
                if (transactions.isEmpty()) {
                    bot.send(chatId, "No transactions found!");
                } else {
                    for (Transaction transaction : transactions) {
                        bot.send(chatId, String.valueOf(transaction));
                    }
                }
                session.setCurrentStep("USER_transaction_section");
                transactionSectionMenu(message, bot);
            }
            case "USER_transaction_section_make_payment" -> {
                cardInput(session, message, bot, chatId);
                session.setCurrentStep("USER_transaction_section_card_number");
            }
            case "USER_transaction_section_card_number" -> {
                if (text != null) {
                    session.setCardNumber(text);
                    List<Terminal> terminalList = new ArrayList<>(new TerminalRepository().read().stream().toList());
                    Collections.shuffle(terminalList);
                    Terminal terminal = terminalList.getLast();
                    bot.send(chatId, terminal.toString());
                    terminalCodeInput(session, message, bot, chatId);
                    session.setCurrentStep("USER_transaction_section_terminal_code");
                } else {
                    bot.send(chatId, "Something went wrong! Please try again later.");
                    session.setCurrentStep("USER_transaction_section");
                }
            }
            case "USER_transaction_section_terminal_code" -> {
                if (text != null) {
                    PaymentRequest paymentRequest = new PaymentRequest(session.getCardNumber(), text);
                    String result = profileController.makePayment(paymentRequest, session.getAuthenticatedUser());
                    if (result != null) {
                        if (result.equals("Success")) {
                            bot.send(chatId, "✅ " + result);
                        } else {
                            bot.send(chatId, "❌ " + result + " Try again!");
                        }
                        session.setCurrentStep("USER_transaction_section");
                        transactionSectionMenu(message, bot);
                    }
                }
            }

        }

    }*/

    private static void transactionSectionMenu(Message message, AttoBot bot) {
        Stack<String> buttons = new Stack<>();
        buttons.push("◀️");
        buttons.push("📜 Transaction List");
        buttons.push("💸 Make Payment");

        Stack<String> callBackDataForButtons = new Stack<>();
        callBackDataForButtons.push("USER_MENU");
        callBackDataForButtons.push("USER_transaction_section_transaction_list");
        callBackDataForButtons.push("USER_transaction_section_make_payment");
        bot.getMenu("Betda endi uyodan buyaqqa pul o'tkaziladi ",
                message,
                3,
                1,
                buttons,
                callBackDataForButtons);
    }

    public void startCardGenerationTimer(Message originalMessage, AttoBot bot, int totalSeconds) {
        long chatId = originalMessage.getChatId();

        SendMessage sendMsg = new SendMessage();
        sendMsg.setChatId(String.valueOf(chatId));
        sendMsg.setText("⏳ Processing card generation... Time remaining: " + totalSeconds + "s");

        try {
            Message timerMessage = bot.execute(sendMsg);
            int timerMessageId = timerMessage.getMessageId();
            final int[] secondsLeft = {totalSeconds};

            AttoBot.getUiScheduler().scheduleAtFixedRate(() -> {
                secondsLeft[0] -= 5;

                if (secondsLeft[0] <= 0) {
                    DeleteMessage deleteMsg = new DeleteMessage();
                    deleteMsg.setChatId(String.valueOf(chatId));
                    deleteMsg.setMessageId(timerMessageId);
                    try {
                        bot.execute(deleteMsg);

                        SendMessage readyMsg = new SendMessage();
                        readyMsg.setChatId(String.valueOf(chatId));
                        readyMsg.setText("✅ Generation complete! You can now press the 'Add Card' button.");
                        bot.execute(readyMsg);
                        cardSectionMenu(originalMessage, bot);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    throw new RuntimeException("Timer completed successfully.");
                } else {
                    EditMessageText editMsg = new EditMessageText();
                    editMsg.setChatId(String.valueOf(chatId));
                    editMsg.setMessageId(timerMessageId);
                    editMsg.setText("⏳ Processing card generation... Time remaining: " + secondsLeft[0] + "s");

                    try {
                        bot.execute(editMsg);
                    } catch (TelegramApiException e) {
                        String errorDesc = e.getMessage() != null ? e.getMessage() : "";
                        if (errorDesc.contains("Forbidden: bot was blocked by the user") || errorDesc.contains("Chat not found")) {
                            throw new RuntimeException("Termination: User blocked bot.");
                        }

                        // FIXED SEPARATION: Safe logging for message modification warnings
                        if (errorDesc.contains("message is not modified")) {
                            System.out.println("INFO: Text un-modified on this tick. Keeping timer alive.");
                        } else if (errorDesc.contains("Bad Request: message to edit not found")) {
                            throw new RuntimeException("Termination: Message missing.");
                        }

                        if (errorDesc.contains("Too Many Requests")) {
                            System.err.println("WARN: Rate limited on UI timer. Skipping tick.");
                        }
                    }
                }
            }, 5, 5, TimeUnit.SECONDS);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static void amountInput(UserSessionDTO session, Message message, AttoBot bot, String text) {
        Stack<String> buttons = new Stack<>();
        buttons.push("/cancel");
        Stack<String> callBackDataForButtons = new Stack<>();
        callBackDataForButtons.push("USER_card_section");
        String step2Msg = "\uD83D\uDCE5 *Enter Amount*\n" +
                "\n" +
                "Card selected: `" + text + "`\n" +
                "\n" +
                "Please send the total amount you want to transfer.\n" +
                "\n" +
                "\uD83D\uDCA1 *Example:* `150000`\n" +
                "\n" +
                "❌ To stop this process, push the button below\n";
        bot.getMenu(step2Msg,
                message,
                1,
                1,
                buttons,
                callBackDataForButtons);
        session.setCardNumber(text);
    }

    private void cardInput(UserSessionDTO session, Message message, AttoBot bot, long chatId) {
        cardList(session, bot, chatId);
        Stack<String> buttons = new Stack<>();
        buttons.push("/cancel");
        Stack<String> callBackDataForButtons = new Stack<>();
        callBackDataForButtons.push("USER_card_section");
        String step1Msg = """
                💳 *Card Validation*
                
                Please enter your 16-digit bank card number.
                
                ⚠️ *Rules:*
                • Numbers only (digits 0-9)
                • Do not include spaces or dashes (-)
                
                💡 *Example:* `8600123456789012`
                
                ❌ To stop this process, push the button below
                """;
        bot.getMenu(step1Msg,
                message,
                1,
                1,
                buttons,
                callBackDataForButtons);
    }

    private void terminalCodeInput(UserSessionDTO session, Message message, AttoBot bot, long chatId) {
        Stack<String> buttons = new Stack<>();
        buttons.push("/cancel");
        Stack<String> callBackDataForButtons = new Stack<>();
        callBackDataForButtons.push("USER_transaction_section");
        String stepMsg = """
                \uD83D\uDCCD Please enter the Terminal Code to proceed.
                
                ❌ To stop this process, push the button below
                """;
        bot.getMenu(stepMsg,
                message,
                1,
                1,
                buttons,
                callBackDataForButtons);
    }


    private void cardList(UserSessionDTO session, AttoBot bot, Long chatId) {
        for (Card cards : profileController.cardList(session.getAuthenticatedUser())) {
            bot.send(chatId, cards.toString());
        }
    }


    private static void cardRequest(Message message, AttoBot bot) {
        bot.send(message.getChatId(), "Card Request");
        Stack<String> buttons = new Stack<>();
        buttons.push("◀️");
        buttons.push("💳 Humo");
        buttons.push("💳 UzCard");

        Stack<String> callBackDataForButtons = new Stack<>();
        callBackDataForButtons.push("USER_card_section");
        callBackDataForButtons.push("USER_card_section_request_Humo");
        callBackDataForButtons.push("USER_card_section_request_UzCard");
        bot.getMenu("Karta qanaqa bo'sin \uD83E\uDDD0",
                message,
                2,
                2,
                buttons,
                callBackDataForButtons);
    }

    private static String getCurrentStepIfCallBackQuery(Message message, AttoBot bot, Update update, String currentStep) {
        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            currentStep = callbackQuery.getData();

            try {
                AnswerCallbackQuery answer = new AnswerCallbackQuery();
                answer.setCallbackQueryId(callbackQuery.getId());
                bot.execute(answer);
            } catch (Exception e) {
                bot.send(message.getChatId(), "An error occured while processing the callback query!");
            }
        }
        return currentStep;
    }

    private void cardSectionMenu(Message message, AttoBot bot) {
        Stack<String> buttons = new Stack<>();
        buttons.push("◀️");
        buttons.push("🔄 ReFill");
        buttons.push("🗑️ Delete Card");
        buttons.push("📇 Card List");
        buttons.push("➕ Add Card");
        buttons.push("📩 Card Request");

        Stack<String> callBackDataForButtons = new Stack<>();
        callBackDataForButtons.push("USER_MENU");
        callBackDataForButtons.push("USER_card_section_refill");
        callBackDataForButtons.push("USER_card_section_delete_card");
        callBackDataForButtons.push("USER_card_section_card_list");
        callBackDataForButtons.push("USER_card_section_add_card");
        callBackDataForButtons.push("USER_card_section_card_request");
        bot.getMenu("Karta bilan mashu ishlani qisez bo'ladi \uD83D\uDC47",
                message,
                3,
                2,
                buttons,
                callBackDataForButtons);
    }
}
