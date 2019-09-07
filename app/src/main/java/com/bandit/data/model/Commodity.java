package com.bandit.data.model;

public class Commodity {

    public Commodity(String name, boolean buy) {
        this.name = name;
        this.buy = buy;
    }

    private String name;

    private boolean buy;

    public String getName() {
        return name;
    }

    public boolean canBuy() {
        return buy;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBuy(boolean buy) {
        this.buy = buy;
    }
}
