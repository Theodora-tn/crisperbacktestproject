package com.backtesting.view;

import com.shinobi.persistence.annotation.Entity;
import com.shinobi.persistence.annotation.Sequence;

import java.math.BigDecimal;
import java.util.Date;

@Entity(tableName = "viewgraph")
public class viewgraph {

    @Sequence
    String stock;
    BigDecimal buyprice;
    BigDecimal sellprice;
    Date buydate;
    Date selldate;
    int tradevolume;
    BigDecimal lastnav;
    BigDecimal profit;

    public String getStocksymbol() {
        return stock;
    }
    public BigDecimal getBuyprice() {
        return buyprice;
    }
    public BigDecimal getSellprice() {
        return sellprice;
    }
    public Date getBuydate() {
        return buydate;
    }
    public Date getSelldate() {
        return selldate;
    }
    public int getTradevolume() {
        return tradevolume;
    }

    public void setStocksymbol(String stocksymbol) {
        this.stock = stocksymbol;
    }
    public void setBuyprice(BigDecimal buyprice) {
        this.buyprice = buyprice;
    }
    public void setSellprice(BigDecimal sellprie) {
        this.sellprice = sellprie;
    }
    public void setBuydate(Date buydate) {
        this.buydate = buydate;
    }
    public void setSelldate(Date selldate) {
        this.selldate = selldate;
    }
    public void setTradevolume(int tradevolume) {
        this.tradevolume = tradevolume;
    }
    public BigDecimal getLastnav() {
        return lastnav;
    }

    public void setLastnav(BigDecimal lastnav) {
        this.lastnav = lastnav;
    }


}

