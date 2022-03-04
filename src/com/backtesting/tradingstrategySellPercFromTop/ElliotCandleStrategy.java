package com.backtesting.tradingstrategySellPercFromTop;

import com.backtesting.Indicator.*;
import com.backtesting.IndicatorRule.IntegerIndexIndicatorRule;
import com.backtesting.datacrawler.SynPugna;
import com.shinobiutil.exception.SnbException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Rule;
import org.ta4j.core.Strategy;
import org.ta4j.core.rules.BooleanIndicatorRule;

import java.io.IOException;
import java.text.ParseException;

public class ElliotCandleStrategy {
    private static final Logger logger = LogManager.getLogger();
    public Strategy buildStrategy(String stockSymbol) throws SnbException, IOException, ParseException {
        BarSeries series = new SynPugna(stockSymbol).synPugna();

        if (series != null) {
            logger.info("stockSymbol {}", stockSymbol);
            ElliotBuyIndicator elliotBuyIndicator = new ElliotBuyIndicator(series, 2, series.getBeginIndex(), series.getEndIndex());
            CandleBottomUpIndicator candleBullish = new CandleBottomUpIndicator(series);

//            ElliotSellIndicator elliotSellIndicator = new ElliotSellIndicator(series, 2, series.getBeginIndex(), series.getEndIndex());
//            CandleTopDownIndicator candleBearishIndicator = new CandleTopDownIndicator(series);

            PercTopFallingSellIndicator sellingSignal = new PercTopFallingSellIndicator(series,2,series.getBeginIndex(), series.getEndIndex());

            Rule entryRule = new IntegerIndexIndicatorRule(elliotBuyIndicator).or(new BooleanIndicatorRule(candleBullish));
            Rule exitRule = new IntegerIndexIndicatorRule(sellingSignal);
            return new BaseStrategy("ElliotCandleStrategy", entryRule, exitRule, 5);
        }
        return null;
    }
}
