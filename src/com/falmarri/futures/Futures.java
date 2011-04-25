package com.falmarri.futures;

import java.util.ArrayList;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Futures extends ListActivity {

	private static final int MENU_REFRESH = Menu.FIRST;
	private static final int DIALOG_SELECT_INDICES = 2;
	private static final int SELECT_INDICES = 3;

	public static final String TAG = "Futures";

	Context ctx;

	String which = "downloaded";

	String region = "";

	boolean bool = true;

	boolean reload = false;

	QuoteAdapter adapter;

	SharedPreferences pref;

	private final ArrayList<Quote> quotes = new ArrayList<Quote>();

	private QuoteGetterService quoteBinder;

	// Indices don't change
	public static final String[] indices = { "DJIA INDEX", "S&P 500",
			"NASDAQ 100", "S&P/TSX 60", "MEX BOLSA", "BOVESPA",
			"DJ EURO STOXX 50", "FTSE 100", "CAC 40 10 EURO", "DAX", "IBEX 35",
			"FTSE MIB", "AMSTERDAM", "OMXS30", "SWISS MARKET", "NIKKEI 225",
			"HANG SENG", "SPI 200" };

	String displayedIndices;

	boolean[] checkedIndices = new boolean[18];

	private boolean mIsBound;

	ArrayList<String> display;

	// private ListView lv;

	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context content, Intent intent) {
			// TODO Auto-generated method stub

			getFreshQuotes();

		}

	};

	private void getFreshQuotes() {
		if (mIsBound && quoteBinder != null) {

			Log.d(TAG, "Received new quotes");
			quotes.clear();
			quotes.addAll(quoteBinder.getQuotes());
			adapter.notifyDataSetChanged();
		}
	}

	private ServiceConnection mConnection = new ServiceConnection() {

	    
		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			// Get the reference to the service
			quoteBinder = ((QuoteGetterService.MyBinder) service).getService();
			Log.i(Futures.TAG, "QuoteGetterService bound");
			// Bound flag
			mIsBound = true;
			// Tell the service we are bound to it
			quoteBinder.connect(true);

			Futures.this.getFreshQuotes();

		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mIsBound = false;
			// Actually unbind the service
			quoteBinder = null;
		}

	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/*
		 * 
		 * Starts the service. Doesn't bind to it. We just need to start it
		 * explicitly to make sure it stays beyond the lifetime of this activity
		 * (for future use when there's a desktop widget)
		 */
		
		/*
		new AlertDialog.Builder(this).setMessage("Bloomberg's source is unparasble with the current XML parser I'm using. Give me time to use a true html parser")
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
				finish();
				
			}
		})
		.create().show();
		*/
		

		startService(new Intent(this, QuoteGetterService.class));

		pref = PreferenceManager.getDefaultSharedPreferences(this);

		for (int i = 0; i < indices.length && i < checkedIndices.length; i++) {

			checkedIndices[i] = pref.getBoolean(indices[i], true);

		}


		adapter = new QuoteAdapter(this, R.layout.quote_row, quotes);
		setListAdapter(adapter);

		
		
	}

	private void doBindService() {

		bindService(new Intent(this, QuoteGetterService.class), mConnection,
				Context.BIND_AUTO_CREATE);
		registerReceiver(receiver, new IntentFilter(
				QuoteGetterService.BROADCAST_UPDATED));

	}

	private void doUnbindService() {

		if (mIsBound) {
			quoteBinder.connect(false);
			unregisterReceiver(receiver);
			Log.d(TAG, "Unbinding service");
			unbindService(mConnection);

		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, MENU_REFRESH, 0, "Refresh");
		menu.add(0, DIALOG_SELECT_INDICES, 0, "Select Indices to Show");

		return true;

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		/*
		 * 
		 * Force the service to get new data
		 */
		case (MENU_REFRESH):
			quoteBinder.update();
			break;

		/*
		 * 
		 * Dialog to select which indices to display
		 */
		case (DIALOG_SELECT_INDICES):

			showPickerDialog();
			break;

		}

		return false;

	}

	private void showPickerDialog() {
		// TODO Auto-generated method stub

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		AlertDialog dialog = builder
				.setMultiChoiceItems(indices, checkedIndices,
						new DialogInterface.OnMultiChoiceClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which, boolean isChecked) {
								// TODO Auto-generated method stub

								final SharedPreferences.Editor editor = pref
										.edit();
						
								editor.putBoolean(indices[which], isChecked);
								editor.commit();

							}

						}).setCancelable(true)

				.setTitle("Indices").create();

		dialog.show();

	}

	@Override
	public void onResume() {
		super.onResume();

		doBindService();

	}

	@Override
	public void onPause() {
		super.onPause();

		doUnbindService();

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		/*
		 * SharedPreferences.Editor editor = pref.edit();
		 * 
		 * for (int j = 0; j < checkedIndices.length; j++){
		 * editor.putBoolean(indices[j], checkedIndices[j]);
		 * 
		 * 
		 * } editor.commit();
		 */
	}

	private class QuoteAdapter extends ArrayAdapter<Quote> implements OnSharedPreferenceChangeListener {

		ArrayList<Quote> items;
		int resource;

		public QuoteAdapter(Context context, int textViewResourceId,
				ArrayList<Quote> items) {
			super(context, textViewResourceId, items);

			this.items = items;
			this.resource = textViewResourceId;

		}

		@Override
		public boolean isEnabled(int position) {

			if (items.get(position).blank)
				return false;

			else
				return true;

		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View v = convertView;

			// if (v == null){

			int bg = (position % 2 == 0) ? 0xFFFFFFFF : 0xFFDCDCDC;

			Quote q = items.get(position);

			if (!q.blank) {
				boolean up = q.getChange().contains("-") ? false : true;
				int color = up ? 0xFF328A00 : 0xFFF10000;

				LayoutInflater vi = (LayoutInflater) getContext()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

				v = vi.inflate(resource, null);

				LinearLayout back = (LinearLayout) v
						.findViewById(R.id.BACKGROUND);
				TextView index = (TextView) v.findViewById(R.id.TICKER);
				TextView value = (TextView) v.findViewById(R.id.VALUE);
				TextView change = (TextView) v.findViewById(R.id.CHANGE);
				TextView updated = (TextView) v.findViewById(R.id.TIME);

				index.setText(q.getIndex());

				back.setBackgroundColor(bg);
				value.setText(q.getVal());
				change.setText(q.getChange());
				updated.setText(q.getTime());
				value.setTextColor(color);
				change.setTextColor(color);

			} else {

				LayoutInflater vi = (LayoutInflater) getContext()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.header_row, null);

				TextView h = (TextView) v.findViewById(R.id.HEADER);
				h.setText(q.getRegion());

				v.setClickable(false);

			}

			return v;

		}

		@Override
		public void onSharedPreferenceChanged(
				SharedPreferences sharedPreferences, String key) {
			// TODO Auto-generated method stub
			
		}

	}

}