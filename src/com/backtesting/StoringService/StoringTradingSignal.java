package com.backtesting.StoringService;

import com.backtesting.entity.Tbtradingsignal;
import com.backtesting.view.viewstocklist;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.shinobi.persistence.AbstractDbService;
import com.shinobi.persistence.EntityManager;
import com.shinobi.persistence.EntityManagerGroup;
import com.shinobi.persistence.Query;
import com.shinobiutil.exception.SnbException;
import com.backtesting.config.DbEnum;
import com.backtesting.datacrawler.SynPugna;
import com.backtesting.tradingsignal.BuySellForecast;
import org.apache.commons.collections.map.HashedMap;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StoringTradingSignal extends AbstractDbService {

    private static final Logger logger = LogManager.getLogger();
    BarSeries barSeries;
    int movingBackWardTestingBar;
    String boardName, stockList;

    public StoringTradingSignal(EntityManagerGroup emg, int movingBackWardTestingBar, String boardName, String stockList) {
        super(emg);
        this.boardName = boardName;
        this.stockList = stockList;
        this.movingBackWardTestingBar = movingBackWardTestingBar;
    }

    @Override
    public void process() throws SnbException, IOException, ParseException {


    }

    public void runCrawler() throws SnbException {
        try {
            logger.info("begin to getSignal stock chart");

            clearOldData();

            crawlElliotTradingSignal();

            logger.info("crawl stock chart successfully");
        } catch (Exception e) {
            logger.catching(Level.TRACE, e);
            throw new SnbException(e);
        }

    }

    private void crawlElliotTradingSignal() throws SnbException, IOException, ParseException {

//        List<String> symbols = getStockSymbolListfromView();
        List<String> symbols = getStockSymbolMultiList("https://price.tpbs.com.vn/api/StockBoardApi/getStockList");

        for (String symbol : symbols) {
            logger.debug("total stock: {}" + symbol);
            logger.info("symbols {}", symbol);
            List<Tbtradingsignal> stockcharts = getTradingTable(symbol);

            if (stockcharts != null && !stockcharts.isEmpty()) {
                storeStockChart(stockcharts);
                logger.info("stored! " + symbol);
            }

        }

    }

    private void clearOldData() throws SnbException {
        EntityManager em = emg.getEntityManager(DbEnum.TRAINING.getName());
        Query query = em.createQuery("TRUNCATE TABLE tbtradingsignal");
        query.executeUpdate();
//		logger.info("Clearing Done");
    }

    private void storeStockChart(List<Tbtradingsignal> list) throws SnbException {
        EntityManager em = emg.getEntityManager(DbEnum.TRAINING.getName());

        Query query = em.createQuery("INSERT INTO tbtradingsignal " + "( symbolname , buyingprice , signalbuydate , currentcloseprice , sellingprice , sellingdate )"
                + " VALUES " + "( :symbolname , :buyingprice , :signalbuydate , :currentcloseprice , :sellingprice , :sellingdate )");

//		Date today = new Date();

        for (Tbtradingsignal Tbtradingsignal : list) {
            query.setParameter("symbolname", Tbtradingsignal.getSymbolname());
            query.setParameter("buyingprice", Tbtradingsignal.getBuyingprice());
            query.setParameter("signalbuydate", Tbtradingsignal.getSignalbuydate());
            query.setParameter("currentcloseprice", Tbtradingsignal.getCurrentcloseprice());
            query.setParameter("sellingprice", Tbtradingsignal.getSellingprice());
            query.setParameter("sellingdate", Tbtradingsignal.getSignalsellingdate());

            query.addBatch();
        }

        query.executeBatch();
//		logger.info("input into SQL Done");
    }

    private List<Tbtradingsignal> getTradingTable(String stockSymbol) throws IOException, SnbException, ParseException {

        SynPugna getPugna = new SynPugna(stockSymbol);
        barSeries = getPugna.synPugna();

        List<Tbtradingsignal> list = new ArrayList<>();
        Map<String, String> uniqueStockChart = new HashedMap();
        String unique;

        if (barSeries != null) {
            int totalBar = barSeries.getBarCount() - 1;

            for (int i = totalBar; i >= totalBar - movingBackWardTestingBar; i--) {
                BuySellForecast signal = new BuySellForecast(barSeries, 1, 1, i);
                Tbtradingsignal Tbtradingsignal = new Tbtradingsignal();
                if (signal.buyingSignal(i) > 0) {

                    Tbtradingsignal.setSymbolname(stockSymbol);

                    Tbtradingsignal.setBuyingprice(signal.buyingPriceSignal(i));

                    Tbtradingsignal.setSignalbuydate(signal.dateBuySignal(i));

                    Tbtradingsignal.setCurrentcloseprice(
                            BigDecimal.valueOf(barSeries.getLastBar().getClosePrice().doubleValue()));


                    if (signal.sellingSignal(i) > 0) {

                        Bar bar = barSeries.getBar(signal.sellingSignal(i));

                        Tbtradingsignal.setSellingprice(BigDecimal.valueOf(bar.getClosePrice().doubleValue()));

                        Tbtradingsignal.setSignalsellingdate(signal.dateSellSignal(i));
                    }

                    unique = stockSymbol + barSeries.getBar(i).getDateName();

                    if (!uniqueStockChart.containsKey(unique)) {
                        list.add(Tbtradingsignal);
                        uniqueStockChart.put(unique, unique);

                    }

                }

            }
        }
//		logger.info(stockSymbol);
        return list;
    }

//    public List<String> getStockSymbolList(String urlPath) throws IOException {
//		"https://price.tpbs.com.vn/api/StockBoardApi/getStockList"
//        List<String> result = new ArrayList<>();
//
//        URL url = new URL(urlPath);
//
//        String data = String.format("{\n \"%s\": \"%s\",\n }", boardName, stockList);
//
//
//        byte[] postDataBytes = data.toString().getBytes("UTF-8");
//
//        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//        conn.setRequestMethod("POST");
//        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//        conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
//        conn.setDoOutput(true);
//        conn.getOutputStream().write(postDataBytes);
//
//        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
//
//        StringBuilder sb = new StringBuilder();
//        for (int c; (c = in.read()) >= 0; )
//            sb.append((char) c);
//        String response = sb.toString();
//
//        JsonParser parser = new JsonParser();
//        JsonObject o = parser.parse(response).getAsJsonObject();
//        String[] array = o.get("content").getAsString().split("\\W+");
//
//        for (String ele : array) {
//            result.add(ele);
//        }
//        logger.info("stockList {}" + result);
//        return result;
//    }

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

    public List<String> getStockSymbolMultiList(String urlPath) throws IOException, SnbException {
//		"https://price.tpbs.com.vn/api/StockBoardApi/getStockList"
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
