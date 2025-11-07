package petterim1.lightchat;

import cn.nukkit.Player;
import cn.nukkit.utils.Config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Settings {

    static final int PROVIDER_LUCKPERMS = 1;
    static final int PROVIDER_MULTIPASS = 2;

    private final Main plugin;

    int provider;

    Map<String, String> format;
    Map<String, String> nameFormat;

    boolean antiSpam;
    int antiSpamThreshold;
    String antispamMessage;
    boolean limitUnicode;
    String replaceUnicode;
    boolean duplicatedMessageCheck;
    int duplicatedMessageThreshold;
    String duplicatedMessageMessage;
    int maxMessageLength;
    String messageTooLongMessage;
    boolean cleanMessages;
    List<String> filter;
    String replaceFilter;
    List<String> blacklist;
    String blacklistMessage;
    String defaultFormat;
    boolean formatDisplayName;
    String defaultNameFormat;

    Settings(Main plugin) {
        this.plugin = plugin;
        plugin.saveDefaultConfig();
        loadConfig();
        checkProvider();
    }

    @SuppressWarnings("unchecked")
    private void loadConfig() {
        Config cfg = plugin.getConfig();
        int current = 2;
        int ver = cfg.getInt("configVersion");

        if (ver != current) {
            if (ver < 2) {
                cfg.set("formatDisplayName", false);
                cfg.set("defaultNameFormat", "%username%");
                cfg.set("nameFormat", new HashMap<String, String>());
            }
            cfg.set("configVersion", current);
            cfg.save();
            plugin.getLogger().notice("Config file updated");
        }

        try {
            antiSpam = cfg.getBoolean("antiSpam");
            antiSpamThreshold = cfg.getInt("antiSpamThreshold");
            antispamMessage = cfg.getString("antispamMessage");
            limitUnicode = cfg.getBoolean("limitUnicode");
            replaceUnicode = cfg.getString("replaceUnicode");
            duplicatedMessageCheck = cfg.getBoolean("duplicatedMessageCheck");
            duplicatedMessageThreshold = cfg.getInt("duplicatedMessageThreshold");
            duplicatedMessageMessage = cfg.getString("duplicatedMessageMessage");
            maxMessageLength = cfg.getInt("maxMessageLength");
            messageTooLongMessage = cfg.getString("messageTooLongMessage");
            cleanMessages = cfg.getBoolean("cleanMessages");
            filter = cfg.getStringList("filter");
            replaceFilter = cfg.getString("replaceFilter");
            blacklist = cfg.getStringList("blacklist");
            blacklistMessage = cfg.getString("blacklistMessage");
            defaultFormat = cfg.getString("defaultFormat");

            // 🔧 Asegurar que format nunca sea null
            Object rawFormat = cfg.get("format");
            if (rawFormat instanceof Map) {
                format = (Map<String, String>) rawFormat;
            } else {
                format = new HashMap<>();
            }
            format.putIfAbsent("default", defaultFormat);

            formatDisplayName = cfg.getBoolean("formatDisplayName");
            defaultNameFormat = cfg.getString("defaultNameFormat");

            // 🔧 Asegurar que nameFormat nunca sea null
            Object rawNameFormat = cfg.get("nameFormat");
            if (rawNameFormat instanceof Map) {
                nameFormat = (Map<String, String>) rawNameFormat;
            } else {
                nameFormat = new HashMap<>();
            }
            nameFormat.putIfAbsent("default", defaultNameFormat);

        } catch (Exception ex) {
            plugin.getLogger().error("Config error! Please fix your config", ex);
        }
    }

    private void checkProvider() {
        if (plugin.getServer().getPluginManager().getPlugin("LuckPerms") != null) {
            provider = PROVIDER_LUCKPERMS;
            plugin.getLogger().info("Set LuckPerms as group info provider");
        } else if (plugin.getServer().getPluginManager().getPlugin("Multipass") != null) {
            provider = PROVIDER_MULTIPASS;
            plugin.getLogger().info("Set Multipass as group info provider");
        } else {
            plugin.getLogger().error("No supported permission plugins found! Per group formatting won't be enabled");
        }
    }

    void reload() {
        plugin.getConfig().reload();
        loadConfig();
        plugin.events.groupCache.clear();
        for (Player player : plugin.getServer().getOnlinePlayers().values()) {
            plugin.api.initPlayer(player);
        }
        plugin.getLogger().notice("Reload completed");
    }
}
