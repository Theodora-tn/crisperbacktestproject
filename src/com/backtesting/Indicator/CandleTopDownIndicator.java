package com.backtesting.Indicator;

import com.ta4jTraining.candle.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.num.Num;

import java.text.ParseException;

public class CandleTopDownIndicator implements Indicator<Boolean> {
    private static final Logger logger = LogManager.getLogger();
    BarSeries series;
    public CandleTopDownIndicator(BarSeries series) throws ParseException {
        super();
        this.series = series;
    }

    @Override
    public Boolean getValue(int index) {
        if( series!=null) {
            DarkCloudCoverPattern darkCloud = new DarkCloudCoverPattern(series, 9, 3, 1, 1.5);
            EveningStarPattern eveningStar = new EveningStarPattern(series, 9, 5, 1, 3, 1);
            ThreeBlackCrowsPattern blackCrows = new ThreeBlackCrowsPattern(series, 9, 3, 1.0, 0.1, 0);
            TowerTopPattern towerTop = new TowerTopPattern(series, 9, 3, 5, 1, 4, 5, 0.1);
            if ( darkCloud.getValue(index)|| eveningStar.getValue(index) || blackCrows.getValue(index) || towerTop.getValue(index)) {
                return true;
            }
        }
        return false;
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
