package ru.velialcult.promocodes.loader;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import ru.velialcult.library.java.utils.TimeUtil;
import ru.velialcult.promocodes.promocode.PromoCode;

import java.util.ArrayList;
import java.util.List;

/**
 * Written by Nilsson
 * 01.06.2024
 */
public class PromoCodesLoader {

    private final FileConfiguration config;

    public PromoCodesLoader(FileConfiguration config) {
        this.config = config;
    }

    public List<PromoCode> loadPromoCodes() {
        ConfigurationSection configurationSection = config.getConfigurationSection("promocodes");
        List<PromoCode> promoCodes = new ArrayList<>();
        if (configurationSection != null) {
            for (String key : configurationSection.getKeys(false)) {
                List<String> commands = config.getStringList("promocodes." + key + ".commands");
                List<String> message = config.getStringList("promocodes." + key + ".message");
                int maxUsages = config.getInt("promocodes." + key + ".maxUsages", 0);
                int maxUsagesPerPlayer = config.getInt("promocodes." + key + ".maxUsagesPerPlayer", 0);
                long cooldownTime = TimeUtil.parseStringToTime(config.getString("promocodes." + key + ".cooldown", "1s"));
                boolean timed = config.getBoolean("promocodes." + key + ".timed", false);
                long timeToDeletePromoCode = TimeUtil.parseStringToTime(config.getString("promocodes." + key + ".timeToDeletePromoCode", "1H"));
                promoCodes.add(new PromoCode(key, commands, message, maxUsages, maxUsagesPerPlayer, cooldownTime, timed, timeToDeletePromoCode));
            }
        }
        return promoCodes;
    }
}
