package com.backtesting.Indicator;

import com.backtesting.tradingsignal.BuySellForecast;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.num.Num;

import java.text.ParseException;

public class ElliotBuyIndicator extends BuySellForecast implements Indicator<Integer> {
    private static final Logger logger = LogManager.getLogger();
    BarSeries series;
    double sizingBottom;
    int startIndex, lastIndex;

    public ElliotBuyIndicator(BarSeries series, double sizingBottom, int startIndex, int lastIndex) throws ParseException {
        super(series, sizingBottom, startIndex, lastIndex);
        this.series = series;
        if(series!=null){
            this.startIndex= series.getBeginIndex();;
            this.lastIndex =  series.getEndIndex();;
        }
        this.sizingBottom = sizingBottom;
    }

    @Override
    public Integer getValue(int index) {

            try {
                if(elliotBuyingIndicator(index)>0) {
//                    logger.info("ElliotBuySignal {}", series.getBar(buyingSignal(index)));
                    return elliotBuyingIndicator(index);
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
