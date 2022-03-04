package com.backtesting.Indicator;

import com.backtesting.tradingsignal.BuySellForecast;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.num.Num;

import java.text.ParseException;

public class ElliotSellIndicator extends BuySellForecast implements Indicator<Integer> {
    private static final Logger logger = LogManager.getLogger();
    BarSeries series;
    double sizingBottom;
    int startIndex, lastIndex;

    public ElliotSellIndicator(BarSeries series, double sizingBottom, int startIndex, int lastIndex) throws ParseException {
        super(series, sizingBottom, startIndex, lastIndex);
        this.series = series;
        if (series != null) {
            this.startIndex = series.getBeginIndex();
            ;
            this.lastIndex = series.getEndIndex();
            ;
        }
        this.sizingBottom = sizingBottom;
    }

    @Override
    public Integer getValue(int index) {

        try {
            if (elliotSellingSignal(index) > 0) {
                return elliotSellingSignal(index);
            }
        } catch (ParseException e) {
            logger.debug(e);
        }
        return 0;
    }

    @Override
    public BarSeries getBarSeries() {
        return series;
    }

    @Override
    public Num numOf(Number number) {
        return null;
    }
}
