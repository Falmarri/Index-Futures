package com.falmarri.futures;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;

public class ConfigureFuturesWidget extends Activity {
	
	AlertDialog dialog;
	
	ArrayList<String> shown = new ArrayList<String>();
	
	boolean[] check = new boolean[Futures.indices.length];

	private int mAppWidgetId;
	
	
	@Override
	public void onCreate(Bundle onSavedInstanceState){
		this.setVisible(false);
		setResult(RESULT_CANCELED);
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		if (extras != null) {
		    mAppWidgetId = extras.getInt(
		            AppWidgetManager.EXTRA_APPWIDGET_ID, 
		            AppWidgetManager.INVALID_APPWIDGET_ID);
		}

		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		builder.setTitle("Indices")
		.setMultiChoiceItems(Futures.indices, null, new DialogInterface.OnMultiChoiceClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which, boolean isChecked) {
				// TODO Auto-generated method stub
				
				check[which] = isChecked;
			}
		})
		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
				finish();
				
				
			}
		})
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
				AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(ConfigureFuturesWidget.this);
				RemoteViews layout = new RemoteViews(ConfigureFuturesWidget.this.getPackageName(), R.id.widgetlayout);

				for (int i = 0; i < check.length; i++){
					
					if (check[i]){
						
						RemoteViews row = new RemoteViews(ConfigureFuturesWidget.this.getPackageName(), R.layout.quote_row);
						row.setTextViewText(R.id.TICKER, "Tick");
						row.setTextViewText(R.id.CHANGE, "-32.02");
						row.setTextViewText(R.id.VALUE, "3928.39");
						layout.addView(layout.getLayoutId(), row);
						
					}
					
				}

				appWidgetManager.updateAppWidget(mAppWidgetId, layout);
				
			}
		})
		.create().show();
		
		
	}
	
	public void addIndexToWidget(RemoteViews rv, String index){
		
		
	}

}
