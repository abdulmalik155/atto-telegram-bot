package api.auto.generate.table.Bot;

import api.auto.generate.table.Bot.DtoForBot.UserSessionDTO;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface BotHandler {
        void processWorkflow(UserSessionDTO session, Message message, AttoBot bot, Update update);
}
