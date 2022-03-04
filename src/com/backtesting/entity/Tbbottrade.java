package com.backtesting.entity;

import com.shinobi.persistence.annotation.Entity;
import com.shinobi.persistence.annotation.Sequence;

import java.math.BigDecimal;
import java.util.Date;
@Entity(tableName = "tbbottrade")
public class Tbbottrade {
	
	@Sequence
	long tradeid;
	long botid;
	long buysignalid;
	long sellsignalid;
	String stocksymbol;
	long buyorderid;
	long sellorderid;
	BigDecimal buyprice;
	BigDecimal sellprice;
	Date buydate;
	Date selldate;
	int tradevolume;
	boolean stocksettlementstatus = false;
	boolean cashsettlementstatus = false;
	
	public long getTradeid() {
		return tradeid;
	}
	public long getBotid() {
		return botid;
	}
	public long getBuysignalid() {
		return buysignalid;
	}
	public long getSellsignalid() {
		return sellsignalid;
	}
	public String getStocksymbol() {
		return stocksymbol;
	}
	public long getBuyorderid() {
		return buyorderid;
	}
	public long getSellorderid() {
		return sellorderid;
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
	public boolean isStocksettlementstatus() {
		return stocksettlementstatus;
	}
	public boolean isCashsettlementstatus() {
		return cashsettlementstatus;
	}
	public void setTradeid(long tradeid) {
		this.tradeid = tradeid;
	}
	public void setBotid(long botid) {
		this.botid = botid;
	}
	public void setBuysignalid(long buysignalid) {
		this.buysignalid = buysignalid;
	}
	public void setSellsignalid(long sellsignalid) {
		this.sellsignalid = sellsignalid;
	}
	public void setStocksymbol(String stocksymbol) {
		this.stocksymbol = stocksymbol;
	}
	public void setBuyorderid(long buyorderid) {
		this.buyorderid = buyorderid;
	}
	public void setSellorderid(long sellorderid) {
		this.sellorderid = sellorderid;
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
	public void setStocksettlementstatus(boolean stocksettlementstatus) {
		this.stocksettlementstatus = stocksettlementstatus;
	}
	public void setCashsettlementstatus(boolean cashsettlementstatus) {
		this.cashsettlementstatus = cashsettlementstatus;
	}
	
}
