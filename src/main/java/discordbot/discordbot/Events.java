package discordbot.discordbot;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class Events extends ListenerAdapter implements CommandExecutor, Listener {

    private HashMap<UUID, String> uuidCode = new HashMap<>();
    private HashMap<UUID, String> discordMemberID = new HashMap<>();
    private List<UUID> verifiedMembers = new ArrayList<>();
    public Guild guild;

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        String[] args = event.getMessage().getContentRaw().split(" "); //!link args
        Player target = Bukkit.getPlayer(args[1]);
        String randomCode = new Random().nextInt(800000)+200000+"AA"; //999999AA

        if (event.getAuthor().isBot() || event.getAuthor().isFake() || event.isWebhookMessage()) return;

        if (args[0].equalsIgnoreCase("!link")) { //!link <player>
            if (event.getMember().getRoles().stream().filter(role ->
                    role.getName().equalsIgnoreCase("Verified")).findAny().orElse(null) != null) {
                event.getChannel().sendMessage( event.getAuthor().getAsMention() + "You are already verified!").queue();
                return;
            }

            if (args.length != 2) {
                event.getChannel().sendMessage("**!** Error! You need to specify a player who is online.").queue();
                return;
            }
            if (target == null) {
                event.getChannel().sendMessage("**!** Error! That player is not online.").queue();
                return;
            }

            uuidCode.put(target.getUniqueId(), randomCode);
            uuidCode.put(target.getUniqueId(), randomCode);
            discordMemberID.put(target.getUniqueId(), event.getAuthor().getId());
            event.getAuthor().openPrivateChannel().complete().sendMessage("**!** Here is your code: " + randomCode).queue();
            event.getAuthor().openPrivateChannel().complete().sendMessage(uuidCode.get(target.getUniqueId())).queue();


        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (DiscordBot.getPlugin().playerData.contains("Data." + event.getPlayer().getUniqueId().toString())) {
            verifiedMembers.add(event.getPlayer().getUniqueId());
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {


       Bukkit.getScheduler().runTaskLater(DiscordBot.getPlugin(), ()-> guild = DiscordBot.getJda().getGuilds().get(0), 100L);


        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        if (command.getName().equalsIgnoreCase("verify")) {

            Player player = (Player) sender;
            if (args.length != 1) {
                player.sendMessage(ChatColor.RED + "Wrong usage, /dverify <code>");
                return true;
            }


            if (!uuidCode.containsKey(player.getUniqueId())) {
                player.sendMessage(ChatColor.RED + "You do not have a verification code, please see the #verify channel on discord.");
                return true;
            }

            if (DiscordBot.getPlugin().playerData.contains("Data." + player.getUniqueId().toString())) {
                player.sendMessage(ChatColor.RED + "You are already verified.");
                return true;
            }

            String actualCode = uuidCode.get(player.getUniqueId());

            if (!actualCode.equalsIgnoreCase(args[0])) {
                player.sendMessage(ChatColor.RED + "Wrong code, please try again.");
                return true;
            }

            String discordID = discordMemberID.get(player.getUniqueId());
            Member target = guild.getMemberById(discordID);
            if (target == null) {
                player.sendMessage(ChatColor.RED + "Couldn't find player.");
                uuidCode.remove(player.getUniqueId());
                discordMemberID.remove(player.getUniqueId());
                return true;
            }

            if (DiscordBot.getPlugin().playerData != null ) {
                DiscordBot.getPlugin().playerData.set("Data." + player.getUniqueId(), discordID);
            }

            try {
                DiscordBot.getPlugin().playerData.save(DiscordBot.getPlugin().data);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Role verifiedRole = guild.getRolesByName("Verified", true).get(0);
            //Adds role to player.
            uuidCode.remove(player.getUniqueId());
            discordMemberID.remove(player.getUniqueId());

            verifiedMembers.add(player.getUniqueId());
            target.getUser().openPrivateChannel().complete().sendMessage(":white_check_mark: You have been verified!").queue();
            guild.getController().addSingleRoleToMember(target, verifiedRole).queue();
            player.sendMessage(ChatColor.GREEN + "You have been verified, please make sure you have the 'Verified' role on discord.");
            player.sendMessage(ChatColor.AQUA + target.getUser().getDiscriminator());
            return true;
        }
        return true;
    }
}
