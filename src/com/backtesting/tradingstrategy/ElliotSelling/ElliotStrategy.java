package com.backtesting.tradingstrategy.ElliotSelling;

import com.backtesting.Indicator.ElliotBuyIndicator;
import com.backtesting.Indicator.ElliotSellIndicator;
import com.backtesting.Indicator.PercTopFallingSellIndicator;
import com.backtesting.IndicatorRule.IntegerIndexIndicatorRule;
import com.backtesting.datacrawler.SynPugna;
import com.shinobiutil.exception.SnbException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Rule;
import org.ta4j.core.Strategy;

import java.io.IOException;
import java.text.ParseException;

public class ElliotStrategy {

    private static final Logger logger = LogManager.getLogger();

    public Strategy buildStrategy(String stockSymbol) throws SnbException, IOException, ParseException {
        BarSeries series = new SynPugna(stockSymbol).synPugna();
        if (series != null) {
            logger.info("stockSymbol {}", stockSymbol);
            ElliotBuyIndicator elliotBuyIndicator = new ElliotBuyIndicator(series, 2, series.getBeginIndex(), series.getEndIndex());

//            ElliotSellIndicator elliotSellIndicator = new ElliotSellIndicator(series, 2, series.getBeginIndex(), series.getEndIndex());
            ElliotSellIndicator sellingSignal = new ElliotSellIndicator(series,2,series.getBeginIndex(), series.getEndIndex());

            Rule entryRule = new IntegerIndexIndicatorRule(elliotBuyIndicator);
            Rule exitRule = new IntegerIndexIndicatorRule(sellingSignal);
            return new BaseStrategy("ElliotCandleStrategy", entryRule, exitRule, 10);
        }
        return null;
    }
}
