
package com.backtesting.main.chart;

import java.awt.Color;
import java.awt.Dimension;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.backtesting.config.DbEnum;
import com.backtesting.entity.Tbbottrade;
import com.backtesting.view.viewgraph;
import com.backtesting.view.viewstocklist;
import com.shinobi.persistence.AbstractDbService;
import com.shinobi.persistence.EntityManager;
import com.shinobi.persistence.EntityManagerGroup;
import com.shinobiutil.exception.SnbException;
import com.ta4jTraining.candle.DarkCloudCoverPattern;
import com.ta4jTraining.candle.PiercingPattern;
import com.backtesting.datacrawler.SynPugna;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.ta4j.core.*;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.Num;
import org.ta4j.core.rules.BooleanIndicatorRule;

/**
 * This class builds a graphical chart showing the buy/sell signals of a
 * strategy.
 */
public class BuyAndSellSignalsToChart extends AbstractDbService {
    private static String stockSymbol;
    public BuyAndSellSignalsToChart(EntityManagerGroup emg, String stockSymbol) {
        super(emg);
        this.stockSymbol= stockSymbol;

    }

    /**
     * Builds a JFreeChart time series from a Ta4j bar series and an indicator.
     *
     * @param barSeries the ta4j bar series
     * @param indicator the indicator
     * @param name      the name of the chart time series
     * @return the JFreeChart time series
     */
    private static org.jfree.data.time.TimeSeries buildChartTimeSeries(BarSeries barSeries, Indicator<Num> indicator,
                                                                       String name) {
        org.jfree.data.time.TimeSeries chartTimeSeries = new org.jfree.data.time.TimeSeries(name);
        for (int i = 0; i < barSeries.getBarCount(); i++) {
            Bar bar = barSeries.getBar(i);
            chartTimeSeries.add(new Minute(Date.from(bar.getEndTime().toInstant())),
                    indicator.getValue(i).doubleValue());
        }
        return chartTimeSeries;
    }

    private void addBuySellSignals(BarSeries series, XYPlot plot, String stockSymbol) throws SnbException {
        EntityManager em = emg.getEntityManager(DbEnum.TRAINING.getName());
        List<Tbbottrade> signalList = em.createQuery("SELECT * FROM tbbottrade WHERE stocksymbol = :stocksymbol ", Tbbottrade.class)
                .setParameter("stocksymbol", stockSymbol).getResultList();

        // Adding markers to plot
        for (Tbbottrade position : signalList) {
            // Buy signal
            double buySignalBarTime = new Minute(position.getBuydate()).getFirstMillisecond();
            Marker buyMarker = new ValueMarker(buySignalBarTime);
            buyMarker.setPaint(Color.GREEN);
            buyMarker.setLabel("B");
            plot.addDomainMarker(buyMarker);
            // Sell signal
            double sellSignalBarTime = new Minute(position.getSelldate()).getFirstMillisecond();
            Marker sellMarker = new ValueMarker(sellSignalBarTime);
            sellMarker.setPaint(Color.RED);
            sellMarker.setLabel("S");
            plot.addDomainMarker(sellMarker);
        }
    }
    private void addCustomBuySellSignals(BarSeries series, XYPlot plot, String stockSymbol) throws SnbException {
        EntityManager em = emg.getEntityManager(DbEnum.TRAINING.getName());
        List<viewgraph> signalList = getDatafromView();

        // Adding markers to plot
        for (viewgraph position : signalList) {
            // Buy signal
            double buySignalBarTime = new Minute(position.getBuydate()).getFirstMillisecond();
            Marker buyMarker = new ValueMarker(buySignalBarTime);
            buyMarker.setPaint(Color.GREEN);
            buyMarker.setLabel("B");
            plot.addDomainMarker(buyMarker);
            // Sell signal
            double sellSignalBarTime = new Minute(position.getSelldate()).getFirstMillisecond();
            Marker sellMarker = new ValueMarker(sellSignalBarTime);
            sellMarker.setPaint(Color.RED);
            sellMarker.setLabel("S");
            plot.addDomainMarker(sellMarker);
        }
    }

    /**
     * Displays a chart in a frame.
     *
     * @param chart the chart to be displayed
     */
    private static void displayChart(JFreeChart chart) {
        // Chart panel
        ChartPanel panel = new ChartPanel(chart);
        panel.setFillZoomRectangle(true);
        panel.setMouseWheelEnabled(false);
        panel.setPreferredSize(new Dimension(1024, 800));
        // Application frame
        ApplicationFrame frame = new ApplicationFrame(stockSymbol);
        frame.setContentPane(panel);
        frame.pack();
        RefineryUtilities.centerFrameOnScreen(frame);
        frame.setVisible(true);
    }


    @Override
    public void process() throws SnbException, IOException, ParseException {
        // Getting the bar series
        SynPugna getPugna = new SynPugna(stockSymbol);
        BarSeries series = getPugna.synPugna();
        
        /*
         * Building chart datasets
         */
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(buildChartTimeSeries(series, new ClosePriceIndicator(series), "closePriceStockChart"));

        /*
         * Creating the chart
         */
        JFreeChart chart = ChartFactory.createTimeSeriesChart(stockSymbol, // title
                "Date", // x-axis label
                "Price", // y-axis label
                dataset, // data
                true, // create legend?
                true, // generate tooltips?
                false // generate URLs?
        );
        XYPlot plot = (XYPlot) chart.getPlot();
        DateAxis axis = (DateAxis) plot.getDomainAxis();
        axis.setDateFormatOverride(new SimpleDateFormat("yyyy-MM-dd"));

        /*
         * Running the strategy and adding the buy and sell signals to plot
         */
//        addBuySellSignals(series, plot, stockSymbol);
        addCustomBuySellSignals(series, plot, stockSymbol);

        /*
         * Displaying the chart
         */
        displayChart(chart);
    }
    public List<viewgraph> getDatafromView() throws SnbException {
        EntityManager em = emg.getEntityManager(DbEnum.TRAINING.getName());
        List<viewgraph> graph = em
                .createQuery("SELECT * FROM graphview ", viewgraph.class).getResultList();

        return graph;
    }
}

