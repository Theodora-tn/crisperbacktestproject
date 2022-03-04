package com.backtesting.datacrawler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ta4j.core.BarSeries;

import com.invoker.indicator.service.impl.BarSeriesGenerator;
import com.invoker.shinobibot.AbstractStockSymbolBot;
import com.shinobiutil.exception.SnbException;
import com.backtesting.tradingsignal.BuySellForecast;

import java.io.IOException;
import java.text.ParseException;

import com.backtesting.config.DbEnum;
import com.pugna.crawler.candle.service.StockChartCrawlerFactory;
import com.pugna.crawler.candle.service.StockChartCrawlerService;
import com.pugna.crawler.candle.service.StockChartSourceEnum;

public class SynPugna extends AbstractStockSymbolBot {

    private static final Logger logger = LogManager.getLogger();
    protected String stockSymbol;
    public BarSeries series;
    protected StockChartCrawlerService crawStock;
    protected SynPugna service;

    public SynPugna(String stockSymbol) {
        super(stockSymbol);
        this.stockSymbol = stockSymbol;
        crawStock = StockChartCrawlerFactory.getInstance()
                .createCrawler(StockChartSourceEnum.valueOf(System.getProperty("CHART_SOURCE", "SSI")));
    }

    public BarSeries synPugna() throws SnbException, IOException, ParseException {
//		logger.info("start new stocksymbol {} ",stockSymbol );
        if (!stockSymbol.equals("")) {
            service = new SynPugna(stockSymbol);
            service.syncStockChart(DbEnum.PUGNA.getName(), stockSymbol);
            return series = service.generateBarSeries(stockSymbol);
        }

        return null;
    }

    public void printSignal() throws SnbException, IOException, ParseException {
        series = synPugna();

        if (series != null) {
//            logger.info(series.getBarCount() - 1);
            for (int i = series.getBarCount() - 1; i > series.getBarCount() - 50; i--) {
                BuySellForecast signal = new BuySellForecast(series, 2, 1, i);
                if (signal.buyingSignal(i) > 0) {
                    logger.info("buying {}", series.getBar(i).getDateName());
                    if (signal.sellingSignal(i) > 0) {
                        logger.info("selling {}", series.getBar(signal.sellingSignal(i)).getDateName());
//					logger.info("done getting signal");
                    }
                }
            }
        }
    }


    public BarSeries barSeries() {

        return series;
    }

    private BarSeries generateBarSeries(String stockSymbol) throws SnbException {
        BarSeriesGenerator service = new BarSeriesGenerator(emg, stockSymbol);
        service.process();
        barSeries = service.getBarSeries();
        return barSeries;
    }

    @Override
    public void syncRawData(String stockSymbol) throws SnbException {
        // TODO Auto-generated method stub

    }

    @Override
    public void generateSignal(String stockSymbol) throws SnbException {
        // TODO Auto-generated method stub

    }

    @Override
    public void applyTradingStrategy(String stockSymbol) throws SnbException {
        // TODO Auto-generated method stub

    }

    @Override
    public void generateTradingReport(String stockSymbol) throws SnbException {
        // TODO Auto-generated method stub
    }

}
