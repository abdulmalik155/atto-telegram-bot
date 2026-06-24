package api.auto.generate.table;

import api.auto.generate.table.Bot.AttoBot;
import api.auto.generate.table.ui.AuthUi;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {
    void main() {

        if (System.getenv("RENDER") != null) {
            System.out.println("Render environment detected. Bypassing interactive menu.");

            // Start the mock server so Render doesn't shut us down
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

            // Directly boot up the Telegram Bot on the cloud
            try {
                TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
                botsApi.registerBot(new AttoBot());
                System.out.println("ATTO Bot successfully registered on Render!");
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            // 2. Local Machine fallback: Safe restoration of your full interactive console setup
            System.out.println("Local machine detected. Launching full console interface...");
            new AuthUi().run();
        }

    }
}