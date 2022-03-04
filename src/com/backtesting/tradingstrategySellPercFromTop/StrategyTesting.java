package com.backtesting.tradingstrategySellPercFromTop;

import com.backtesting.Indicator.ElliotBuyIndicator;
import com.backtesting.IndicatorRule.IntegerIndexIndicatorRule;
import com.backtesting.datacrawler.SynPugna;
import com.shinobiutil.exception.SnbException;
import com.ta4jTraining.candle.DarkCloudCoverPattern;
import com.ta4jTraining.candle.PiercingPattern;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Rule;
import org.ta4j.core.Strategy;
import org.ta4j.core.rules.BooleanIndicatorRule;

import java.io.IOException;
import java.text.ParseException;

public class StrategyTesting  {

    public Strategy buildStrategy(String stockSymbol) throws SnbException, IOException, ParseException {
        BarSeries series = new SynPugna(stockSymbol).synPugna();
        if (series == null) {
            throw new IllegalArgumentException("Series cannot be null");
        }
        ElliotBuyIndicator elliotIndicator = new ElliotBuyIndicator(series, 2, series.getBeginIndex(), series.getEndIndex());
        DarkCloudCoverPattern topDownIndicator = new DarkCloudCoverPattern(series, 5, 3, 0, 0);
        PiercingPattern bottomUpIndicator = new PiercingPattern(series, 5, 3, 0, 0);
        Rule entryRule = new IntegerIndexIndicatorRule(elliotIndicator).or(new BooleanIndicatorRule(bottomUpIndicator));


        // Exit rule
        Rule exitRule = new BooleanIndicatorRule(topDownIndicator);
        return new BaseStrategy(entryRule, exitRule);
    }

}
