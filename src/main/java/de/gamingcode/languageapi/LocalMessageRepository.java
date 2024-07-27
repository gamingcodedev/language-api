package de.gamingcode.languageapi;

import com.google.common.collect.Maps;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class LocalMessageRepository
        implements MessageRepository {

    private final File localeDirectory;

    private final Map<String, Map<String, String>> localesCache = Maps.newHashMap();

    private LocalMessageRepository(Plugin plugin) {

        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }

        this.localeDirectory = new File(plugin.getDataFolder() + "/locales");

        if (!this.localeDirectory.exists()) {
            this.localeDirectory.mkdir();
            try {
                new File(this.localeDirectory, "messages_en.yml").createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        this.loadLocales();
    }

    public static LocalMessageRepository register(Plugin plugin) {
        return new LocalMessageRepository(plugin);
    }

    private void loadLocales() {
        for (File file : this.localeDirectory.listFiles()) {
            String localeName = file.getName().split("_")[1];
            Map<String, String> locales = this.localesCache.getOrDefault(localeName.toLowerCase(), Maps.newHashMap());
            YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
            for (String key : yamlConfiguration.getKeys(true)) {
                System.out.println("key: " + key);
                System.out.println("content: " + yamlConfiguration.getString(key));
                locales.put(key, yamlConfiguration.getString(key));
            }
            this.localesCache.put(localeName.toLowerCase(), locales);
        }
    }

    @Override
    public void sendMessage(Player player, String locale, String messageKey, Placeholder... placeholders) {
        player.sendMessage(getMessage(locale, messageKey, placeholders));
    }

    @Override
    public String getMessage(String locale, String messageKey, Placeholder... placeholders) {
        if (!this.localesCache.containsKey(locale.toLowerCase())) {
            System.out.println("Locale file for " + locale + " not found.");
            return messageKey;
        }
        Map<String, String> localesCache = this.localesCache.get(locale);
        if (!localesCache.containsKey(messageKey)) {
            System.out.println("Locale key " + messageKey + " for " + locale + " not found.");
            return messageKey;
        }

        String message = localesCache.get(messageKey);

        for (Placeholder placeholder : placeholders) {
            message = message.replace(placeholder.getTarget(), String.valueOf(placeholder.getReplacement()));
        }

        return message;
    }

    @Override
    public void sendMessage(Player player, String locale, String messageKey) {
        sendMessage(player, locale, messageKey, new Placeholder[0]);
    }

    @Override
    public String getMessage(String locale, String messageKey) {
        return getMessage(locale, messageKey, new Placeholder[0]);
    }

    @Override
    public void reloadMessages() {
        this.localesCache.clear();

        loadLocales();
    }
}
