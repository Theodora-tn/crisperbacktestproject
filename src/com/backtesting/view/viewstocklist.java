package com.backtesting.view;

import com.shinobi.persistence.annotation.Entity;
import com.shinobi.persistence.annotation.Sequence;

@Entity(tableName = "viewstocklist")
public class viewstocklist {
    @Sequence
    private String symbolname;

    public String getSymbolname() {
        return symbolname;
    }

    public void setSymbolname(String symbolname) {
        this.symbolname = symbolname;
    }
}

