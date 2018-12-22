package discordbot.discordbot;

import discordbot.discordbot.Events.AuthenticationEvent;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import org.bukkit.plugin.java.JavaPlugin;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;


public class DiscordBot extends JavaPlugin {

    private static JDA jda;
    private static DiscordBot plugin;

    public FileConfiguration playerData;
    public File data;
    private final String TOKEN = "redacted";

    public DiscordBot() {
        plugin = this;
    }

    public static DiscordBot getPlugin() {
        return plugin;
    }

    public static JDA getJda() {
        return jda;
    }

    @Override
    public void onEnable() {
        createConfig();
        startBot();
        getCommand("discordverify").setExecutor(new AuthenticationEvent());
        getServer().getPluginManager().registerEvents(new AuthenticationEvent(), this);
        jda.addEventListener(new AuthenticationEvent());
    }

    private void startBot() {
        try {
            jda = new JDABuilder(AccountType.BOT).setToken(TOKEN).build();
        } catch (LoginException e) {
            e.printStackTrace();
        }
    }

    private void createConfig() {
       data = new File(getDataFolder() + File.separator + "data.yml");
       if (!data.exists()) {
           getServer().getConsoleSender().sendMessage(ChatColor.RED + "Creating data file for discord bot.");
           this.saveResource("data.yml", false);
       }
       playerData = new YamlConfiguration();
       try {
           playerData.load(data);
       } catch (InvalidConfigurationException | IOException e) {
           e.printStackTrace();
       }

    }

}
