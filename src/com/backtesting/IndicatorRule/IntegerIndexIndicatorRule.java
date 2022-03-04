package com.backtesting.IndicatorRule;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ta4j.core.Indicator;
import org.ta4j.core.TradingRecord;
import org.ta4j.core.rules.AbstractRule;

public class IntegerIndexIndicatorRule extends AbstractRule {

    private final Indicator<Integer> indicator;
    private static final Logger logger = LogManager.getLogger();
    public IntegerIndexIndicatorRule(Indicator<Integer> indicator) {
        this.indicator = indicator;
    }
    @Override
    public boolean isSatisfied(int index, TradingRecord tradingRecord) {
            final boolean satisfied = indicator.getValue(index)>0;
            return satisfied;
    }
}
