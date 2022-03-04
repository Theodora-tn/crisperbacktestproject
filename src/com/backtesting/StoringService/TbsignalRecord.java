package com.backtesting.StoringService;

import com.backtesting.config.DbEnum;
import com.backtesting.config.DbSignal;
import com.backtesting.datacrawler.SynPugna;

import com.backtesting.entity.Tbsignal;
import com.backtesting.tradingsignal.BuySellForecast;
import com.shinobi.persistence.AbstractDbService;
import com.shinobi.persistence.EntityManager;
import com.shinobi.persistence.EntityManagerGroup;
import com.shinobi.persistence.Query;
import com.shinobiutil.exception.SnbException;
import org.apache.commons.collections.map.HashedMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ta4j.core.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class TbsignalRecord extends AbstractDbService {

    private static final Logger logger = LogManager.getLogger();

    private final BarSeries series;
    String stockSymbol;

    public TbsignalRecord(EntityManagerGroup emg, String stockSymbol) throws SnbException, IOException, ParseException {
        super(emg);
        this.series = new SynPugna(stockSymbol).synPugna();
        this.stockSymbol = stockSymbol;

    }

    @Override
    public void process() throws SnbException, IOException, ParseException {
        List<Tbsignal> list = setTbSignal(stockSymbol);// input Tbsignal into sql
        new TbbottradeInput(emg).process();// input tbtrade into sql
    }


    private List<Tbsignal> setTbSignal(String stockSymbol) throws IOException, SnbException, ParseException {
        List<Tbsignal> list = new ArrayList<>();
        Tbsignal tbsignal = new Tbsignal();
        Map<String, String> uniqueStockChart = new HashedMap();
        String unique;
        int loop = 0;
        if (series != null) {
            for (int i = series.getBeginIndex(); i <= series.getEndIndex(); i++) {
                if (new BuySellForecast(series, 2, series.getBeginIndex(), i).elliotBuyingIndicator(i) > 0) {

                    int buySignalIndex = i;
//                    logger.info("buydate {}", series.getBar(buySignalIndex).getDateName());
                    tbsignal.setBotid(7012021);
                    tbsignal.setOrderprice(BigDecimal.valueOf(series.getBar(buySignalIndex).getClosePrice().doubleValue()));
                    tbsignal.setSignaldate(getDate(buySignalIndex, 0));
                    tbsignal.setStocksymbol(stockSymbol);
                    tbsignal.setOrderside(DbSignal.BUY.toString());
                    tbsignal.setTradingdate(getDate(buySignalIndex, 1));
                    unique = stockSymbol + series.getBar(buySignalIndex).getDateName();


                    if (!uniqueStockChart.containsKey(unique)) {
                        insertSignalTable(tbsignal);
                        uniqueStockChart.put(unique, unique);
                    }


                    for (int sellIndex = buySignalIndex+2; sellIndex<=series.getEndIndex(); sellIndex++) {
                        if (new BuySellForecast(series, 2, series.getBeginIndex(), sellIndex).sellingPercFallingSignal(sellIndex, buySignalIndex,7) > 0) {
                            int sellSignalIndex = sellIndex;
//                            logger.info("selldate {}", series.getBar(sellSignalIndex).getDateName());
                            Bar barExit = series.getBar(sellSignalIndex);
                            tbsignal.setOrderprice(BigDecimal.valueOf(barExit.getClosePrice().doubleValue()));
                            tbsignal.setSignaldate(getDate(sellSignalIndex, 0));
                            tbsignal.setStocksymbol(stockSymbol);
                            tbsignal.setOrderside(DbSignal.SELL.toString());
                            tbsignal.setTradingdate(getDate(sellSignalIndex, 1));
                            tbsignal.setBotid(7012021);
                            list.add(tbsignal);

                            unique = stockSymbol + series.getBar(sellSignalIndex).getDateName();

                            if (!uniqueStockChart.containsKey(unique)) {
                                insertSignalTable(tbsignal);
                                uniqueStockChart.put(unique, unique);
                                break;
                            }
                        }
                    }
                }
            }
        }
        return list;
    }

    private void insertSignalTable(Tbsignal tb) throws SnbException {
        EntityManager em = emg.getEntityManager(DbEnum.TRAINING.getName());

        Query query = em.createQuery("INSERT INTO tbsignal " + "( stocksymbol , orderprice , tradingdate , orderside , botid , signaldate )" + " VALUES " + "( :stocksymbol , :orderprice , :tradingdate , :orderside , :botid , :signaldate )");

        query.setParameter("stocksymbol", tb.getStocksymbol());
        query.setParameter("orderprice", tb.getOrderprice());
        query.setParameter("tradingdate", tb.getTradingdate());
        query.setParameter("orderside", tb.getOrderside());
        query.setParameter("botid", tb.getBotid());
        query.setParameter("signaldate", tb.getSignaldate());

        query.addBatch();
        query.executeBatch();
    }


    protected Date getDate(int index, int incrementDays) throws ParseException {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String buyDate = series.getBar(index).getSimpleDateName();

        Calendar c = Calendar.getInstance();
        try {
            c.setTime(format.parse(buyDate));

        } catch (ParseException e) {
            e.printStackTrace();
        }
        // Incrementing the date by 1 day
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {

            c.add(Calendar.DAY_OF_MONTH, incrementDays + 2);
        } else {
            c.add(Calendar.DAY_OF_MONTH, incrementDays);
        }

//		String newDate = format.format(c.getTime());

        return c.getTime();
    }


}
