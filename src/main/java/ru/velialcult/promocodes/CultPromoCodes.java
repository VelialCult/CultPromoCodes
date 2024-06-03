package ru.velialcult.promocodes;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import ru.velialcult.library.bukkit.file.FileRepository;
import ru.velialcult.library.bukkit.utils.ConfigurationUtil;
import ru.velialcult.library.java.database.DataBase;
import ru.velialcult.library.java.database.DataBaseType;
import ru.velialcult.promocodes.command.PromoCodeCommand;
import ru.velialcult.promocodes.file.MessagesFile;
import ru.velialcult.promocodes.service.PromoCodeService;
import ru.velialcult.promocodes.update.UpdateChecker;

/**
 * Written by Nilsson
 * 01.06.2024
 */
public class CultPromoCodes extends JavaPlugin {

    private static CultPromoCodes instance;

    private DataBase dataBase;

    private MessagesFile messagesFile;

    private PromoCodeService promoCodeService;

    @Override
    public void onEnable() {
        instance = this;
        long mills = System.currentTimeMillis();

        try {

            UpdateChecker updateChecker = new UpdateChecker(this);
            updateChecker.check();

            loadConfigurations();

            String dataBaseType = getConfig().getString("settings.database.type");
            if (dataBaseType.equalsIgnoreCase("mysql")) {
                this.dataBase = new DataBase(this, DataBaseType.MySQL);
                dataBase.connect(getConfig().getString("settings.database.mysql.user"), getConfig().getString("settings.database.mysql.password"), getConfig().getString("settings.database.mysql.url"));
            } else {
                this.dataBase  = new DataBase(this, DataBaseType.SQLite);
                dataBase.connect();
            }

            promoCodeService = new PromoCodeService(this);

            Bukkit.getPluginCommand("promocode").setExecutor(new PromoCodeCommand(messagesFile, promoCodeService));

            getLogger().info("Плагин был запущен за " + ChatColor.YELLOW + (System.currentTimeMillis() - mills) + " ms");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        promoCodeService.getPromoCodesStorage().save();
        if (dataBase != null && dataBase.getConnector().isConnected()) {
            dataBase.getConnector().close();
        }
    }

    private void loadConfigurations() {
        ConfigurationUtil.loadConfigurations(this, "promocodes.yml", "messages.yml");
        FileRepository.load(this);
        this.saveDefaultConfig();
        messagesFile = new MessagesFile(this);
        messagesFile.load();
    }

    public static CultPromoCodes getInstance() {
        return instance;
    }

    public DataBase getDataBase() {
        return dataBase;
    }

    public MessagesFile getMessagesFile() {
        return messagesFile;
    }

    public PromoCodeService getPromoCodeService() {
        return promoCodeService;
    }
}
