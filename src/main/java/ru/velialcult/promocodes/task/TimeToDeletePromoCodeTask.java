package ru.velialcult.promocodes.task;

import org.bukkit.scheduler.BukkitRunnable;
import ru.velialcult.promocodes.manager.PromoCodesManager;
import ru.velialcult.promocodes.promocode.PromoCode;
import ru.velialcult.promocodes.storage.PromoCodesStorage;

import java.util.Iterator;

/**
 * Written by Nilsson
 * 02.06.2024
 */
public class TimeToDeletePromoCodeTask extends BukkitRunnable {

    private final PromoCodesStorage promoCodesStorage;
    private final PromoCodesManager promoCodesManager;

    public TimeToDeletePromoCodeTask(PromoCodesStorage promoCodesStorage,
                                     PromoCodesManager promoCodesManager) {
        this.promoCodesStorage = promoCodesStorage;
        this.promoCodesManager = promoCodesManager;
    }

    @Override
    public void run() {
        Iterator<PromoCode> promoCodeIterator = promoCodesStorage.getPromoCodes().iterator();
        if (promoCodeIterator.hasNext()) {
            PromoCode promoCode = promoCodeIterator.next();
            if (promoCode.isTimed()) {
                String promoCodeKey = promoCode.getKey();
                if (promoCodesManager.promoCodeIsExpire(promoCodeKey)) {
                    promoCodesManager.deletePromoCodeData(promoCodeKey);
                }
            }
        }
    }
}
