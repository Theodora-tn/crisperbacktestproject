package com.backtesting.tradingsignal;

import com.backtesting.linedetection.BottomTurningPoint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.MACDIndicator;
import org.ta4j.core.indicators.helpers.*;
import org.ta4j.core.num.Num;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ElliotForecastUtil {

    private static final Logger logger = LogManager.getLogger();

    MACDIndicator macd;
    BarSeries series;
    DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    double sizingBottom;
    int startIndex, lastIndex;
    BottomTurningPoint bottomPoint;

    public ElliotForecastUtil(BarSeries series, double sizingBottom, int startIndex, int lastIndex) throws ParseException {

        this.series = series;
        this.sizingBottom = sizingBottom;
        this.startIndex = startIndex;
        this.lastIndex = lastIndex;
        macd = new MACDIndicator(new ClosePriceIndicator(series), 8, 12);
        bottomPoint = new BottomTurningPoint(macd, series, 2,15);

    }

    protected boolean isUpTrend(int index, double sizing, int testPeriod, Indicator<Num> Indicator) {

        if (index < testPeriod) {
            return false;
        }

        int count = 0;

        Double lastIndicatorValue = Indicator.getValue(index).doubleValue();
        Double initialIndicatorValue = Indicator.getValue(index - testPeriod + 1).doubleValue();

        if (lastIndicatorValue > (initialIndicatorValue * (1 + (sizing / 100)))) {
            for (int i = index - testPeriod + 2; i <= index; i++) {
                if (Indicator.getValue(i).isGreaterThan(Indicator.getValue(i - 1))) {
                    count = count + 1;

                    if (count == testPeriod - 1) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    protected double getPriceOnLine(HashMap<Integer, ArrayList<Double>> line, int index) throws ParseException {
        if(line.size()>0) {
            double y1 = line.get(0).get(3);
            double y2 = line.get(0).get(1);
            double x1 = line.get(0).get(2);
            double x2 = line.get(0).get(0);


            double[] param = parallelParamArray(series, y2, y1, x2, x1);

            double dateX = (double) format.parse(series.getBar(index).getSimpleDateName()).getTime();
            return param[0] * dateX + param[1];
        }
        return 0;
    }

    protected double[] parallelParamArray(BarSeries series, double y2, double y1, double x2, double x1) throws ParseException {
        double a = (y2 - y1) / (x2 - x1);
        double b = y1 - (a * x1);

        double[] lineParam = {0, 0};

        lineParam[0] = a;
        lineParam[1] = b;

        return lineParam;

    }

    public HashMap<Integer, ArrayList<Double>> getBottomMap() throws ParseException {

        ArrayList<Double> getBottomArr = new ArrayList<Double>();
        HashMap<Integer, ArrayList<Double>> bottomMap = new HashMap<>();
        getBottomArr = new ArrayList<Double>();
        int m = 0;
        if (getMACDBottom() != null) {

            for (Map.Entry<Integer, Integer> entry : getMACDBottom().entrySet()) {

//			getBottomArr.add(entry.getValue().doubleValue());
                getBottomArr.add((double) format.parse(series.getBar(entry.getValue()).getSimpleDateName()).getTime());
                getBottomArr.add(series.getBar(entry.getValue()).getClosePrice().doubleValue());

                bottomMap.put(m, getBottomArr);

            }

//			System.out.println("bottomMap " + bottomMap);

        }
        return bottomMap;
    }

    private Map<Integer, Integer> getMACDBottom() {
        Map<Integer, Integer> sortedMap = new HashMap<Integer, Integer>();

        int m = 0;

        for (int i = lastIndex; i > startIndex; i--) {
            if (bottomPoint.calculateBottomTradingSignal(i, 20) > 0) {
//					System.out.println("bottomPoint "+ bottomPoint.getValue(i) + " m "+m);
                sortedMap.put(m, i);
                m = m + 1;
                if (m == 2) {
                    break;
                }
            }
        }
        if (sortedMap.size() > 1) {

            int index0 = sortedMap.get(1);
            int index2 = sortedMap.get(0);

            Double bottom0 = new LowestValueIndicator(new LowPriceIndicator(series), 3).getValue(index0).doubleValue();
            Double bottom2 = new LowestValueIndicator(new LowPriceIndicator(series), 3).getValue(index2).doubleValue();

            if (bottom0 < bottom2) {
                return sortedMap;
            }

        }
        return null;
    }

    protected Date getDate(int index, int incrementDays) throws ParseException {

        String buyDate = series.getBar(index).getSimpleDateName();

        Calendar c = Calendar.getInstance();
        try {
            c.setTime(format.parse(buyDate));

        } catch (ParseException e) {
            e.printStackTrace();
        }
        // Incrementing the date by 1 day
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
            if(incrementDays>0) {
                c.add(Calendar.DAY_OF_MONTH, incrementDays + 2);
            }
            if(incrementDays==0){
                c.add(Calendar.DAY_OF_MONTH, incrementDays);
            }
        } else {
            c.add(Calendar.DAY_OF_MONTH, incrementDays);
        }

//		String newDate = format.format(c.getTime());

        return c.getTime();
    }



    public int getTopPointIndex() {
        if (getMACDBottom() != null) {
            int index0 = getMACDBottom().get(1);
            int index2 = getMACDBottom().get(0);
            int m = 0;
            Num highest = new HighPriceIndicator(series).getValue(index2);
            for (int i = index2 - 1; i >= index0; i--) {
                if (highest.isLessThan(new HighPriceIndicator(series).getValue(i))) {
                    highest = new HighPriceIndicator(series).getValue(i);
                    m = i;
                }
            }
//            logger.info("topPoint {}, index0 {}, index2 {}", series.getBar(m).getSimpleDateName()
//                    , series.getBar(index0).getSimpleDateName(), series.getBar(index2).getSimpleDateName());
            return m;
        }
        return 0;
    }

    protected double getPriceOnTopParallelLine(int index) throws ParseException {
        if (getTopPointIndex() > 0) {
            int topPointIndex = getTopPointIndex();
            double priceOnBottomLine = getPriceOnLine(getBottomMap(), topPointIndex);
            double priceOnTopLine = series.getBar(topPointIndex).getHighPrice().doubleValue();
            double priceDifferent = priceOnTopLine-priceOnBottomLine;
            return getPriceOnLine(getBottomMap(),index)+priceDifferent;
        }
        return 0;
    }

}
