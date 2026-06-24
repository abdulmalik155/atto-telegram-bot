package api.auto.generate.table;

import api.auto.generate.table.Bot.AttoBot;
import api.auto.generate.table.controller.AuthController;
import api.auto.generate.table.ui.AuthUi;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {
    void main() {

        if (System.getenv("RENDER") != null) {
            System.out.println("Render environment detected. Booting cloud banking core...");

            // CRITICAL FIX: Trigger the background engines so authentication and session state processing work!
            try {
                AuthController authController = new AuthController();
                authController.triggerAutomationWorker();
                authController.triggerAutomationWorker2();
                System.out.println("Asynchronous banking pipelines successfully online.");
            } catch (Exception e) {
                System.out.println("Error initializing background workers: " + e.getMessage());
            }

            // Start the mock HTTP port server so Render keeps the app online
            try {
                int port = System.getenv("PORT") != null ? Integer.parseInt(System.getenv("PORT")) : 8080;
                com.sun.net.httpserver.HttpServer server = com.sun.net.httpserver.HttpServer.create(
                        new java.net.InetSocketAddress(port), 0
                );
                server.createContext("/", exchange -> {
                    String response = "ATTO Bot is Active";
                    exchange.sendResponseHeaders(200, response.length());
                    try (java.io.OutputStream os = exchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                });
                server.start();
                System.out.println("Mock web server listening on port: " + port);
            } catch (Exception e) {
                System.out.println("Failed to start port binding helper: " + e.getMessage());
            }

            // Fire up the Telegram Bot pipeline
            try {
                TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
                botsApi.registerBot(new AttoBot());
                System.out.println("ATTO Bot successfully registered on Render!");
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            // 2. Local Machine fallback: Run your interactive console setup normally
            System.out.println("Local machine detected. Launching full console interface...");
            new AuthUi().run();
        }
    }
}