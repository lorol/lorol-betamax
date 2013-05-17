package com.mridang.betamax.services;

import java.io.IOException;

import org.jsoup.HttpStatusException;
import org.jsoup.nodes.Document;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;

import com.bugsense.trace.BugSenseHandler;
import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.android.apps.dashclock.api.ExtensionData;
import com.mridang.betamax.R;
import com.mridang.betamax.functions.HelperFunctions;

/*
 * This class the is the class that provides the service to the extension
 */
public class BetamaxWidget extends DashClockExtension {

	/*
	 * @see com.google.android.apps.dashclock.api.DashClockExtension#onCreate()
	 */
	public void onCreate() {

		super.onCreate();
		Log.d("BetamaxWidget", "Created");
		BugSenseHandler.initAndStartSession(this, "61f91e82");

	}

	/*
	 * @see com.google.android.apps.dashclock.api.DashClockExtension#onUpdateData(int)
	 */
	@Override
	protected void onUpdateData(int arg0) {

		SharedPreferences speSettings = PreferenceManager.getDefaultSharedPreferences(this);
		ExtensionData edtInformation = new ExtensionData();
		edtInformation.visible(false);
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        
        Log.d("BetamaxWidget", "Running");
        Log.d("BetamaxWidget", "Checking if we have internet connectivity");
        
        NetworkInfo nifNetwork = connectivityManager.getActiveNetworkInfo();
        if (nifNetwork != null && nifNetwork.isConnected()) {
        
	        Log.d("BetamaxWidget", "We are connected to the internet");
			Log.d("BetamaxWidget", "Checking if we have a username");
	
			String strProvider = speSettings.getString("provider", "");
			if (!strProvider.isEmpty()) {
	
				Log.v("BetamaxWidget", "We have a provider defined");
				Log.d("BetamaxWidget", "Checking if we have a username");
	
				String strUsername = speSettings.getString("username", "");
				if (!strUsername.isEmpty()) {
	
					Log.v("BetamaxWidget", "We have a username defined");
					Log.d("BetamaxWidget", "Checking if we have a password");
	
					String strPassword = speSettings.getString("password", "");
					if (!strUsername.isEmpty()) {
	
						Log.v("BetamaxWidget", "We have a password defined");
						Log.d("BetamaxWidget", "Logging in to the website");
	
						try {
	
							Log.d("BetamaxWidget", "Posting credentials and fetching page");
							Log.v("BetamaxWidget", "POSTing to: " + String.format(HelperFunctions.POST_URL, strProvider));
	
							Document docPage = HelperFunctions.getPage(strProvider, strUsername, strPassword);
							if (docPage.select(":contains(" + strUsername + ")").size() > 0) {
	
								Log.v("BetamaxWidget", "Username found. Credentials were correct");
								Log.d("BetamaxWidget", "Scraping information from page");
	
								String strBalance = docPage.select("span:containsOwn(€)").text();
	
								try {
	
									Log.d("BetamaxWidget", "Publishing update");
									edtInformation.status(String.format(getString(R.string.status), strBalance));
									edtInformation.expandedBody(String.format(getString(R.string.message), strProvider));
									edtInformation.visible(true);
									Log.d("BetamaxWidget", "Published");
	
								} catch (Exception e) {
									BugSenseHandler.sendException(e);
								}
	
							} else {
								Log.w("BetamaxWidget", "Username not found. Credentials are incorrect");
							}
	
						} catch (IOException e) {
		                    Log.e("BetamaxWidget", "Unable to connect to website", e);
		                    
		                    if (e instanceof HttpStatusException) {

		                    	if (((HttpStatusException) e).getStatusCode() >= 400 && ((HttpStatusException) e).getStatusCode() <= 599) {
			                		BugSenseHandler.sendException(e);
		                    	}

		                    }

						}
	
					} else {
						Log.v("BetamaxWidget", "No password given");
					}
	
				} else {
					Log.v("BetamaxWidget", "No username given");
				}
	
			} else {
				Log.v("BetamaxWidget", "No provider given");
			}
			

        } else {
        	Log.v("BetamaxWidget", "Not connected to the internet");
        }

		edtInformation.icon(R.drawable.ic_dashclock);
		publishUpdate(edtInformation);
		Log.d("BetamaxWidget", "Done");

	}

	/*
	 * @see com.google.android.apps.dashclock.api.DashClockExtension#onDestroy()
	 */
	public void onDestroy() {

		super.onDestroy();
		Log.d("BetamaxWidget", "Destroyed");
		BugSenseHandler.closeSession(this);

	}

}