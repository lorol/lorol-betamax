package com.lorol.betamax.services;

import java.io.IOException;

import org.jsoup.HttpStatusException;
import org.jsoup.nodes.Document;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;

import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.android.apps.dashclock.api.ExtensionData;
import com.lorol.betamax.R;
import com.lorol.betamax.functions.HelperFunctions;

//import android.util.Log;

/*
 * This class the is the class that provides the service to the extension
 */
public class BetamaxWidget extends DashClockExtension {

	public static String strBalance = "---";
	
	/*
	 * @see com.google.android.apps.dashclock.api.DashClockExtension#onCreate()
	 */
	public void onCreate() {

		super.onCreate();
//		Log.d("BetamaxWidget", "Created");
//		BugSenseHandler.initAndStartSession(this, "");

	}
	
	/*
	 * @see com.google.android.apps.dashclock.api.DashClockExtension#onInitialize(boolean)
	 */	
//	@Override
//	protected void onInitialize(boolean isReconnect) {
//		super.onInitialize(isReconnect); 
//		if (!isReconnect) {
//			setUpdateWhenScreenOn(true);
//			strBalance = "-----";
//		}
//	}
	
	/*
	 * @see com.google.android.apps.dashclock.api.DashClockExtension#onUpdateData(int)
	 */
	@Override
	protected void onUpdateData(int arg0) {
		
		boolean allGood = false;
		
		SharedPreferences speSettings = PreferenceManager.getDefaultSharedPreferences(this);
		
		boolean chkOnscreen = speSettings.getBoolean("scron", false);
		if (chkOnscreen) setUpdateWhenScreenOn(true);
		else setUpdateWhenScreenOn(false);
		
		ExtensionData edtInformation = new ExtensionData();
		edtInformation.visible(false);
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        
//       Log.d("BetamaxWidget", "Running");
//       Log.d("BetamaxWidget", "Checking if we have Internet connectivity");
        
        NetworkInfo nifNetwork = connectivityManager.getActiveNetworkInfo();
        if (nifNetwork != null && nifNetwork.isConnected()) {
        
//	        Log.d("BetamaxWidget", "We are connected to the Internet");
//			Log.d("BetamaxWidget", "Checking if we have a username");
	
			String strProvider = speSettings.getString("provider", "");
			if (!strProvider.isEmpty()) {
	
//				Log.v("BetamaxWidget", "We have a provider defined");
//				Log.d("BetamaxWidget", "Checking if we have a username");
	
				String strUsername = speSettings.getString("username", "");
				if (!strUsername.isEmpty()) {
	
//					Log.v("BetamaxWidget", "We have a username defined");
//					Log.d("BetamaxWidget", "Checking if we have a password");
	
					String strPassword = speSettings.getString("password", "");
					if (!strPassword.isEmpty()) {
	
//						Log.v("BetamaxWidget", "We have a password defined");
//						Log.d("BetamaxWidget", "Logging in to the website");
	
						try {
	
//							Log.d("BetamaxWidget", "Posting credentials and fetching page");
//							Log.v("BetamaxWidget", "POSTing to: " + String.format(HelperFunctions.POST_URL, strProvider));
	
							Document docPage = HelperFunctions.getPage(strProvider, strUsername, strPassword);
							if (docPage.select(":contains(" + strUsername + ")").size() > 0) {
	
//								Log.v("BetamaxWidget", "Username found. Credentials were correct");
//								Log.d("BetamaxWidget", "Scraping information from page");
	
								String strBalance0 = docPage.select("span:containsOwn(€)").text();
								if (strBalance0.isEmpty()){
									strBalance0 = docPage.select("span:containsOwn($)").text();
								}
	
								try {
	
//									Log.d("BetamaxWidget", "Publishing update");
									edtInformation.status(String.format(getString(R.string.status), strBalance0));
									edtInformation.expandedBody(String.format(getString(R.string.message), strProvider));
									edtInformation.visible(true);
//									Log.d("BetamaxWidget", "Published");
									strBalance = strBalance0;
									allGood = true;
								} catch (Exception e) {
//									BugSenseHandler.sendException(e);
								}
	
							} else {
//								Log.w("BetamaxWidget", "Username not found. Credentials are incorrect");
							}
	
						} catch (IOException e) {
//		                    Log.e("BetamaxWidget", "Unable to connect to website", e);
		                    
		                    if (e instanceof HttpStatusException) {

		                    	if (((HttpStatusException) e).getStatusCode() >= 400 && ((HttpStatusException) e).getStatusCode() <= 599) {
//			                		BugSenseHandler.sendException(e);
		                    	}

		                    }

						}
	
					} else {
//						Log.v("BetamaxWidget", "No password given");
					}
	
				} else {
//					Log.v("BetamaxWidget", "No username given");
				}
	
			} else {
//				Log.v("BetamaxWidget", "No provider given");
			}
			
        } else {
//        	Log.v("BetamaxWidget", "Not connected to the Internet");
        }	
		
        boolean showOn = speSettings.getBoolean("showon", true);
        String expBody = speSettings.getString("provider", getString(R.string.all_title));
        String clickLink = getString(R.string.all_click);
        
        if (expBody != getString(R.string.all_title)){
        	clickLink = "https://www." + expBody + "/login";
        }
        
        if ((allGood)||(showOn)){
			if (!allGood){
				edtInformation.status(String.format(getString(R.string.status), strBalance + "*"));
				edtInformation.visible(true);
			}		
    		edtInformation.icon(R.drawable.ic_dashclock);
    		edtInformation.expandedBody(String.format(getString(R.string.message), expBody));
//    		edtInformation.clickIntent(new Intent(Intent.ACTION_VIEW, Uri.parse(clickLink)));   
    		edtInformation.clickIntent(null); 
        } else {
    		edtInformation.clean();
    		edtInformation.visible(false);
    		strBalance = "***";
        }
        
        publishUpdate(edtInformation);
		//		Log.d("BetamaxWidget", "Done");
	}

	/*
	 * @see com.google.android.apps.dashclock.api.DashClockExtension#onDestroy()
	 */
	public void onDestroy() {

		super.onDestroy();
//		Log.d("BetamaxWidget", "Destroyed");
//		BugSenseHandler.closeSession(this);

	}

}