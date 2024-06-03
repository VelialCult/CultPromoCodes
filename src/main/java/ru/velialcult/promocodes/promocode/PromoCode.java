package ru.velialcult.promocodes.promocode;

import java.util.List;

/**
 * Written by Nilsson
 * 01.06.2024
 */
public class PromoCode {

    private String key;
    private final List<String> commands;
    private final List<String> message;
    private int maxUsages;
    private int maxUsagesPerPlayer;
    private long cooldownInSeconds;
    private boolean timed;
    private long timeToDeleteCode;

    public PromoCode(String key,
                     List<String> commands,
                     List<String> message,
                     int maxUsages,
                     int maxUsagesPerPlayer,
                     long cooldownInSeconds,
                     boolean timed,
                     long timeToDeleteCode) {
        this.key = key;
        this.commands = commands;
        this.message = message;
        this.maxUsages = maxUsages;
        this.maxUsagesPerPlayer = maxUsagesPerPlayer;
        this.cooldownInSeconds = cooldownInSeconds;
        this.timed = timed;
        this.timeToDeleteCode = timeToDeleteCode;
    }

    public long getCooldownInSeconds() {
        return cooldownInSeconds;
    }

    public void setCooldownInSeconds(long cooldownInSeconds) {
        this.cooldownInSeconds = cooldownInSeconds;
    }

    public void setMaxUsagesPerPlayer(int maxUsagesPerPlayer) {
        this.maxUsagesPerPlayer = maxUsagesPerPlayer;
    }

    public int getMaxUsagesPerPlayer() {
        return maxUsagesPerPlayer;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setMaxUsages(int maxUsages) {
        this.maxUsages = maxUsages;
    }

    public String getKey() {
        return key;
    }

    public int getMaxUsages() {
        return maxUsages;
    }

    public List<String> getCommands() {
        return commands;
    }

    public List<String> getMessage() {
        return message;
    }

    public void setTimeToDeleteCode(long timeToDeleteCode) {
        this.timeToDeleteCode = timeToDeleteCode;
    }

    public long getTimeToDeleteCode() {
        return timeToDeleteCode;
    }

    public boolean isTimed() {
        return timed;
    }

    public void setTimed(boolean timed) {
        this.timed = timed;
    }
}
