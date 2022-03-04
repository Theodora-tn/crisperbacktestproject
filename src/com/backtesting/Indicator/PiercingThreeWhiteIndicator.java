package com.backtesting.Indicator;

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

public class PiercingThreeWhiteIndicator implements Indicator<Boolean> {
    private static final Logger logger = LogManager.getLogger();
    BarSeries series;
    public PiercingThreeWhiteIndicator(BarSeries series) throws ParseException {
        super();
        this.series = series;
    }


    @Override
    public Boolean getValue(int index) {
        if( series!=null) {
            PiercingPattern piercing = new PiercingPattern(series, 8, 3, 1, 1);

            ThreeWhiteSoldierPattern threeWhiteSoldier = new ThreeWhiteSoldierPattern(series, 9, 3, 1, 0.1, 1.5);

            if (threeWhiteSoldier.getValue(index)  || piercing.getValue(index)) {
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
