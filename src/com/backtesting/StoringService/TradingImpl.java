package com.backtesting.StoringService;

import com.backtesting.config.DbEnum;
import com.backtesting.view.viewstocklist;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.shinobi.persistence.AbstractDbService;
import com.shinobi.persistence.EntityManager;
import com.shinobi.persistence.EntityManagerGroup;
import com.shinobi.persistence.Query;
import com.shinobiutil.exception.SnbException;
import org.apache.commons.collections.map.HashedMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TradingImpl extends AbstractDbService {

    private static final Logger logger = LogManager.getLogger();

    public TradingImpl(EntityManagerGroup emg) {
        super(emg);
    }

    @Override
    public void process() throws SnbException, IOException, ParseException {

    }

    public void runBackTest() throws SnbException, IOException, ParseException {
        List<String> stockSymbols = new ArrayList<>();
//        stockSymbols = getStockSymbolList("https://price.tpbs.com.vn/api/StockBoardApi/getStockList",
//                "boardName", "VN30");
        stockSymbols = getStockSymbolMultiList();
        clearOldTbTradingData();
        for (String stockSymbol : stockSymbols) {
            clearOldTbSignalData();
//            Strategy testingStrategy = new ElliotStrategy().buildStrategy(stockSymbol);
//                TbsignalInput backtest = new TbsignalInput(emg, stockSymbol, testingStrategy);
            TbsignalRecord backtest = new TbsignalRecord(emg, stockSymbol);
            backtest.process();
        }

    }


    public void runSimpleTest() throws SnbException, IOException, ParseException {
        TbsignalRecord backtest = new TbsignalRecord(emg, "VNINDEX");
        backtest.process();
    }


    public List<String> getStockSymbolList(String urlPath, String boardName, String stockList) throws IOException {
//		"https://price.tpbs.com.vn/api/StockBoardApi/getStockList"
        List<String> result = new ArrayList<>();

        URL url = new URL(urlPath);

        String data = String.format("{\n \"%s\": \"%s\",\n }", boardName, stockList);


        byte[] postDataBytes = data.toString().getBytes("UTF-8");

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
        conn.setDoOutput(true);
        conn.getOutputStream().write(postDataBytes);

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

        StringBuilder sb = new StringBuilder();
        for (int c; (c = in.read()) >= 0; )
            sb.append((char) c);
        String response = sb.toString();

        JsonParser parser = new JsonParser();
        JsonObject o = parser.parse(response).getAsJsonObject();
        String[] array = o.get("content").getAsString().split("\\W+");

        for (String ele : array) {
            result.add(ele);
        }
        logger.info("stockList {}" + result);
        return result;
    }

    public List<String> getStockSymbolListfromView() throws SnbException {
        List<String> symbols = new ArrayList<>();
        EntityManager pugnaEm = emg.getEntityManager(DbEnum.PUGNA.getName());
        List<viewstocklist> list = pugnaEm
                .createQuery("SELECT symbolname FROM viewstocklist ", viewstocklist.class).getResultList();

        for (viewstocklist stockSymbol : list) {
            symbols.add(stockSymbol.getSymbolname());
        }
        return symbols;
    }

    private void clearOldTbTradingData() throws SnbException {
        EntityManager em = emg.getEntityManager(DbEnum.TRAINING.getName());
        Query query = em.createQuery("TRUNCATE TABLE tbbottrade");
        query.executeUpdate();
//		logger.info("Clearing Done");
    }



    private void clearOldTbSignalData() throws SnbException {
        EntityManager em = emg.getEntityManager(DbEnum.TRAINING.getName());
        Query query = em.createQuery("TRUNCATE TABLE tbsignal");
        query.executeUpdate();
    }

    public List<String> getStockSymbolMultiList() throws IOException, SnbException {
		String urlPath="https://price.tpbs.com.vn/api/StockBoardApi/getStockList";
        List<String> result = new ArrayList<>();
        Map<String, String> uniqueSymbol = new HashedMap();
        URL url = new URL(urlPath);

        for (String stockList : listStockListNameCrawler()) {
            String data = String.format("{\n \"%s\": \"%s\",\n }", "boardName", stockList);

            byte[] postDataBytes = data.toString().getBytes("UTF-8");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
            conn.setDoOutput(true);
            conn.getOutputStream().write(postDataBytes);

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

            StringBuilder sb = new StringBuilder();
            for (int c; (c = in.read()) >= 0; )
                sb.append((char) c);
            String response = sb.toString();

            JsonParser parser = new JsonParser();
            JsonObject o = parser.parse(response).getAsJsonObject();
            String[] array = o.get("content").getAsString().split("\\W+");

            for (String ele : array) {
                if (!uniqueSymbol.containsKey(ele)) {
                    result.add(ele);
                    uniqueSymbol.put(ele, ele);
                }

            }
            for (String symbol : getStockSymbolListfromView()) {
                if (!uniqueSymbol.containsKey(symbol)) {
                    result.add(symbol);
                    uniqueSymbol.put(symbol, symbol);
                }
            }
        }
        logger.info("stockList {}" + result);
        return result;

    }

    public List<String> listStockListNameCrawler() throws IOException {
        List<String> result = new ArrayList<>();
        result.add("VN100");
        result.add("HNX30");
        return result;
    }


}
