package com.falmarri.futures;


import java.io.IOException;
import java.util.ArrayList;


import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;


public class GetFuturesParser extends DefaultHandler{
	
	private static final String URL = "http://www.bloomberg.com/markets/stocks/futures.html";
	
	private boolean inStockPortion = false;
	
	ArrayList<String> values;
	
	QuoteList quotes;
	
	String region = "";
	
	String tempVal;
	
	public QuoteList getQuotes(){
		return quotes;
	}
	
	@Override
    public void startDocument() throws SAXException {
         
    }

	@Override
    public void startElement(String namespaceURI, String localName,
              String qName, Attributes atts) throws SAXException 
    {
		

		if (localName.equalsIgnoreCase("div")){

			if(atts.getValue("id") != null && atts.getValue("id").equalsIgnoreCase("stock_data")){
				quotes = new QuoteList();

				inStockPortion  = true;
			}
			
		}
		
		if (inStockPortion && localName.equalsIgnoreCase("tr")){
			
			values = new ArrayList<String>(7);
		}
		 
		
    }
	
	

	@Override
    public void endElement(String uri, String localName, String qName) throws SAXException 
    {
		
		if(inStockPortion && localName.equalsIgnoreCase("h3")){
			
			if (tempVal != region){
				
				quotes.add(new Quote(tempVal));
				
			}
			
			region = tempVal;
			
		}

		if(inStockPortion && localName.equalsIgnoreCase("td")){

			values.add(new String(tempVal));
			
		}
		
		if(inStockPortion && localName.equalsIgnoreCase("tr") && values.size() > 0){
			
			try {
				//System.out.println("Adding a quote");
				quotes.add(new Quote(new String(region),values));
			} catch (QuoteValueException e) {
				// TODO Auto-generated catch block
				
				Log.e("GetFutures","Error parsing quotes", e);
				
				e.printStackTrace();
			}
			
		}
		
		if(inStockPortion && localName.equalsIgnoreCase("div") && quotes.size() > 0){
			
			inStockPortion = false;
			
		}
		
		
    }
	
	 @Override
	 public void characters(char[] ch, int start, int length) throws SAXException {
		 
		 tempVal = new String(ch, start, length).replace("__and__", "&");
		 
		 tempVal = tempVal.trim();
		 
	 }
	
	
}
