package io.github.seriousguy888.cheezsurvquotes;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

public final class CheezSurvQuotes extends JavaPlugin {
  public static CheezSurvQuotes plugin;

  FileConfiguration config = getConfig();
  List<String> quotesList;


  @Override
  public void onEnable() {
    plugin = this;

    config.options().copyDefaults();
    saveDefaultConfig();

    getQuotes();
  }

  private void getQuotes() {
    int interval = config.getInt("interval");
    String urlString = config.getString("quotes.source_url");
    String delimiter = config.getString("quotes.delimiter");
    if(urlString == null || delimiter == null) {
      getLogger().warning("Missing or invalid config options!");
      getServer().getPluginManager().disablePlugin(this);
      return;
    }

    URI uri;
    try {
      uri = URI.create(urlString);
    } catch (IllegalArgumentException e) {
      getLogger().warning("Could not load quotes because of invalid URL.\n" + e);
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
    client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
        .thenAccept(response -> {
          if(response.statusCode() != 200) {
            getLogger().warning("Failed to load quotes. HTTP request response code was " + response.statusCode() + " and not 200.");
            getServer().getPluginManager().disablePlugin(this);
            return;
          }

          String body = response.body();
          quotesList = Arrays.asList((body.split(delimiter)));
          new ShowQuoteTask().runTaskTimer(this, 0L, interval * 20L);
        });
  }
}
