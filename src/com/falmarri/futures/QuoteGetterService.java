package com.falmarri.futures;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;

import org.apache.http.client.ClientProtocolException;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.util.Log;
import android.widget.RemoteViews;

public class QuoteGetterService extends Service {

	private volatile QuoteList quotes = new QuoteList();
	private volatile QuoteList displayedQuotes;

	private final IBinder binder = new MyBinder();
	
	RemoteViews removeViews;
	AppWidgetManager appWidgetManager;
	ComponentName futuresWidget;

	private static final String[] indices = { "DJIA INDEX", "S&P 500",
			"NASDAQ 100", "S&P/TSX 60", "MEX BOLSA", "BOVESPA",
			"DJ EURO STOXX 50", "FTSE 100", "CAC 40 10 EURO", "DAX", "IBEX 35",
			"FTSE MIB", "AMSTERDAM", "OMXS30", "SWISS MARKET", "NIKKEI 225",
			"HANG SENG", "SPI 200" };

	public static final String BROADCAST_UPDATED = "com.falmarri.futures.QuoteUpdateEvent";

	public static final String BROADCAST_UPDATE_NOW = "com.falmarri.futures.UPDATE";

	private Intent broadcast = new Intent(BROADCAST_UPDATED);
	
	private Intent update = new Intent(BROADCAST_UPDATE_NOW);

	String displayedIndices;

	private boolean activityConnected = false;

	Time updated;

	// ArrayList<Messenger> mClients = new ArrayList<Messenger>();

	SharedPreferences pref;

	// boolean[] checkedIndices = new boolean[18];

