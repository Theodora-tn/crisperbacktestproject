package com.backtesting.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.shinobi.persistence.annotation.Entity;
import com.shinobi.persistence.annotation.Sequence;

@Entity(tableName = "tbtradingsignal")
public class Tbtradingsignal {

	@Sequence
	private long id;

	private String symbolname;
	private Date signalbuydate;
	private BigDecimal buyingprice;
	private BigDecimal currentcloseprice;
	private BigDecimal sellingprice;
	private Date signalsellingdate;

	public void setSellingprice(BigDecimal sellingprice) {
		this.sellingprice = sellingprice;
	}

	public BigDecimal getSellingprice() {
		return sellingprice;
	}

	public Date getSignalsellingdate() {
		return signalsellingdate;
	}

	public void setSignalsellingdate(Date signalsellingdate) {
		this.signalsellingdate = signalsellingdate;
	}

	public BigDecimal getCurrentcloseprice() {
		return currentcloseprice;
	}

	public void setCurrentcloseprice(BigDecimal currentcloseprice) {
		this.currentcloseprice = currentcloseprice;
	}

	public String getSymbolname() {
		return symbolname;
	}

	public void setSymbolname(String symbolname) {
		this.symbolname = symbolname;
	}

	public BigDecimal getBuyingprice() {
		return buyingprice;
	}

	public void setBuyingprice(BigDecimal buyingprice) {
		this.buyingprice = buyingprice;
	}

	public Date getSignalbuydate() {
		return signalbuydate;
	}

	public void setSignalbuydate(Date signalbuydate) {
		this.signalbuydate = signalbuydate;
	}

}
