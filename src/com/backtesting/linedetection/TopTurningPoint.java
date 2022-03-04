package com.backtesting.linedetection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.num.Num;

public class TopTurningPoint extends CachedIndicator<Num> {

    private final Indicator<Num> indicator;

    private final int checkBarCount;
    private static final Logger logger = LogManager.getLogger();

//    private final SMAIndicator sma;

//	private final MACDIndicator macd;

    private final BarSeries series;

    /*
     * @param checkBarCount: number of bar b4 and after the turning point to
     * determine bottom usually 3-5 bars
     */
    public TopTurningPoint(Indicator<Num> indicator, BarSeries series, int checkBarCount) {
        super(indicator);
        this.indicator = indicator;
        this.series = series;
        this.checkBarCount = checkBarCount; // number of bar b4 and after the turning point
    }

    @Override
    protected Num calculate(int index) {
        if (isTopTurningPoint(index)) {
            return series.getBar(index).getClosePrice();
        }
        return null;

    }

    public Boolean isTopTurningPoint(int index) {

        int totalBar = series.getBarCount() - 1;
        if (index > totalBar - checkBarCount || index < checkBarCount) {
            return false;
        }
// to detect downtrend b4 turning point
        int countI = 0;
        int countM = 0;
        for (int i = index; i >= index - checkBarCount; i--) {
            if (indicator.getValue(index).isGreaterThan(indicator.getValue(i - 1))) {
                countI = countI + 1;
                if (countI == checkBarCount) {
                    for (int m = index; m < index + checkBarCount; m++) {
                        if (indicator.getValue(index).isGreaterThan(indicator.getValue(m + 1))) {
                            countM = countM + 1;
                            if (countM == checkBarCount) {
                                return true;
                            }
                        }
                    }
                }
            }
        }

        return false;

    }
}


