package discordbot.discordbot;

import net.dv8tion.jda.bot.sharding.DefaultShardManager;
import net.dv8tion.jda.bot.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import org.bukkit.plugin.java.JavaPlugin;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;


public class DiscordBot extends JavaPlugin {

    public JDA jda;
    static DiscordBot plugin;

    FileConfiguration playerData;
    File data;
    private final String TOKEN = "NTE5MzM5MzQwNzk5OTM0NDY0.Duk5Hg.TVLoSHaICPelDwp26V0bP8sfKhc";

    public DiscordBot() {
        plugin = this;
    }

    public static DiscordBot getPlugin() {
        return plugin;
    }

    public JDA getJda() {
        return jda;
    }


    @Override
    public void onEnable() {
        createConfig();
        startBot();
        getCommand("dverify").setExecutor(new Events());
        getServer().getPluginManager().registerEvents(new Events(), this);
        jda.addEventListener(new Events());
    }

    private void startBot() {

        try {

            jda = new JDABuilder(AccountType.BOT).setToken(TOKEN).build();
            jda.getPresence().setGame(Game.watching("Lanthyr Discord"));
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
