package ru.velialcult.promocodes.storage;

import org.bukkit.configuration.file.FileConfiguration;
import ru.velialcult.library.bukkit.file.FileRepository;
import ru.velialcult.promocodes.CultPromoCodes;
import ru.velialcult.promocodes.loader.PromoCodesLoader;
import ru.velialcult.promocodes.promocode.PromoCode;
import ru.velialcult.promocodes.saver.PromoCodesSaver;
import ru.velialcult.promocodes.storage.database.PromoCodesDataBase;

import java.util.ArrayList;
import java.util.List;

/**
 * Written by Nilsson
 * 01.06.2024
 */
public class PromoCodesStorage {

    private final List<PromoCode> promoCodes;
    private final PromoCodesSaver promoCodesSaver;
    private final PromoCodesCache promoCodesCache;
    private final PromoCodesDataBase promoCodesDataBase;

    public PromoCodesStorage(CultPromoCodes cultPromoCodes, PromoCodesDataBase promoCodesDataBase) {
        this.promoCodes = new ArrayList<>();
        FileConfiguration config = FileRepository.getByName(cultPromoCodes, "promocodes.yml").getConfiguration();
        PromoCodesLoader promoCodesLoader = new PromoCodesLoader(config);
        this.promoCodes.addAll(promoCodesLoader.loadPromoCodes());
        this.promoCodesSaver = new PromoCodesSaver(config);
        this.promoCodesCache = new PromoCodesCache(promoCodesDataBase, this);
        this.promoCodesDataBase = promoCodesDataBase;
    }

    public void save() {
        promoCodesSaver.save(promoCodes);
        promoCodesDataBase.save(promoCodesCache.getPromoCodesCreateTime(),
                                promoCodesCache.getTimeUsagePromoCodeMap(),
                                promoCodesCache.getPromoCodesUsage());
    }

    public List<PromoCode> getPromoCodes() {
        return promoCodes;
    }

    public PromoCodesCache getPromoCodesCache() {
        return promoCodesCache;
    }
}
