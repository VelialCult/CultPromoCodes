package ru.velialcult.promocodes.update;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bukkit.ChatColor;
import ru.velialcult.promocodes.CultPromoCodes;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * Written by Nilsson
 * 03.06.2024
 */
public class UpdateChecker {

    private static final String GITHUB_API_LATEST_RELEASE = "https://api.github.com/repos/VelialCult/CultPromoCodes/releases/latest";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final CultPromoCodes cultPromoCodes;

    public UpdateChecker(CultPromoCodes cultPromoCodes) {
        this.cultPromoCodes = cultPromoCodes;
    }

    public void check() {

        String gitHubVersion = getLatestVersionFromGitHub();
        String pluginYmlVersion = cultPromoCodes.getDescription().getVersion();

        if (gitHubVersion != null) {
            if (gitHubVersion.equals(pluginYmlVersion)) {
                cultPromoCodes.getLogger().info("Используется последняя версия плагина");
            } else {
                cultPromoCodes.getLogger().warning("Ваша версия " + pluginYmlVersion + " устарела. Доступна новая версия: " + ChatColor.YELLOW + gitHubVersion);
                cultPromoCodes.getLogger().warning("Пожалуйста, скачайте её - https://github.com/VelialCult/CultPromoCodes/releases/latest");
            }
        }
    }

    private String getLatestVersionFromGitHub() {
        try {
            URL url = new URL(GITHUB_API_LATEST_RELEASE);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            try (Scanner scanner = new Scanner(new InputStreamReader(connection.getInputStream()))) {
                String jsonResponse = scanner.useDelimiter("\\A").next();
                JsonNode rootNode = objectMapper.readTree(jsonResponse);
                return rootNode.get("tag_name").asText();
            }
        } catch (Exception e) {
            cultPromoCodes.getLogger().severe("Не удалось получить последнюю версию с GitHub: " + e.getMessage());
        }
        return null;
    }
}
