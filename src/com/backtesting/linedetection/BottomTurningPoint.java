package com.backtesting.linedetection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.num.Num;

public class BottomTurningPoint extends CachedIndicator<Num> {

    private static final Logger logger = LogManager.getLogger();
    private final Indicator<Num> indicator;

    private final int checkBarCount;

    private final BarSeries series;

    private double sizeDifferent;

    /*
     * @param checkBarCount: number of bar b4 and after the turning point to
     * determine bottom usually 3-5 bars
     */
    public BottomTurningPoint(Indicator<Num> indicator, BarSeries series, int checkBarCount, double sizeDifferent) {
        super(indicator);
        this.indicator = indicator;
        this.series = series;
        this.checkBarCount = checkBarCount; // number of bar b4 and after the turning point
        this.sizeDifferent = sizeDifferent;
    }

    @Override
    protected Num calculate(int index) {
        if (isBottomTurningTradingSignal(index, checkBarCount, sizeDifferent)) {

            return series.getBar(index).getClosePrice();

        }
        return null;

    }


    public Boolean isBottomTurningTradingSignal(int index, int checkBarCount, double sizeDifferent) {
        int totalBar = series.getBarCount() - 1;
        if (index >= totalBar || index <= checkBarCount) {
            return false;
        }
// to detect downtrend b4 turning point
        int countI = 0;
        int countM = 0;
        Num seizing = numOf(1).plus((numOf(sizeDifferent).dividedBy(numOf(100))));

        if (series.getBarCount() - 1 >= index && index>=5) {
//			System.out.println(indicator.getValue(index));
            for (int i = index; i > index - checkBarCount; i--) {
                if (indicator.getValue(i).isGreaterThan(numOf(0))
                        && indicator.getValue(i).isGreaterThan((indicator.getValue(i - 1)).multipliedBy(seizing))) {

                    countI = countI + 1;
                } else if (indicator.getValue(i).isLessThan(numOf(0))
                        && indicator.getValue(i).dividedBy(seizing).isGreaterThan((indicator.getValue(i - 1)))) {
                    countI = countI + 1;

//                    logger.info("countI {}", countI);
                    if (countI == checkBarCount) {
                        for (int m = i-1; m > i-1 - checkBarCount; m--) {
                            if (indicator.getValue(m).isGreaterThan(numOf(0))
                                    && indicator.getValue(m).multipliedBy(seizing).isLessThan(indicator.getValue(m - 1))) { //multipliedBy(seizing)
//								logger.info("m date {} macdValue {} ",series.getBar(m).getDateName(), indicator.getValue(m));
                                countM = countM + 1;
                            } else if (indicator.getValue(m).isLessThan(numOf(0))
                                    && indicator.getValue(m).dividedBy(seizing).isLessThan(indicator.getValue(m - 1))) {//dividedBy(seizing)
//								logger.info("m date {} macdValue {} ",series.getBar(m).getDateName(), indicator.getValue(m));
                                countM = countM + 1;
                            }
//                            logger.info("countM {}", countM);
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


    public double calculateBottomTradingSignal(int index, double sizeDifferent) {
        if (isBottomTurningPointTradingSignal(index, checkBarCount, sizeDifferent)) { // &&
            // indicator.getValue(index).doubleValue()<=0
//			logger.info("date {} indicator {} ", series.getBar(index).getDateName(), indicator.getValue(index));
            return series.getBar(index).getClosePrice().doubleValue();

        }
        return 0;
    }

    /*
     * isBottomTurningPointTradingSignal doesn't take MACD<0 into account, only
     * compare the value of MACD at bottom
     */
    public Boolean isBottomTurningPointTradingSignal(int index, int checkBarCount, double sizeDifferent) {
        int totalBar = series.getBarCount() - 1;
        if (index >= totalBar || index <= checkBarCount) {
            return false;
        }
// to detect downtrend b4 turning point
        int countI = 0;
        int countM = 0;
        Num seizing = numOf(1).plus((numOf(sizeDifferent).dividedBy(numOf(100))));

        if (series.getBarCount() - checkBarCount - 1 >= index) {
//			System.out.println(indicator.getValue(index));
            for (int i = index; i > index - checkBarCount; i--) {
                if (indicator.getValue(i).isLessThan(indicator.getValue(i - 1))) {
                    countI = countI + 1;

                    if (countI == checkBarCount) {
                        for (int m = index; m < index + checkBarCount; m++) {
                            if (indicator.getValue(m).isGreaterThan(numOf(0))
                                    && indicator.getValue(m).multipliedBy(seizing).isLessThan(indicator.getValue(m + 1))) {
//								logger.info("date {} macdValue {} ",series.getBar(m).getDateName(), indicator.getValue(m));
                                countM = countM + 1;

                            } else if (indicator.getValue(m).isLessThan(numOf(0))
                                    && indicator.getValue(m).dividedBy(seizing).isLessThan(indicator.getValue(m + 1))) {
//								logger.info("date {} macdValue {} ",series.getBar(m).getDateName(), indicator.getValue(m));
                                countM = countM + 1;
                            }

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