package com.falmarri.futures;

import java.util.HashMap;

public abstract class AbstractQuote {
	
	HashMap<String, String> values = new HashMap<String, String>();
	
	
	public void putValue(String key, String value){
		
		values.put(key, value);
		
	}
	
	public String getValue(String key){
		
		if (values.containsKey(key)){
			return values.get(key);
		}
		else{
			return null;
		}
	}

}
