package com.backtesting.Indicator;

import com.backtesting.tradingsignal.BuySellForecast;
import com.ta4jTraining.candle.MorningStarPattern;
import com.ta4jTraining.candle.PiercingPattern;
import com.ta4jTraining.candle.ThreeWhiteSoldierPattern;
import com.ta4jTraining.candle.TowerBottomPattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.num.Num;

import java.text.ParseException;

public class CandleBottomUpIndicator implements Indicator<Boolean> {
    private static final Logger logger = LogManager.getLogger();
    BarSeries series;
    public CandleBottomUpIndicator(BarSeries series) throws ParseException {
        super();
        this.series = series;
    }


    @Override
    public Boolean getValue(int index) {
        if( series!=null) {
            PiercingPattern piercing = new PiercingPattern(series, 8, 3, 1, 1);
            MorningStarPattern morningStar = new MorningStarPattern(series, 9, 5, 1, 1, 3);
            ThreeWhiteSoldierPattern threeWhiteSoldier = new ThreeWhiteSoldierPattern(series, 9, 3, 1, 0.1, 1.5);
            TowerBottomPattern towerBottom = new TowerBottomPattern(series, 9, 3, 5, 0.5, 2, 2, 0.1);
            if ( morningStar.getValue(index)|| threeWhiteSoldier.getValue(index) || towerBottom.getValue(index) || piercing.getValue(index)) {
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
