package com.yourname.baritone.pathing;

public enum Movement {
    WALK(1.0),
    JUMP(1.6),
    FALL(1.2),
    SWIM(2.0),
    DOOR(1.4);

    private final double cost;

    Movement(double cost) {
        this.cost = cost;
    }

    public double getCost() {
        return cost;
    }
}
