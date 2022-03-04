package com.backtesting.datacrawler;

import java.util.ArrayList;
import java.util.HashMap;

import org.ta4j.core.BarSeries;

public class CandleData {

	private final BarSeries series;

	public CandleData(BarSeries series) {
		this.series = series;
	}

	public HashMap<Double, ArrayList<Double>> getPriceMap() {

		HashMap<Double, ArrayList<Double>> priceMap = new HashMap<Double, ArrayList<Double>>();

		series.getBar(0).getClosePrice().doubleValue();

		int index = 0;
		int totalRecord = series.getBarCount();
		for (int i = index; i < totalRecord; i++) {
			ArrayList<Double> openClosePriceList = new ArrayList<Double>();
			openClosePriceList.add(series.getBar(i).getOpenPrice().doubleValue());
			openClosePriceList.add(series.getBar(i).getClosePrice().doubleValue());
			openClosePriceList.add(series.getBar(i).getHighPrice().doubleValue());
			openClosePriceList.add(series.getBar(i).getLowPrice().doubleValue());
			priceMap.put((double) i, openClosePriceList);
		}

		return priceMap;
	}

}
