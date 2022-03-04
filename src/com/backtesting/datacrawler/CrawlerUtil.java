package com.backtesting.datacrawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class CrawlerUtil {
	
	private static final Logger logger = LogManager.getLogger();
	public List<String> getStockSymbolList(String urlPath) throws IOException {
//		"https://price.tpbs.com.vn/api/StockBoardApi/getStockList"
		List<String> result = new ArrayList<>();

		URL url = new URL(urlPath);

		
		String data = "{\n \"boardName\": \"VN100\",\n }";
		
		byte[] postDataBytes = data.toString().getBytes("UTF-8");

		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
		conn.setDoOutput(true);
		conn.getOutputStream().write(postDataBytes);

		BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

		StringBuilder sb = new StringBuilder();
		for (int c; (c = in.read()) >= 0;)
			sb.append((char) c);
		String response = sb.toString();

		JsonParser parser = new JsonParser();
		JsonObject o = parser.parse(response).getAsJsonObject();
		String[] array = o.get("content").getAsString().split("\\W+");

		for (String ele : array) {
			result.add(ele);
		}
		logger.info("Finish stockSymbol Crawling Process at" + urlPath);
		return result;
	}

}
