package ru.velialcult.promocodes.saver;

import org.bukkit.configuration.file.FileConfiguration;
import ru.velialcult.library.bukkit.utils.ConfigurationUtil;
import ru.velialcult.library.java.utils.TimeUtil;
import ru.velialcult.promocodes.CultPromoCodes;
import ru.velialcult.promocodes.promocode.PromoCode;

import java.util.List;

/**
 * Written by Nilsson
 * 01.06.2024
 */
public class PromoCodesSaver {

    private final FileConfiguration config;

    public PromoCodesSaver(FileConfiguration config) {
        this.config = config;
    }

    public void save(List<PromoCode> promoCodeList) {
        for (PromoCode promoCode : promoCodeList) {
            config.set("promocodes." + promoCode.getKey() + ".commands", promoCode.getCommands());
            config.set("promocodes." + promoCode.getKey() + ".message", promoCode.getMessage());
            config.set("promocodes." + promoCode.getKey() + ".maxUsages", promoCode.getMaxUsages());
            config.set("promocodes." + promoCode.getKey() + ".maxUsagesPerPlayer", promoCode.getMaxUsagesPerPlayer());
            config.set("promocodes." + promoCode.getKey() + ".cooldown", TimeUtil.parseTimeToString(promoCode.getCooldownInSeconds()));
            config.set("promocodes." + promoCode.getKey() + ".timed", promoCode.isTimed());
            config.set("promocodes." + promoCode.getKey() + ".timeToDeletePromoCode", TimeUtil.parseTimeToString(promoCode.getTimeToDeleteCode()));
        }

        ConfigurationUtil.saveFile(config, CultPromoCodes.getInstance().getDataFolder().getAbsolutePath(), "promocodes.yml");
    }
}
