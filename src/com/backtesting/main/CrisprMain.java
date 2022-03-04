package com.backtesting.main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import com.backtesting.StoringService.RunService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jfree.ui.RefineryUtilities;
import org.ta4j.core.BarSeries;

import com.shinobi.persistence.impl.DataSourceManager;
import com.shinobiutil.caching.ShinobiCacheFactory;
import com.shinobiutil.dbhelper.DbHelper;
import com.shinobiutil.dbhelper.DbHelperFactory;
import com.shinobiutil.exception.SnbException;
import com.backtesting.config.DbEnum;


//import com.ta4jTraining.util.Ta4jTrainingUtil;;

public class CrisprMain {

	private static final Logger logger;

	static {
		if (System.getProperty("logname") == null) {
			System.setProperty("logname", "ta4jTraining.log");
		}

		logger = LogManager.getLogger(CrisprMain.class);
	}

	public static void main(String[] args) throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
			SecurityException, SnbException, ParseException, IOException {

		logger.info("register database begin");

		registerLogDb();
		registerExternalDb("dbsignalconfig", DbEnum.TRAINING);
		registerExternalDb("dbpugnaconfig", DbEnum.PUGNA);

		DataSourceManager.setDefaultEntityManager(DbEnum.TRAINING.getName());

		ShinobiCacheFactory.getInstance().isEnableHazelcast = false;

		logger.info("register database done");
		long startTime = System.currentTimeMillis();

		startService(args);

		logger.info("all process in " + (System.currentTimeMillis() - startTime) / 1000 + " s");
	}


	private static void startService(String[] args) throws SnbException, ParseException, IOException {

		RunService service = new RunService();
		service.doProcess();
	}

	private static void registerLogDb()
			throws ClassNotFoundException, FileNotFoundException, IllegalAccessException, InstantiationException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		String logdbconfig = System.getProperty("logdbconfig");
		if (logdbconfig != null && !logdbconfig.equals("")) {

			DbHelper dbHelper = DbHelperFactory.getInstance().createDbHelper("logdbconfig");
			dbHelper.loadConfig(System.getProperty("logdbconfig"));
		}
	}

	private static void registerExternalDb(String configname, DbEnum dbname)
			throws FileNotFoundException, ClassNotFoundException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		HashMap<String, String> dbconfigs = new HashMap<String, String>();

		dbconfigs.put(configname, dbname.getName());

		for (Map.Entry<String, String> dbconfig : dbconfigs.entrySet()) {

			String configPath = System.getProperty(dbconfig.getKey());
			String configName = dbconfig.getValue();

			if (configPath == null) {
				throw new FileNotFoundException("Cannot find " + dbconfig.getKey() + " file");
			}

			DataSourceManager.registerDataSource(configName, configPath);

		}
	}

}
