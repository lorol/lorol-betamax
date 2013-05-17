package com.mridang.betamax.functions;

import java.io.IOException;

import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.util.Log;

/*
 * This class contains very simple helper-functions
 */
public class HelperFunctions {

    /* This is the URL to which the request is sent */
    public static String POST_URL = "https://www.%s/login";

    /*
     * This method makes a HTTP POST request used for logging in
     *
     * @param   strProvider  The provider used for logging in
     * @param   strUsername  The username used for logging in
     * @param   strPassword  The password used for logging in
     * @return               The parsed document from the POST request
     */
    public static Document getPage(String strProvider, String strUsername, String strPassword) throws IOException {

    	Connection.Response resResponse = Jsoup.connect(String.format(POST_URL, strProvider)).execute();

        Log.d("HelperFunctions", "Getting login token");
        Element eleHidden = resResponse.parse().select("input[type=hidden]").first();
        Log.d("HelperFunctions", "Token: " + eleHidden.attr("name"));
        
        Document docBody = Jsoup
                .connect(String.format(POST_URL, strProvider))
                .data(eleHidden.attr("name"), eleHidden.attr("value"), "login[username]", strUsername, "page_referrer", "login", "login[password]", strPassword)
                .method(Method.POST)
                .followRedirects(true)
                .cookie("PHPSESSID", resResponse.cookie("PHPSESSID"))
                .execute()
                .parse();
        
       return docBody;
        
    }

}