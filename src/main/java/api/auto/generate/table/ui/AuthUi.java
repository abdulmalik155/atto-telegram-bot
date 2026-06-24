package api.auto.generate.table.ui;

import api.auto.generate.table.Bot.AttoBot;
import api.auto.generate.table.controller.AuthController;
import api.auto.generate.table.dto.AuthConfirm;
import api.auto.generate.table.dto.AuthLogin;
import api.auto.generate.table.dto.AuthRegister;
import api.auto.generate.table.entity.Profile;
import api.auto.generate.table.enums.Role;
import api.auto.generate.table.utill.ScannerUtil;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.time.LocalDateTime;

public class AuthUi {
    private final AuthController authController = new AuthController();

    public void run() {
        authController.triggerAutomationWorker();
        authController.triggerAutomationWorker2();
        while (true) {
            switch (authUsingBotOrAppMenu()) {
                case 1 -> authByBot();
                case 2 -> authByApp();
                case 0 -> {
                    return;
                }
            }
        }
    }

    private void authByBot(){
        try{
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new AttoBot());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private int authUsingBotOrAppMenu() {
        return ScannerUtil.getOption("""
                1. Auth using Bot
                2. Auth using App
                0. Exit
                Enter your choice:""");
    }

    private void authByApp(){
        while (true) {
            switch (menu()) {
                case 1 -> login();
                case 2 -> register();
                case 0 -> {
                    return;
                }
            }
        }
    }

    public void login() {
        System.out.println("Login");
        String username = ScannerUtil.getString("Enter your phone number: ");
        String password = ScannerUtil.getString("Enter your password: ");
        AuthLogin authLogin = new AuthLogin(username);
        Profile profile = authController.login(authLogin);
        if (profile != null && profile.getRole().equals(Role.USER)) {
            ProfileUi profileUi = new ProfileUi(profile);
            profileUi.run();
        }else if (profile != null && profile.getRole().equals(Role.ADMIN)) {
            AdminUi adminUi = new AdminUi();
            adminUi.run();
        }else {
            System.out.println("Login failed!");
        }
    }

    private void register() {
        System.out.println("Register");
        String name = ScannerUtil.getString("Enter your name: ");
        String surname = ScannerUtil.getString("Enter your surname: ");
        String phone = ScannerUtil.getString("Enter your phone: ");
        String pswd = ScannerUtil.getString("Enter your password: ");
        AuthRegister request = new AuthRegister(name, surname, phone, pswd);
        String response = authController.register(request);
        if (response != null) {
            if (confirm()) {
                System.out.println(response);
                return;
            }
        }
        System.out.println("Register failed!");
    }

    private boolean confirm() {
        String confirmationCode = ScannerUtil.generateCode();
        LocalDateTime createdTime = LocalDateTime.now();
        String userInput = ScannerUtil.getString("Enter the code: ");
        LocalDateTime inputTime = LocalDateTime.now();
        AuthConfirm request = new AuthConfirm(createdTime, inputTime, userInput, confirmationCode);
        return authController.confirmCode(request);
    }

    private int menu() {
        return ScannerUtil.getOption("""
                        Menu
                1. Login
                2. Register
                0. Exit
                Enter your choice:""");
    }
}
