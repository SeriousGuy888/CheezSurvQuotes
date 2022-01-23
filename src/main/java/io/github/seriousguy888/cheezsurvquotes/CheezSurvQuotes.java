package io.github.seriousguy888.cheezsurvquotes;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public final class CheezSurvQuotes extends JavaPlugin {
  FileConfiguration config = getConfig();
  List<String> quotesList;

  public HashMap<Player, Boolean> quotesEnabled;
  public DataManager dataManager;


  @Override
  public void onEnable() {
    config.options().copyDefaults();
    saveDefaultConfig();

    quotesEnabled = new HashMap<>();
    dataManager = new DataManager(this);
    dataManager.loadPlayerData();

    getQuotes();
    if(!getServer().getPluginManager().isPluginEnabled(this))
      return;

    Bukkit.getPluginManager().registerEvents(dataManager, this);
    Objects.requireNonNull(getCommand("togglequotes"))
        .setExecutor(new ToggleQuotesCommand(this));
  }

  @Override
  public void onDisable() {
    dataManager.savePlayerData();
  }

  private void getQuotes() {
    int interval = config.getInt("interval");
    String urlString = config.getString("quotes.source_url");
    String delimiter = config.getString("quotes.delimiter");
    if(urlString == null || delimiter == null) {
      getLogger().severe("Missing or invalid config options!");
      getServer().getPluginManager().disablePlugin(this);
      return;
    }

    URI uri;
    try {
      uri = URI.create(urlString);
    } catch (IllegalArgumentException e) {
      getLogger().severe("Could not load quotes because of invalid URL.\n" + e);
      getServer().getPluginManager().disablePlugin(this);
      return;
    }


    HttpClient client = HttpClient.newBuilder()
        .followRedirects(HttpClient.Redirect.NORMAL)
        .connectTimeout(Duration.ofSeconds(20))
        .build();
    HttpRequest request = HttpRequest.newBuilder()
        .uri(uri)
        .GET()
        .build();

    try {
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

      if(response.statusCode() != 200) {
        getLogger().severe("Failed to load quotes. HTTP request response code was " +
            response.statusCode() + " and not 200.");
        getServer().getPluginManager().disablePlugin(this);
        return;
      }

      String body = response.body();
      quotesList = Arrays.asList((body.split(delimiter)));
      new ShowQuoteTask(this).runTaskTimer(this, 0L, interval * 20L);

    } catch (IOException | InterruptedException e) {
      getLogger().severe("Failed to load quotes because HTTP request failed. Make sure that the source URL is set correctly in the plugin config. " + e);
      getServer().getPluginManager().disablePlugin(this);
    }
  }
}
