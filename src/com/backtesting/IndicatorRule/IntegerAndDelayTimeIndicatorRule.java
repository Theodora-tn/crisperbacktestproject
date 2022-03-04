package com.backtesting.IndicatorRule;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.TradingRecord;
import org.ta4j.core.rules.AbstractRule;

public class IntegerAndDelayTimeIndicatorRule extends AbstractRule {
    private final Indicator<Integer> indexIndicator;
    private final Indicator<Boolean> booleanIndicator;
    private final int delayDays;
    BarSeries series;

    public IntegerAndDelayTimeIndicatorRule(Indicator<Integer> indexIndicator, Indicator<Boolean> booleanIndicator, int delayDays, BarSeries series) {
        this.indexIndicator = indexIndicator;
        this.booleanIndicator = booleanIndicator;
        this.delayDays = delayDays;
        this.series = series;
    }

    @Override
    public boolean isSatisfied(int index, TradingRecord tradingRecord) {
        boolean satisfied = false;
        if (indexIndicator.getValue(index) > 0 && series.getEndIndex()>=index+delayDays) {
            for (int i = index; i <= index + delayDays; i++) {
                if(booleanIndicator.getValue(i)==true){
                    satisfied=true;
                }
            }
        }
        return satisfied;
    }
}
