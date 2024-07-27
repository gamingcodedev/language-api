package de.gamingcode.languageapi;

import org.bukkit.entity.Player;

public interface MessageRepository {

    void sendMessage(Player player, String locale, String messageKey, Placeholder... placeholders);

    String getMessage(String locale, String messageKey, Placeholder... placeholders);

    void sendMessage(Player player, String locale, String messageKey);

    String getMessage(String locale, String messageKey);

    void reloadMessages();
}
