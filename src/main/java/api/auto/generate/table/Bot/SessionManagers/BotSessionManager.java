package api.auto.generate.table.Bot.SessionManagers;

import api.auto.generate.table.Bot.AttoBot;
import api.auto.generate.table.Bot.BotHandler;
import api.auto.generate.table.Bot.DtoForBot.UserSessionDTO;
import api.auto.generate.table.controller.AuthController;
import api.auto.generate.table.dto.AuthConfirm;
import api.auto.generate.table.dto.AuthLogin;
import api.auto.generate.table.dto.AuthRegister;
import api.auto.generate.table.entity.Profile;
import api.auto.generate.table.enums.Role;
import api.auto.generate.table.utill.ScannerUtil;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDateTime;

public class BotSessionManager implements BotHandler {

    private final AuthController authController = new AuthController();
    private final BotAdminSession botAdminSession = new BotAdminSession();
    private final BotUserSession botUserSession = new BotUserSession();

    @Override
    public void processWorkflow(UserSessionDTO session, Message message, AttoBot bot, Update update) {
        String step = session.getCurrentStep();

        if (step.startsWith("/start")
                || step.startsWith("register_")
                || step.startsWith("login")
                || step.startsWith("confirm")
        || step.startsWith("Register Complete")) {
            manageAuth(session, message, bot);
        }

        if (session.getCurrentStep().startsWith("ADMIN")) {
            bot.send(message.getChatId(), "Login successful! Adminjon");
            bot.send(message.getChatId(), "Building the Admin Panel");
            botAdminSession.processWorkflow(session, message, bot, update);
        } else if (session.getCurrentStep().startsWith("USER")) {
            if (session.getLoginCount() == 0) {
                bot.send(message.getChatId(), "Login successful! \uD83D\uDD13 \n" + session.getAuthenticatedUser().getName().trim().toUpperCase());
                session.setLoginCount(1);
            }
            botUserSession.processWorkflow(session, message, bot, update);
        }
    }

    private void manageAuth(UserSessionDTO session, Message message, AttoBot bot) {
        if (session == null || message == null) return;
        long chatId = message.getChatId();
        String text = message.hasText() ? message.getText().trim() : null;

        String currentStep = session.getCurrentStep();
        switch (currentStep) {
            case "/start" -> {
                if (text != null && text.equals("Register")) {
                    bot.send(chatId, "Enter your name: ");
                    session.setCurrentStep("register_name");
                } else if (text != null && text.equals("Login")) {
                    bot.send(chatId, "Enter your phone number: ");
                    bot.shareContact(chatId);
                    session.setCurrentStep("login_phone");
                }
            }
            case "register_name" -> {
                if (text != null) {
                    session.getRegistrationProfile().setName(text);
                    session.setCurrentStep("register_surname");
                    bot.send(chatId, "Enter your surname: ");
                }
            }
            case "register_surname" -> {
                if (text != null) {
                    session.getRegistrationProfile().setSurname(text);
                    session.setCurrentStep("register_phone");
                    bot.shareContact(chatId);
                }
            }
            case "register_phone" -> {
                Contact contact = message.hasContact() ? message.getContact() : null;
                String phoneNumber = contact != null ? contact.getPhoneNumber().trim() : null;

                if (phoneNumber != null || text != null) {
                    if (phoneNumber == null) {
                        session.getRegistrationProfile().setPhone(text);
                    } else {
                        session.getRegistrationProfile().setPhone(phoneNumber);
                    }
                    session.setCurrentStep("register_password");
                    bot.send(chatId, "Enter your password: ");
                }
            }
            case "register_password" -> {
                if (text != null) {
                    session.getRegistrationProfile().setPswd(text);
                    session.setConfirmationCode(ScannerUtil.generateCodeForConfirmation());
                    session.setCodeCreatedTime(LocalDateTime.now());

                    bot.send(chatId, "Qani hoziroq " + session.getConfirmationCode() + " \uD83D\uDC48 kiritingchi ");
                    session.setCurrentStep("confirm");
                }
            }
            case "confirm" -> {
                if (text != null) {
                    AuthConfirm authConfirm = new AuthConfirm(
                            session.getCodeCreatedTime(),
                            LocalDateTime.now(),
                            text, session.getConfirmationCode());
                    if (authController.confirmCode(authConfirm)) {
                        bot.send(chatId, "Code confirmed!");
                        session.setCurrentStep("Register Complete");

                        bot.oneFunctionButton(chatId, "Register Complete", "Register Complete <- shuni bosib yuborin", false);
                    } else {
                        bot.send(chatId, "Ha jigar nima bo'ldi ne unaqa qivos" +
                                "\n yoki 1 daqiqalik muddat tugdimi." +
                                "\n Mayli qaytadan urinib ko'ring." +
                                "\n Hozir sizni menuga qaytaraman");
                        bot.startMenu(chatId);
                        session.setCurrentStep("/start");
                    }
                }
            }
            case "Register Complete" -> {
                AuthRegister authRegister = new AuthRegister(
                        session.getRegistrationProfile().getName(),
                        session.getRegistrationProfile().getSurname(),
                        session.getRegistrationProfile().getPhone(),
                        session.getRegistrationProfile().getPswd());
                session.setSavedControllerResponse(authController.register(authRegister));

                if (session.getSavedControllerResponse() != null) {
                    bot.send(chatId, session.getSavedControllerResponse());
                } else {
                    bot.send(chatId, "Brat siz bor ekansizu! \uD83D\uDE12");
                }
                bot.startMenu(chatId);
                session.setCurrentStep("/start");
            }
            case "login_phone" -> {
                Contact contact = message.hasContact() ? message.getContact() : null;
                String phoneNumber = contact != null ? contact.getPhoneNumber().trim() : null;
                if (phoneNumber != null || text != null) {
                    if (phoneNumber == null) {
                        session.getRegistrationProfile().setPhone(text);
                    } else {
                        session.getRegistrationProfile().setPhone(phoneNumber);
                    }
                    bot.send(chatId, "Enter your password: ");
                    session.setCurrentStep("login_password");
                } else {
                    bot.send(chatId, "Nimadir bo'ldi shu sizga xabar jo'natvotganimda!");
                }
            }
            case "login_password" -> {
                if (text != null) {
                    session.getRegistrationProfile().setPswd(text);
                    AuthLogin authLogin = new AuthLogin(session.getRegistrationProfile().getPhone());
                    Profile profile = authController.login(authLogin);
                    if (profile != null && profile.getRole().equals(Role.USER)) {
                        session.setCurrentStep("USER");
                        session.setAuthenticatedUser(profile);
                        session.getAuthenticatedUser().setChatId(chatId);

                    } else if (profile != null && profile.getRole().equals(Role.ADMIN)) {
                        session.setCurrentStep("ADMIN");
                        session.setAuthenticatedUser(profile);
                        session.getAuthenticatedUser().setChatId(chatId);
                    } else {
                        bot.send(chatId, "Login failed!");
                        bot.startMenu(chatId);
                        session.setCurrentStep("/start");
                    }
                }
            }
        }
    }
}
