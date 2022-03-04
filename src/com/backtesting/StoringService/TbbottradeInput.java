package com.backtesting.StoringService;

import com.backtesting.config.DbEnum;
import com.backtesting.config.DbSignal;
import com.backtesting.entity.Tbbottrade;
import com.backtesting.entity.Tbsignal;
import com.shinobi.persistence.AbstractDbService;
import com.shinobi.persistence.EntityManager;
import com.shinobi.persistence.EntityManagerGroup;
import com.shinobi.persistence.Query;
import com.shinobiutil.exception.SnbException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static java.lang.Math.round;

public class TbbottradeInput extends AbstractDbService {

    public TbbottradeInput(EntityManagerGroup emg) {
        super(emg);
    }

    private static final Logger logger = LogManager.getLogger();

    @Override
    public void process() throws SnbException, IOException, ParseException {
        insertTbbotTradingTable();
    }

    private void insertTbbotTradingTable() throws SnbException {
        EntityManager em = emg.getEntityManager(DbEnum.TRAINING.getName());

        List<Tbsignal> signalList = em.createQuery("SELECT * FROM tbsignal ORDER BY tradingdate ASC ", Tbsignal.class).getResultList();
        logger.info("signalList {}", signalList.size());


        Query query = em.createQuery("INSERT INTO tbbottrade "
                + "( buysignalid , sellsignalid , stocksymbol , buyorderid , sellorderid , buyprice , sellprice , buydate , selldate , tradevolume )"
                + " VALUES "
                + "( :buysignalid , :sellsignalid , :stocksymbol , :buyorderid , :sellorderid , :buyprice , :sellprice , :buydate , :selldate , :tradevolume )");
        if (signalList.size() > 0) {
            int volume = 0;
            double capital = 100000000;
            Date checkBuyDate = getDatewithIncrements(signalList.get(0).getTradingdate(), -1);
            Date checkSellDate = getDatewithIncrements(signalList.get(0).getTradingdate(), -1);
            boolean dealSettle = false;
            for (Tbsignal signal : signalList) {
                dealSettle = false;
                if (volume == 0 && capital > 0 && signal.getOrderside().equals("BUY")
                        && checkBuyDate.before(signal.getTradingdate())) {
//                logger.info("buySignal{}", signal.getTradingdate());
                    query.setParameter("stocksymbol", signal.getStocksymbol());
                    query.setParameter("buysignalid", signal.getSignalid());
                    query.setParameter("buyorderid", signal.getSignalid());
                    query.setParameter("buydate", signal.getTradingdate());
                    query.setParameter("buyprice", signal.getOrderprice());
                    volume = (int) (round(capital / (signal.getOrderprice().doubleValue())));
                    query.setParameter("tradevolume", volume);
                    capital = 0;
                    checkSellDate = getDatewithIncrements(signal.getTradingdate(), 3);

                }


                if (volume > 0 && signal.getTradingdate().after(checkSellDate) && signal.getOrderside().equals(DbSignal.SELL.getName())) {
//                logger.info("sellSignal {} checkDate {}", signal.getTradingdate(), checkBuyDate);
                    query.setParameter("sellsignalid", signal.getSignalid());
                    query.setParameter("sellorderid", signal.getSignalid());
                    query.setParameter("selldate", signal.getTradingdate());
                    query.setParameter("sellprice", signal.getOrderprice());
                    capital = volume * signal.getOrderprice().doubleValue();
                    volume = 0;
                    checkBuyDate = getDatewithIncrements(signal.getTradingdate(), 1);
                    dealSettle = true;
                }
                if (dealSettle) {
                    query.addBatch();
                    query.executeBatch();
                }
            }
        }
    }


    public Date getDatewithIncrements(Date date, int incrementDays) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, incrementDays);
        return c.getTime();
    }
}


