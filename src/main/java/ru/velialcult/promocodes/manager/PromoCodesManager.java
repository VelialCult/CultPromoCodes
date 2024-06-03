package ru.velialcult.promocodes.manager;

import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.Nullable;
import ru.velialcult.library.bukkit.file.FileRepository;
import ru.velialcult.library.bukkit.utils.ConfigurationUtil;
import ru.velialcult.promocodes.CultPromoCodes;
import ru.velialcult.promocodes.promocode.PromoCode;
import ru.velialcult.promocodes.storage.PromoCodesCache;
import ru.velialcult.promocodes.storage.PromoCodesStorage;
import ru.velialcult.promocodes.storage.database.PromoCodesDataBase;

import java.time.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Written by Nilsson
 * 02.06.2024
 */
public class PromoCodesManager {

    private final CultPromoCodes cultPromoCodes;
    private final PromoCodesStorage promoCodesStorage;
    private final PromoCodesCache promoCodesCache;
    private final PromoCodesDataBase promoCodesDataBase;

    public PromoCodesManager(
            CultPromoCodes cultPromoCodes,
            PromoCodesStorage promoCodesStorage,
            PromoCodesDataBase promoCodesDataBase) {
        this.cultPromoCodes = cultPromoCodes;
        this.promoCodesStorage = promoCodesStorage;
        this.promoCodesCache = promoCodesStorage.getPromoCodesCache();
        this.promoCodesDataBase = promoCodesDataBase;
    }

    public boolean hasUsages(String promoCodeKey) {
        PromoCode promoCode = getPromoCodeByKey(promoCodeKey);
        if (promoCode != null) {
            return getTotalPromoCodeUsages(promoCodeKey) < promoCode.getMaxUsages();
        }
        return false;
    }

    public long getTotalPromoCodeUsages(String promoCodeKey) {
        int totalUsages = 0;
        for (Map.Entry<UUID, Map<String, Integer>> entry : promoCodesCache.getPromoCodesUsage().entrySet()) {
            totalUsages += entry.getValue().getOrDefault(promoCodeKey, 0);
        }
        return totalUsages;
    }

    public int getPromoCodeUsages(UUID player, String promoCodeKey) {
        Map<String, Integer> countUsages = promoCodesCache.getPromoCodesUsage().getOrDefault(player, new HashMap<>());
        return countUsages.getOrDefault(promoCodeKey, 0);
    }

    public long getTimeToUsePromoCode(UUID player, String promoCodeKey) {
        Map<String, Long> promoCodesCooldown = promoCodesCache.getTimeUsagePromoCodeMap().getOrDefault(player, new HashMap<>());
        PromoCode promoCode = getPromoCodeByKey(promoCodeKey);
        if (promoCode != null) {
            long timeUsage = promoCodesCooldown.getOrDefault(promoCodeKey, 0L);
            ZonedDateTime timeToGetPromo = Instant.ofEpochMilli(timeUsage).plusSeconds(promoCode.getCooldownInSeconds())
                    .atZone(ZoneId.of("Europe/Moscow"));
            ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Europe/Moscow"));
            return Duration.between(now, timeToGetPromo).toSeconds();
        }
        return 0;
    }

    public boolean hasUsages(UUID player, String promoCodeKey) {
        PromoCode promoCode = getPromoCodeByKey(promoCodeKey);
        if (promoCode != null) {
            int usages = getPromoCodeUsages(player, promoCodeKey);
            return usages < promoCode.getMaxUsagesPerPlayer();
        }
        return false;
    }

    public boolean cooldownIsExpire(UUID player, String promoCodeKey) {
        Map<String, Long> promoCodesCooldown = promoCodesCache.getTimeUsagePromoCodeMap().getOrDefault(player, new HashMap<>());
        PromoCode promoCode = getPromoCodeByKey(promoCodeKey);
        if (promoCode != null) {
            long timeUsage = promoCodesCooldown.getOrDefault(promoCodeKey, 0L);
            ZonedDateTime timeToGetPromo = Instant.ofEpochMilli(timeUsage).plusSeconds(promoCode.getCooldownInSeconds())
                    .atZone(ZoneId.of("Europe/Moscow"));
            ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Europe/Moscow"));
            return Duration.between(now, timeToGetPromo).isNegative();
        }
        return false;
    }

    public void deletePromoCodeData(String promCodeKey) {
        promoCodesDataBase.delete(promCodeKey);

        FileConfiguration config = FileRepository.getByName(cultPromoCodes, "promocodes.yml").getConfiguration();
        config.set("promocodes." + promCodeKey, null);
        ConfigurationUtil.saveFile(config, cultPromoCodes.getDataFolder().getAbsolutePath(), "promocodes.yml");

        PromoCode promoCode = getPromoCodeByKey(promCodeKey);
        if (promoCode != null) {
            promoCodesStorage.getPromoCodes().remove(promoCode);
            promoCodesCache.remove(promCodeKey);
        }
    }

    @Nullable
    public PromoCode getPromoCodeByKey(String promoCodeKey) {
        return promoCodesStorage.getPromoCodes()
                .stream()
                .filter(promoCode -> promoCode.getKey().equalsIgnoreCase(promoCodeKey))
                .findAny()
                .orElse(null);
    }

    public long getTimeToDelete(String promoCodeKey) {
        PromoCode promoCode = getPromoCodeByKey(promoCodeKey);
        if (promoCode != null) {
            if (promoCode.isTimed()) {
                ZonedDateTime zonedDateTime = Instant.ofEpochMilli(promoCodesCache.getPromoCodesCreateTime().getOrDefault(promoCodeKey, 0L))
                        .plusSeconds(promoCode.getTimeToDeleteCode())
                        .atZone(ZoneId.of("Europe/Moscow"));
                ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Europe/Moscow"));
                return Duration.between(now, zonedDateTime).toSeconds();
            }
        }
        return 0;
    }

    public boolean promoCodeIsExpire(String promoCodeKey) {
        PromoCode promoCode = getPromoCodeByKey(promoCodeKey);
        if (promoCode != null) {
            if (promoCode.isTimed()) {
                ZonedDateTime zonedDateTime = Instant.ofEpochMilli(promoCodesCache.getPromoCodesCreateTime().getOrDefault(promoCodeKey, 0L))
                        .plusSeconds(promoCode.getTimeToDeleteCode())
                        .atZone(ZoneId.of("Europe/Moscow"));
                ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Europe/Moscow"));
                return Duration.between(now, zonedDateTime).isNegative();
            }
        }
        return false;
    }
}
