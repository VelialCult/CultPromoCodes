package ru.velialcult.promocodes.service;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.velialcult.library.core.VersionAdapter;
import ru.velialcult.promocodes.CultPromoCodes;
import ru.velialcult.promocodes.manager.PromoCodesManager;
import ru.velialcult.promocodes.promocode.PromoCode;
import ru.velialcult.promocodes.storage.PromoCodesStorage;
import ru.velialcult.promocodes.storage.database.PromoCodesDataBase;
import ru.velialcult.promocodes.task.TimeToDeletePromoCodeTask;

/**
 * Written by Nilsson
 * 02.06.2024
 */
public class PromoCodeService {

    private final PromoCodesStorage promoCodesStorage;
    private final PromoCodesDataBase promoCodesDataBase;
    private final PromoCodesManager promoCodesManager;

    public PromoCodeService(CultPromoCodes cultPromoCodes) {
        this.promoCodesDataBase = new PromoCodesDataBase(cultPromoCodes.getDataBase());
        this.promoCodesStorage = new PromoCodesStorage(cultPromoCodes,promoCodesDataBase);
        this.promoCodesManager = new PromoCodesManager(cultPromoCodes, promoCodesStorage, promoCodesDataBase);
        TimeToDeletePromoCodeTask timeToDeletePromoCodeTask = new TimeToDeletePromoCodeTask(promoCodesStorage, promoCodesManager);
        timeToDeletePromoCodeTask.runTaskTimer(cultPromoCodes, 0L, 20L);
    }

    public void usePromoCode(Player player, String promoCodeKey) {
        PromoCode promoCode = promoCodesManager.getPromoCodeByKey(promoCodeKey);
        if (promoCode != null) {
            VersionAdapter.MessageUtils().sendMessage(player, promoCode.getMessage());
            promoCode.getCommands().forEach(string ->
                                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), string.replace("{player}", player.getName())));
            promoCodesStorage.getPromoCodesCache().add(player.getUniqueId(), promoCodeKey);
        }
    }

    public PromoCodesDataBase getPromoCodesDataBase() {
        return promoCodesDataBase;
    }

    public PromoCodesManager getPromoCodesManager() {
        return promoCodesManager;
    }

    public PromoCodesStorage getPromoCodesStorage() {
        return promoCodesStorage;
    }
}
