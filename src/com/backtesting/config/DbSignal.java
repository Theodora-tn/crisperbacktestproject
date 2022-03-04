package com.backtesting.config;

public enum DbSignal {
    BUY, SELL,
    ;

    public String getName() {
        return this.toString();
    }
}
