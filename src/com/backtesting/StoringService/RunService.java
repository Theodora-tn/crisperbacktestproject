package com.backtesting.StoringService;

import com.backtesting.main.chart.BuyAndSellSignalsToChart;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.shinobi.persistence.AbstractConsole;
import com.shinobiutil.exception.SnbException;

import java.io.IOException;
import java.text.ParseException;

public class RunService extends AbstractConsole{
	private static final Logger logger = LogManager.getLogger();

	public RunService() {
		super();
	}
	
	@Override
	public void doProcess() throws SnbException, IOException, ParseException {
		TradingImpl service = new TradingImpl(emg);
		service.runBackTest();
//		service.runSimpleTest();

	}
	
	public void runProcess()throws SnbException {
		
		try {
		StoringTradingSignal service = new StoringTradingSignal(emg, 60, "boardName", "VN100 ");
		service.runCrawler();
		logger.info("successfully run!");
		}catch (Exception e){
			logger.catching(e);
		}
	}

	public void runTestChart()throws SnbException {
		try {
//			doProcess();
			BuyAndSellSignalsToChart service = new BuyAndSellSignalsToChart(emg, "VNINDEX");
			service.process();
			logger.info("successfully run!");
		}catch (Exception e){
			logger.catching(e);
		}
	}


}
