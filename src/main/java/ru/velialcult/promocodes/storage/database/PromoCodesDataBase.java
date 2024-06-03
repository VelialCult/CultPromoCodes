package ru.velialcult.promocodes.storage.database;

import ru.velialcult.library.java.database.Connector;
import ru.velialcult.library.java.database.DataBase;
import ru.velialcult.library.java.database.query.QuerySymbol;
import ru.velialcult.library.java.database.query.SQLQuery;
import ru.velialcult.library.java.database.table.ColumnType;
import ru.velialcult.library.java.database.table.TableColumn;
import ru.velialcult.library.java.database.table.TableConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Written by Nilsson
 * 01.06.2024
 */
public class PromoCodesDataBase {

    private final DataBase dataBase;
    private final Connector connector;

    public PromoCodesDataBase(DataBase dataBase) {
        this.dataBase = dataBase;
        this.connector = dataBase.getConnector();
        createTables();
    }

    public void delete(String promoCodeKey) {
        connector.execute(SQLQuery.deleteFrom("promocodes_data").where("key", QuerySymbol.EQUALLY, promoCodeKey), false);
        connector.execute(SQLQuery.deleteFrom("promocodes_players").where("promocode", QuerySymbol.EQUALLY, promoCodeKey), false);
    }

    public void save(Map<String, Long> promoCodesCreateTime,
                     Map<UUID, Map<String, Long>> timeUsagePromoCodeMap,
                     Map<UUID, Map<String, Integer>> promoCodesUsage) {
        promoCodesCreateTime.forEach( (promoCode, time) -> {
            connector.execute(SQLQuery.insertOrUpdate(dataBase, "promocodes_data")
                                      .set("key", promoCode)
                                      .set("timeCreate", time)
                                      .where("key", QuerySymbol.EQUALLY, promoCode),
                              false);
        });

        timeUsagePromoCodeMap.forEach((player, timeMap) -> {
            Map<String, Integer> countMap = promoCodesUsage.get(player);
            timeMap.forEach((promoCode, time) -> {
                Integer count = countMap == null ? null : countMap.get(promoCode);
                    connector.execute(
                            SQLQuery.insertOrUpdate(dataBase, "promocodes_players")
                                    .set("uuid", player.toString())
                                    .set("promocode", promoCode)
                                    .set("timeUsage", time)
                                    .set("usages", count)
                                    .where("uuid", QuerySymbol.EQUALLY, player.toString())
                                    .where("promocode", QuerySymbol.EQUALLY, promoCode),
                            false);
            });
        });
    }

    public Map<UUID, Map<String, Integer>> loadUsagePromoCodes() {
        Map<UUID, Map<String, Integer>> map = new HashMap<>();
        connector.executeQuery(SQLQuery.selectFrom("promocodes_players"),
                               rs -> {
                                   while (rs.next()) {
                                       UUID uuid = UUID.fromString(rs.getString("uuid"));
                                       String promoKey = rs.getString("promocode");
                                       int usages = rs.getInt("usages");
                                       map.computeIfAbsent(uuid, k -> new HashMap<>())
                                               .putIfAbsent(promoKey, usages);
                                   }
                                   return null;
                               }, false);
        return map;
    }

    public Map<UUID, Map<String, Long>> loadTimesUsagePromoCodes() {
        Map<UUID, Map<String, Long>> map = new HashMap<>();
        connector.executeQuery(SQLQuery.selectFrom("promocodes_players"),
                               rs -> {
                                   while (rs.next()) {
                                       UUID uuid = UUID.fromString(rs.getString("uuid"));
                                       String promoKey = rs.getString("promocode");
                                       long timeUsage = rs.getLong("timeUsage");
                                       map.computeIfAbsent(uuid, k -> new HashMap<>())
                                               .putIfAbsent(promoKey, timeUsage);
                                   }
                                   return null;
                               }, false);
        return map;
    }

    public Map<String, Long> loadTimesCreatePromoCodes() {
        Map<String, Long> map = new HashMap<>();

        connector.executeQuery(SQLQuery.selectFrom("promocodes_data"),
                               rs -> {
                                    while (rs.next()) {
                                        String promoKey = rs.getString("key");
                                        long timeCreate = rs.getLong("timeCreate");
                                        map.putIfAbsent(promoKey, timeCreate);
                                    }
                                    return null;
                               }, false);
        return map;
    }

    private void createTables() {
        new TableConstructor("promocodes_data",
                             new TableColumn("key", ColumnType.TEXT).primaryKey(true),
                             new TableColumn("timeCreate", ColumnType.BIG_INT)
        ).create(dataBase);
        new TableConstructor("promocodes_players",
                             new TableColumn("uuid", ColumnType.VARCHAR_32),
                             new TableColumn("promocode", ColumnType.TEXT),
                             new TableColumn("usages", ColumnType.INT),
                             new TableColumn("timeUsage", ColumnType.BIG_INT)
        ).create(dataBase);
    }
}
