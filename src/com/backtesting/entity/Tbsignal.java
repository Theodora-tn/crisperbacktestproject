package com.backtesting.entity;

import com.shinobi.persistence.annotation.Entity;
import com.shinobi.persistence.annotation.Sequence;

import java.math.BigDecimal;
import java.util.Date;

@Entity(tableName = "tbsignal")
public class Tbsignal {

        @Sequence
        long signalid;
        long botid;
        String stocksymbol;
        Date tradingdate;
        BigDecimal orderprice;
        String orderside;
        Date signaldate;

        public long getSignalid() {
            return signalid;
        }
        public long getBotid() {
            return botid;
        }
        public String getStocksymbol() {
            return stocksymbol;
        }
        public Date getTradingdate() {
            return tradingdate;
        }
        public BigDecimal getOrderprice() {
            return orderprice;
        }
        public String getOrderside() {
            return orderside;
        }
        public Date getSignaldate() {
            return signaldate;
        }
        public void setSignalid(long signalid) {
            this.signalid = signalid;
        }
        public void setBotid(long botid) {
            this.botid = botid;
        }
        public void setStocksymbol(String stocksymbol) {
            this.stocksymbol = stocksymbol;
        }
        public void setTradingdate(Date tradingdate) {
            this.tradingdate = tradingdate;
        }
        public void setOrderprice(BigDecimal orderprice) {
            this.orderprice = orderprice;
        }
        public void setOrderside(String orderside) {
            this.orderside = orderside;
        }
        public void setSignaldate(Date signaldate) {
            this.signaldate = signaldate;
        }

    }


