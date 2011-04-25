package com.falmarri.futures;

import java.util.ArrayList;

import android.util.Log;

public class QuoteList extends ArrayList<Quote>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1497987032427766089L;

	
	
	public Quote take(String index){
		
		for (int j = 0; j < this.size(); j++){
			
			if(this.get(j).getIndex().equals(index)){
				
				return this.get(j);
			}
			
		}
		
	Log.e("Falmarri", "Taking quote " + index);
	throw new ArrayIndexOutOfBoundsException("Couldn't find " + index);
	
	}
}
