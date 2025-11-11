package plugily.projects.murdermystery.commands.arguments.game;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import plugily.projects.minigamesbox.api.user.IUser;
import plugily.projects.minigamesbox.classic.commands.arguments.data.CommandArgument;
import plugily.projects.minigamesbox.classic.commands.arguments.data.LabelData;
import plugily.projects.minigamesbox.classic.commands.arguments.data.LabeledCommandArgument;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.murdermystery.Main;
import plugily.projects.murdermystery.commands.arguments.ArgumentsRegistry;
import plugily.projects.murdermystery.handlers.skins.sword.SwordSkin;

import java.util.List;
import java.util.stream.Collectors;

public class SwordSkinsArgument {

  public SwordSkinsArgument(ArgumentsRegistry registry) {
    registry.mapArgument("murdermystery", new LabeledCommandArgument("skins", "murdermystery.skins.use", CommandArgument.ExecutorType.PLAYER,
      new LabelData("/mm skins sword <skin_name>", "/mm skins sword <skin_name>", "&7Switch your Murder Mystery sword skin\n&6Permission: &7murdermystery.skins.use")) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        Main plugin = (Main) registry.getPlugin();

        if (args.length < 2) {
          // Display help message
          player.sendMessage(ChatColor.RED + "Usage: /mm skins sword <skin_name>");
          player.sendMessage(ChatColor.YELLOW + "Available Skins:");

          List<SwordSkin> availableSkins = plugin.getSwordSkinManager().getRegisteredSwordSkins().stream()
            .filter(skin -> !skin.hasPermission() || player.hasPermission(skin.getPermission()))
            .collect(Collectors.toList());

          for (SwordSkin skin : availableSkins) {
            String skinName = plugin.getSwordSkinManager().getSkinNameByItemStack(skin.getItemStack());
            if (skinName != null) {
              player.sendMessage(ChatColor.GREEN + "- " + skinName);
            }
          }
          return;
        }

        if (!args[1].equalsIgnoreCase("sword")) {
          player.sendMessage(ChatColor.RED + "Only sword skins are currently supported! Usage: /mm skins sword <skin_name>");
          return;
        }

        if (args.length < 3) {
          player.sendMessage(ChatColor.RED + "Please specify a skin name! Usage: /mm skins sword <skin_name>");
          return;
        }

        String skinName = args[2];

        SwordSkin selectedSkin = plugin.getSwordSkinManager().getSkinByName(skinName);
        if (selectedSkin == null) {
          player.sendMessage(new MessageBuilder("COMMANDS_SWORD_SKINS_SKIN_NOT_FOUND").asKey().value(skinName).build());
          return;
        }

        if (selectedSkin.hasPermission() && !player.hasPermission(selectedSkin.getPermission())) {
          player.sendMessage(new MessageBuilder("COMMANDS_SWORD_SKINS_NO_PERMISSION").asKey().value(skinName).build());
          return;
        }

        IUser user = plugin.getUserManager().getUser(player);
        user.setStatistic("SELECTED_SWORD_SKIN", skinName.hashCode());

        player.sendMessage(new MessageBuilder("COMMANDS_SWORD_SKINS_SKIN_SELECTED").asKey().value(skinName).build());
      }
    });
  }
}