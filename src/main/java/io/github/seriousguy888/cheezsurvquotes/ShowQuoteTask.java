package io.github.seriousguy888.cheezsurvquotes;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Random;

public class ShowQuoteTask extends BukkitRunnable {
  CheezSurvQuotes plugin;
  public ShowQuoteTask(CheezSurvQuotes plugin) {
    this.plugin = plugin;
  }

  @Override
  public void run() {
    Random random = new Random();

    List<String> quotesList = plugin.quotesList;
    String chosenQuote = quotesList.get(random.nextInt(quotesList.size()));

    for(Player player : Bukkit.getOnlinePlayers()) {
      if(!plugin.quotesEnabled.get(player))
        continue;
      player.spigot().sendMessage(
          ChatMessageType.ACTION_BAR,
          TextComponent.fromLegacyText(chosenQuote));
    }
  }
}
