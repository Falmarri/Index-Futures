package com.falmarri.futures;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.ClientProtocolException;

import android.util.Log;


public class Helper {
	
	public static final String URL = "http://www.bloomberg.com/markets/stocks/futures/";
	public static final String YRL = "http://finance.yahoo.com/d/quotes.csv?s={0}&f={1}";
	public static final String DEFAULT_DATA = "nsol1c";
	private static final Pattern p = Pattern.compile("<tr(?: class='(?:even|odd)')?>\\s*<td class='name'>([A-Z0-9&/ ]+)</td>\\s*<td class='date'>[a-zA-Z0-9 ]+</td>\\s*<td class='value'>\\s*([0-9\\.,]+)\\s*</td>\\s*<td\\s+class='value_change (up|down)'>\\s*([-+0-9\\.,]+)\\s*</td>\\s*<td class='value'>\\s*([0-9\\.,]+)\\s*</td>\\s*<td class='value'>\\s*([0-9\\.,]+)</td>\\s*<td class='value'>\\s*([0-9\\.,]+)\\s*</td>\\s*<td class='time last'>\\s*([0-9\\./,:]+)\\s*</td>\\s*</tr>");
    
	
	private static final String[] americas = { "DJIA INDEX", "S&P 500",
		"NASDAQ 100", "S&P/TSX 60 IX", "MEX BOLSA IDX", "BOVESPA INDEX"};
	
	private static final String[] europe = {"EURO STOXX 50", "FTSE 100 IDX", "CAC40 10 EURO", "DAX INDEX", "IBEX 35 INDX",
		"FTSE/MIB IDX", "AMSTERDAM IDX", "OMXS30 IND", "SWISS MKT IX"};
	
//	private static final String[] asia = {"NIKKEI 225 (OSE)",
//		"HANG SENG IDX", "SPI 200", "CSI 300 IDX FUTUR" };
	
	
	
	public static QuoteList getQuotes() throws ClientProtocolException, IOException{
		
		
		
		QuoteList quotes = new QuoteList();
		
		String response = ServerHttpRequest.doGet(URL);
		Matcher m = p.matcher(response);
		
		String r = "";
		while( m.find()) {
			
			String region = Arrays.asList(americas).contains(m.group(1).trim()) ? "Americas" : Arrays.asList(europe).contains(m.group(1)) ? "Europe" : "Asia";
			if (!region.equals(r)){
				quotes.add(new Quote(region));
				r = region;
			}
			quotes.add(new Quote(region,m.group(1), m.group(2), m.group(3), m.group(4), m.group(5), m.group(6), m.group(7), m.group(8)));
			
			
			
		}
		
		
		
		return quotes;
		
	}
	
	public static String getQuoteInfo(String tick, String tags){
		
		try {
			return ServerHttpRequest.doGet(String.format(Locale.US, YRL, tick, tags));
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static ArrayList<String> getQuoteInfo(String[] tick, String tags){
		
		ArrayList<String> res = new ArrayList<String>();
		
		try {
			for (String t: tick){
				res.add(ServerHttpRequest.doGet(String.format(Locale.US, YRL, t, tags)));
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		    
		return res;
	}
	
	
	public static String stripNonValidXMLCharacters(String in) {   
        StringBuffer out = new StringBuffer(); // Used to hold the output.
        char current; // Used to reference the current character.

        if (in == null || ("".equals(in))) return ""; // vacancy test.
        for (int i = 0; i < in.length(); i++) {
            current = in.charAt(i); // NOTE: No IndexOutOfBoundsException caught here; it should not happen.
            if ((current == 0x9) ||
                (current == 0xA) ||
                (current == 0xD) ||
                ((current >= 0x20) && (current <= 0xD7FF)) ||
                ((current >= 0xE000) && (current <= 0xFFFD)) ||
                ((current >= 0x10000) && (current <= 0x10FFFF))){
            	
            
                out.append(current);
            }
            else{
            	Log.e("Falmarri", "Not a valid character " + new Character(current));
            }
        }
        return out.toString();
    }    

	

}
