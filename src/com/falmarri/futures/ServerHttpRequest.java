package com.falmarri.futures;
/** Author: Ivan Lizarraga
 *  Date: 03-12-10
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

/**
 * Provides static methods enabling either HTTP POST or GET.
 */
public class ServerHttpRequest {

	/**
	 * Performs an HTTP POST operation for the given URI. Will be used to update
	 * the phone's location to the server.
	 *
	 * @param url The String representation of the server resource.
	 * @param kvPairs The key-value pairs for the request body.
	 * @return Server response in String format.
	 * @throws ClientProtocolException if HTTP POST failed
	 * @throws IOException if response stream could not be processed.
	 */
	public static String doPost(String  url, Map<String, String> kvPairs)
    throws ClientProtocolException, IOException {
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(url);

		//Read in input data
		if (kvPairs != null && kvPairs.isEmpty() == false) {
		    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
		              kvPairs.size());
		    String k, v;
		    Iterator<String> itKeys = kvPairs.keySet().iterator();
		    while (itKeys.hasNext()) {
		         k = itKeys.next();
		         v = kvPairs.get(k);
		         nameValuePairs.add(new BasicNameValuePair(k, v));
		    }
		    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		}
		//Execute POST
		HttpResponse httpResponse = httpclient.execute(httppost);
		return responseToString(httpResponse);
	}

	/**
	 * Performs an HTTP GET operation for the given URI. Will be used to obtain user
	 * matches from the server.
	 *
	 * @param url The String representation of the server resource.
	 * @return Server response in String format.
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static String doGet(String url)throws ClientProtocolException, IOException{
		HttpGet getRequest = new HttpGet(url);
		HttpClient client = new DefaultHttpClient();
		HttpResponse response = client.execute(getRequest);

		return responseToString(response);
	}

	//Utility method that returns a string representation of the HttpResponce
	//object content.
	private static String responseToString(HttpResponse httpResponse)
	throws IllegalStateException, IOException{
		StringBuilder response = new StringBuilder();
		String aLine = new String();

		//InputStream to String conversion
		InputStream is = httpResponse.getEntity().getContent();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));

		while( (aLine = reader.readLine()) != null){

				response.append(aLine);
			
			}
		reader.close();
		
		return response.toString();
	}
}
