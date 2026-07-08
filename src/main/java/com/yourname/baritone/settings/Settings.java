package com.yourname.baritone.settings;

public final class Settings {
    public String commandPrefix = "#";
    public boolean allowBreak = true;
    public boolean allowPlace = true;
    public boolean allowSprint = true;
    public boolean allowInventory = true;
    public boolean allowParkour = false;
    public boolean allowParkourPlace = false;
    public boolean avoidance = true;
    public int mobAvoidanceRadius = 8;
    public int failureTimeout = 5;
    public boolean chatDebug = false;
    public String color = "#FF6B00";
    public boolean renderPath = true;
    public boolean renderGoal = true;
    public boolean renderSelectionBoxes = true;
    public double blockReachDistance = 4.5;
    public int maxFallHeightNoWater = 3;
    public int maxFallHeightBucket = -1;
    public boolean buildInLayers = true;
    public String layerOrder = "DOWN_UP";
    public int maxPathDistance = 5000;
    public boolean avoidLava = true;
    public boolean avoidDangerousBlocks = true;
    public double followDistance = 2.5;
    public int followRepathTicks = 40;
    public int scanRadius = 32;
    public int actionRateLimitPerSecond = 20;
    public int criticalDurability = 8;
}
