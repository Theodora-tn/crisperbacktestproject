package com.backtesting.tradingsignal;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.backtesting.linedetection.TopTurningPoint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.MACDIndicator;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;

import com.ta4jTraining.TechAnalysisIndicator.BollingerBandCandleIndicator;
import com.backtesting.linedetection.BottomTurningPoint;
import org.ta4j.core.indicators.helpers.HighPriceIndicator;
import org.ta4j.core.indicators.helpers.HighestValueIndicator;

public class BuySellForecast extends ElliotForecastUtil {

    MACDIndicator macd;
    SMAIndicator sma;
    BarSeries series;
    DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    double sizingBottom;
    int startIndex, lastIndex, initialIndex, lastRangeIndex;
    //	HashMap<Integer, ArrayList<Double>> line;
    BottomTurningPoint bottomPoint;
    BollingerBandCandleIndicator bb;
    private static final Logger logger = LogManager.getLogger();

    public BuySellForecast(BarSeries series, double sizingBottom, int startIndex, int lastIndex) throws ParseException {
        super(series, sizingBottom, startIndex, lastIndex);
        this.series = series;
        this.sizingBottom = sizingBottom;
        this.startIndex = startIndex;
        this.lastIndex = lastIndex;
        macd = new MACDIndicator(new ClosePriceIndicator(series), 8, 12);
        sma = new SMAIndicator(new ClosePriceIndicator(series), 5);
        bottomPoint = new BottomTurningPoint(macd, series, 1, 15);
        bb = new BollingerBandCandleIndicator(series, 5, 2, 20, 2, 0);
    }

    public Date dateBuySignal(int index) throws ParseException {
        if (buyingSignal(index) > 0) {
            return getDate(buyingSignal(index), 0);
        }
        return null;
    }

    public BigDecimal buyingPriceSignal(int index) throws ParseException {
        if (buyingSignal(index) > 0) {
            int buyIndex = buyingSignal(index);
            if (buyIndex <= series.getEndIndex()) {
                return BigDecimal.valueOf(series.getBar(buyIndex).getClosePrice().doubleValue());
            }
        }
        return null;

    }

    public int buyingSignal(int index) throws ParseException {

        return elliotBuyingIndicator(index);
    }

    public int sellingSignal(int index) throws ParseException {

        if (buyingSignal(index) + 3 <= series.getBarCount() - 1) {
            int buyIndex = buyingSignal(index);
            TopTurningPoint top = new TopTurningPoint(macd, series, 1);

            for (int i = buyIndex + 2; i < series.getBarCount() - 1; i++) {
                double baseLinePrice = getPriceOnLine(getBottomMap(), index);
                double currentPrice = series.getBar(i).getClosePrice().doubleValue();

                if (getSellingFromTop(i, buyIndex, 7) > 0) {
                    return i + 1;
                }
//                if (currentPrice * (1 + (1 / sizingBottom)) < baseLinePrice) {
//                    return i;
//                }
            }
        }
        return 0;
    }

    public int getSellingFromTop(int currIndex, int buyIndex, double fallingPercFromTop) {
        Double highestPrice = new HighestValueIndicator(new HighPriceIndicator(series), (currIndex - buyIndex + 1)).getValue(currIndex).doubleValue();
        Double currPrice = new ClosePriceIndicator(series).getValue(currIndex).doubleValue();
        if (currPrice * (1 + (fallingPercFromTop / 100)) < highestPrice) {
            return currIndex;
        }
        return 0;
    }

    public int sellingPercFallingSignal(int index, int buyIndex, double fallingPercFromTop) throws ParseException {
        if (getSellingFromTop(index, buyIndex, fallingPercFromTop) > 0) {
            return index;
        }
        return 0;
    }

    public int elliotBuyingIndicator(int index) throws ParseException {
        EMAIndicator signalMACD = new EMAIndicator(macd, 9);
        int totalBarCount = series.getBarCount() - 1;
        if (getBottomMap().size() > 0 && index <=totalBarCount && index - 3 >= 0) {
            double baseLinePrice = getPriceOnLine(getBottomMap(), index);
            double closePrice = series.getBar(index).getClosePrice().doubleValue();

//            if (closePrice * (1 + (sizingBottom / 100)) > baseLinePrice
//                    && bottomPoint.getValue(index) != null //calculateBottomTradingSignal(index, 15)
//                    && bb.BBmiddle.getIndicator().getValue(index)
//                    .doubleValue() > series.getBar(index).getHighPrice().doubleValue()) {
//
//                return index;
//            }
//            if (index+2<=totalBarCount && bottomPoint.calculateBottomTradingSignal(index, 7) > 0) {
//                for (int i = index; i <= index + 2; i++) {
//                    Double currClosePrice = series.getBar(i).getClosePrice().doubleValue();
//                    if (currClosePrice >= closePrice * 1.05) {
//                        return i;
//                    }

            if (bottomPoint.getValue(index) != null) {
                for (int i = index-1; i >= index - 2; i--) {
                    Double prevClosePrice = series.getBar(i).getClosePrice().doubleValue();
                    if (prevClosePrice * 1.05 <= closePrice) {
                        return index;
                    }
                }
            }
        }
        return 0;
    }

    public int elliotSellingSignal(int index) throws ParseException {

        if (elliotBuyingIndicator(index) + 3 < series.getBarCount() - 1) {
            int buyIndex = elliotBuyingIndicator(index);
            TopTurningPoint top = new TopTurningPoint(macd, series, 1);

            for (int i = buyIndex + 2; i < series.getBarCount() - 1; i++) {
                double baseLinePrice = getPriceOnLine(getBottomMap(), index);
                double currentPrice = series.getBar(i).getClosePrice().doubleValue();

                if (getSellingFromTop(i, buyIndex, 9) > 0) {
                    return i;
                }
//                if (currentPrice * (1 + (1 / sizingBottom)) < baseLinePrice) {
//                    return i;
//                }
//                if (top.getValue(i) != null) {
//                    return i + 1;
//                }
            }
        }

        return 0;
    }


    public Date dateSellSignal(int index) throws ParseException {
        if (sellingSignal(index) > 0) {
            return getDate(sellingSignal(index), 0);
        }
        return null;
    }

}
