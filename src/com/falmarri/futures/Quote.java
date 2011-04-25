package com.falmarri.futures;

import java.util.ArrayList;

public class Quote {
	
	boolean blank;
	
	private String change;
	private String low;
	private String open;
	private String high;
	private String value;
	private String index = "";
	private String region;
	public String time;
	
	
	

	
	public Quote(String region, ArrayList<String> values) throws QuoteValueException{
		
		if (values.size()!= 7){
			throw new QuoteValueException();
		}
		
		
		blank = false;
		this.region = region;
		this.index = values.get(0).replace("__and__", "&");
		this.value = values.get(1);
		this.change = values.get(2);
		this.open = values.get(3);
		this.high = values.get(4);
		this.low = values.get(5);
		this.time = values.get(6);
		
		//System.out.println("Stock index: " + index);
		
	}
		
	public Quote(String region) {
		// TODO Auto-generated constructor stub
		
		blank = true;
		
		this.region = region;
		
		
	}
	
	public Quote(String region, String index, String value, String updown,
			String change, String open, String high, String low, String time) {
		// TODO Auto-generated constructor stub
		
		this.region = region;
		this.index = index.trim();
		this.value = value;
		this.change = change;
		this.open = open;
		this.high = high;
		this.low = low;
		this.time = time;
	}

	@Override
	public boolean equals(Object j){
		
		if (this.index == (String) j) return true;
		
		return false;
	}

	public String getChange(){
		return change;
	}
	public String getLow(){
		return low;
	}
	public String getOpen(){
		return open;
	}
	public String getHigh(){
		return high;
	}
	public String getVal(){
		return value;
	}
	public String getIndex(){
		return index;
	}
	public String getRegion(){
		return region;
	}
	public String getTime(){
		return time;
	}
	

}