	private BroadcastReceiver receiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context content, Intent intent) {
			// TODO Auto-generated method stub
			
			Log.i(Futures.TAG,"Received update request... Updating");
			
			QuoteGetterService.this.update();
			

		}
		
		
		
	};  
	
	OnSharedPreferenceChangeListener prefListener = new OnSharedPreferenceChangeListener() {

		@Override
		public void onSharedPreferenceChanged(
				SharedPreferences sharedPreferences, String key) {
			// TODO Auto-generated method stub

			if (Arrays.asList(indices).contains(key)) {

				if (displayedQuotes == null)
					update();

				
				putQuote(key, sharedPreferences.getBoolean(key, true));
		
				sendBroadcast(broadcast);

			}

		}

	};

	public void connect(boolean a) {

		activityConnected = a;

		if (a) {
			//update();
			pref.registerOnSharedPreferenceChangeListener(prefListener);
		} else {
			pref.unregisterOnSharedPreferenceChangeListener(prefListener);
		}
	}

	private void putQuote(String index, boolean in) {
		// TODO Auto-generated method stub

		if (quotes.take(index).blank)
			return;

		else if (!in) {

			// Start at 1 because displayedQuotes[0] is the 'Americas' separator
			for (int j = 1; j < displayedQuotes.size(); j++) {
				if (displayedQuotes.get(j).getIndex().equals(index)) {
					displayedQuotes.remove(j);
				}
			}

		} else {

			// Start at 1 because displayedQuotes[0] is the 'Americas' separator
			for (int j = 1; j < displayedQuotes.size(); j++) {

				// Wow this is a clusterfuck
				if (!displayedQuotes.get(j).blank
						&& !Arrays.asList(indices).subList(0,
								Arrays.asList(indices).indexOf(index))
								.contains(displayedQuotes.get(j).getIndex())) {

					displayedQuotes.add(j, quotes.take(index));
					return;

				}// else if(displayedQuotes.get(j).blank &&
					// !displayedQuotes.get(j-1).blank && )

				else if (displayedQuotes.get(j).blank
						&& /* displayedQuotes.get(j-1).blank && */displayedQuotes
								.get(j - 1).getRegion().equalsIgnoreCase(
										quotes.take(index).getRegion())) {

					displayedQuotes.add(j, quotes.take(index));
					return;

				}

			}

			displayedQuotes.add(quotes.take(index));

		}
		

	}

	@Override
	public void onCreate() {
		super.onCreate();

		Log.i(Futures.TAG, "Service coming up");

		// quotes = new ArrayList<Quote>();
		displayedQuotes = new QuoteList();

		pref = PreferenceManager.getDefaultSharedPreferences(this);

		// Must store as a string. Parse to boolean array when retrieving
		displayedIndices = (pref.getString("indices", null));

		
		
		
		
		
		
		update();
		
		this.removeViews = buildUpdate(this);
		
		this.futuresWidget = new ComponentName(this, FuturesWidget.class);
		
		this.appWidgetManager = AppWidgetManager.getInstance(this);
		
		registerReceiver(receiver,new IntentFilter(QuoteGetterService.BROADCAST_UPDATE_NOW));

		AlarmManager mgr = (AlarmManager) getApplicationContext()
				.getSystemService(Context.ALARM_SERVICE);

		PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(),
				0, update, 0);

		mgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock
				.elapsedRealtime(), AlarmManager.INTERVAL_HALF_HOUR, pi);

	}
	
	
	public RemoteViews buildUpdate(Context context){
		
		RemoteViews updateViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
		updateViews.setTextViewText(R.id.TICKER, "Text");
		updateViews.setTextViewText(R.id.CHANGE, "-32.01");
		updateViews.setTextViewText(R.id.VALUE, "3829.39");
		updateViews.setTextViewText(R.id.TIME, "10/32");
		
		Intent i = new Intent(context, Futures.class);
		PendingIntent pend = PendingIntent.getActivity(context, 0,i, 0);
		updateViews.setOnClickPendingIntent(R.id.widgetlayout, pend);
		
		return updateViews;
	}
	

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub

		// sendBroadcast(broadcast);
		return binder;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startID) {

		//update();

		return Service.START_STICKY;
	}

	public class MyBinder extends Binder {
		QuoteGetterService getService() {
			return QuoteGetterService.this;
		}
	}

	public ArrayList<Quote> getQuotes() {

		if (displayedQuotes == null) {

			synchronized (displayedQuotes) {
				try {
					displayedQuotes.wait();
					update();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					
				}
			}
		}

		return displayedQuotes;

	}

	public void setDisplayed() {
		System.out.println("Setting displayed");
		
		if (quotes == null){
			
			update();
		}
		
		synchronized (displayedQuotes) {
			
			displayedQuotes.clear();
			// displayedQuotes.add(quotes.get(0));
			// displayedQuotes.add(quotes.get(7));
			// displayedQuotes.add(quotes.get(17));
			
			if ( quotes == null){
				return;
			}
			
			displayedQuotes.addAll(quotes);
			displayedQuotes.notifyAll();
		
		for (Quote q : quotes) {
			if (!pref.getBoolean(q.getIndex(), true)) {

				putQuote(q.getIndex(), false);
			}
			// putQuote(q.getIndex(),pref.getBoolean(q.getIndex(), false));

		}
		}
		// System.out.println("Quote size" + displayedQuotes.size());
		sendBroadcast(broadcast);
		this.appWidgetManager.updateAppWidget(this.futuresWidget, this.removeViews);
	}

	public void update() {

		new Thread(new QuoteGetter()).start();

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i(Futures.TAG, "Service going down");
		pref.unregisterOnSharedPreferenceChangeListener(prefListener);

	}

	public class QuoteGetter implements Runnable {

		@Override
		public void run() {

			// TODO Auto-generated method stub

			Log.i(Futures.TAG, "Updating quotes");

			// ArrayList<Quote> quotes = new ArrayList<Quote>();
			try {

				quotes = Helper.getQuotes();
				// updated.setToNow();
				if (displayedQuotes == null) {

					displayedQuotes = new QuoteList();

				}
				setDisplayed();
				//sendBroadcast(broadcast);

			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

}
