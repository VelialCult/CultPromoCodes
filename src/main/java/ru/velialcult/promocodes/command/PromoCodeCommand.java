package ru.velialcult.promocodes.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ru.velialcult.library.bukkit.utils.PlayerUtil;
import ru.velialcult.library.core.VersionAdapter;
import ru.velialcult.library.java.text.ReplaceData;
import ru.velialcult.library.java.utils.TimeUtil;
import ru.velialcult.promocodes.file.MessagesFile;
import ru.velialcult.promocodes.manager.PromoCodesManager;
import ru.velialcult.promocodes.promocode.PromoCode;
import ru.velialcult.promocodes.service.PromoCodeService;

/**
 * Written by Nilsson
 * 02.06.2024
 */
public class PromoCodeCommand implements CommandExecutor {

    private final MessagesFile messagesFile;
    private final PromoCodeService promoCodeService;
    private final PromoCodesManager promoCodesManager;

    public PromoCodeCommand(MessagesFile messagesFile,
                            PromoCodeService promoCodeService) {
        this.messagesFile = messagesFile;
        this.promoCodeService = promoCodeService;
        this.promoCodesManager = promoCodeService.getPromoCodesManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (PlayerUtil.senderIsPlayer(sender)) {
            Player player = (Player)  sender;
            if (args.length != 1) {
                VersionAdapter.MessageUtils().sendMessage(sender, messagesFile.getFileOperations().getList("messages.commands.promocode.help"));
            } else {
                String promoCodeKey = args[0];

                PromoCode promoCode = promoCodesManager.getPromoCodeByKey(promoCodeKey);
                if (promoCode == null) {
                    VersionAdapter.MessageUtils().sendMessage(sender, messagesFile.getFileOperations().getString("messages.commands.promocode.not-found"));
                    return true;
                }

                if (promoCodesManager.promoCodeIsExpire(promoCodeKey)) {
                    VersionAdapter.MessageUtils().sendMessage(sender, messagesFile.getFileOperations().getString("messages.commands.promocode.expire"));
                    return true;
                }

                if (!promoCodesManager.hasUsages(promoCodeKey)) {
                    VersionAdapter.MessageUtils().sendMessage(sender, messagesFile.getFileOperations().getString("messages.commands.promocode.max",
                                                                                                                 new ReplaceData("{max}", promoCode.getMaxUsages()))
                    );
                    return true;
                }

                if (!promoCodesManager.hasUsages(player.getUniqueId(), promoCodeKey)) {
                    VersionAdapter.MessageUtils().sendMessage(sender, messagesFile.getFileOperations().getString("messages.commands.promocode.not-usages",
                                                                                                                 new ReplaceData("{max}", promoCode.getMaxUsagesPerPlayer()),
                                                                                                                 new ReplaceData("{usages}", promoCodesManager.getPromoCodeUsages(player.getUniqueId(), promoCodeKey))
                    ));
                    return true;
                }

                if (!promoCodesManager.cooldownIsExpire(player.getUniqueId(), promoCodeKey)) {
                    VersionAdapter.MessageUtils().sendMessage(sender, messagesFile.getFileOperations().getString("messages.commands.promocode.cooldown",
                                                                                                                 new ReplaceData("{time}", TimeUtil.getTime(promoCodesManager.getTimeToUsePromoCode(player.getUniqueId(), promoCodeKey)))));
                    return true;
                }

                promoCodeService.usePromoCode(player, promoCodeKey);
                VersionAdapter.MessageUtils().sendMessage(sender, messagesFile.getFileOperations().getString("messages.commands.promocode.use",
                                                                                                             new ReplaceData("{code}", promoCodeKey)));
            }
            return true;
        }
        return false;
    }
}
