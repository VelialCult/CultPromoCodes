package ru.velialcult.promocodes.storage;

import ru.velialcult.promocodes.storage.database.PromoCodesDataBase;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/**
 * Written by Nilsson
 * 01.06.2024
 */
public class PromoCodesCache {

    private final Map<String, Long> promoCodesCreateTime;
    private final Map<UUID, Map<String, Long>> timeUsagePromoCodeMap;
    private final Map<UUID, Map<String, Integer>> promoCodesUsage;

    public PromoCodesCache(PromoCodesDataBase promoCodesDataBase,
                           PromoCodesStorage promoCodesStorage) {
        this.promoCodesCreateTime = promoCodesDataBase.loadTimesCreatePromoCodes();
        this.timeUsagePromoCodeMap = promoCodesDataBase.loadTimesUsagePromoCodes();
        this.promoCodesUsage = promoCodesDataBase.loadUsagePromoCodes();
        cacheNewTimedPromoCodes(promoCodesStorage);
    }

    public void cacheNewTimedPromoCodes(PromoCodesStorage promoCodesStorage) {
        promoCodesStorage.getPromoCodes().forEach(promoCode -> {
            if (promoCode.isTimed() && !promoCodesCreateTime.containsKey(promoCode.getKey())) {
                promoCodesCreateTime.put(promoCode.getKey(), System.currentTimeMillis());
            }
        });
    }

    public void add(UUID uuid, String promoCodeKey) {
        Map<String, Long> promoCodeTimeUsages = timeUsagePromoCodeMap.getOrDefault(uuid, new HashMap<>());
        promoCodeTimeUsages.put(promoCodeKey, System.currentTimeMillis());
        this.timeUsagePromoCodeMap.put(uuid, promoCodeTimeUsages);
        Map<String, Integer> promoCodeUsages = promoCodesUsage.getOrDefault(uuid, new HashMap<>());
        promoCodeUsages.compute(promoCodeKey, (key, count) -> (count == null) ? 1 : count + 1);
        this.promoCodesUsage.put(uuid, promoCodeUsages);
    }

    public void remove(String promoCodeKey) {
        promoCodesCreateTime.remove(promoCodeKey);

        timeUsagePromoCodeMap.forEach((player, map) -> {
            map.entrySet().removeIf(entry -> entry.getKey().equalsIgnoreCase(promoCodeKey));
        });
        promoCodesUsage.forEach((player, map) -> {
            map.entrySet().removeIf(entry -> entry.getKey().equalsIgnoreCase(promoCodeKey));
        });
    }

    public Map<UUID, Map<String, Integer>> getPromoCodesUsage() {
        return promoCodesUsage;
    }

    public Map<UUID, Map<String, Long>> getTimeUsagePromoCodeMap() {
        return timeUsagePromoCodeMap;
    }

    public Map<String, Long> getPromoCodesCreateTime() {
        return promoCodesCreateTime;
    }
}
