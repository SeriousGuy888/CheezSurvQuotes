package io.github.seriousguy888.cheezsurvquotes;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class ToggleQuotesCommand implements CommandExecutor {
  CheezSurvQuotes plugin;

  ToggleQuotesCommand(CheezSurvQuotes plugin) {
    this.plugin = plugin;
  }

  @Override
  public boolean onCommand(@Nonnull CommandSender sender,
                           @Nonnull Command command,
                           @Nonnull String commandLabel,
                           @Nonnull String[] args) {
    if(!(sender instanceof Player)) {
      sender.sendMessage("The console cannot use this command!");
      return true;
    }

    Player player = (Player) sender;
    Boolean enabled = plugin.quotesEnabled.get(player);

    player.sendMessage(ChatColor.YELLOW + (enabled ? "Disabled" : "Enabled") + " showing quotes.");
    plugin.quotesEnabled.put(player, !enabled);

    return true;
  }
}