/**
 * @Class JSONParser
 * @Version 1.0.0
 * @Author Justin Burch
 * @Author Brandon Chambers
 *
 * This class provides a means to transform a JSON String returned from a PHP service call into
 * a JSONObject containing a success/fail flag and a returned message, usually indicating
 * success or what type of failure occured.
 */
package edu.uw.tcss450.team1.cosmic_kids_game.HelperCode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.CloseableHttpResponse;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.client.utils.URLEncodedUtils;
import cz.msebera.android.httpclient.impl.client.CloseableHttpClient;
import cz.msebera.android.httpclient.impl.client.HttpClients;

/**
 * Class to handle turning php JSON output into an object with a success/failure & message.
 */
public class JSONParser {

    static InputStream is = null;
    static JSONObject jsonObject = null;
    static String jsonData = "";


    //empty constructor
    public JSONParser() {}

    /**
     * Unused method to get JSON from a URL - URLs can pass JSON info.
     * @param url URL to parse for object
     * @return Object created
     */
    public JSONObject getJSONFromUrl(String url) {

        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            //TODO: should it be "HttpPost" rather than "HttpGet"?
            HttpGet httpGet = new HttpGet(url);
            CloseableHttpResponse response = httpClient.execute(httpGet);
            HttpEntity httpEntity = response.getEntity();
            is = httpEntity.getContent();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }

        //Store retrieved content into StringBuilder-->String
        try {
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line;
            while((line =reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            jsonData = sb.toString();
        } catch (Exception e) {
            Log.e("BufferReader Error", "Error converting the json data " + e.toString());
        }

        //Attempt parsing the String jsonData into a JSON Object
        try {
            jsonObject = new JSONObject(jsonData);
        } catch (JSONException e) {
            Log.e("JSONParser", "Error parsing the data " + e.toString());
        }

        return jsonObject;
    }

    /**
     * Make a request to php server and parse/retrieve a JSON Object, which is returned.
     * @param url URL to attempt connection
     * @param method PHP Method
     * @param params Parameters passed to PHP Method
     * @return JSON Object of the returned response from PHP call
     */
    public JSONObject makeHttpRequest(String url, String method, List<NameValuePair> params) {
        try {
            if(method == "POST") {
                CloseableHttpClient httpClient = HttpClients.createDefault();
                //TODO: HttpPost or HttpGet?
                HttpPost httpPost = new HttpPost(url);
                httpPost.setEntity(new UrlEncodedFormEntity(params));

                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();
            } else if(method == "GET") {
                CloseableHttpClient httpClient = HttpClients.createDefault();
                String paramString = URLEncodedUtils.format(params, "utf-8");
                url += "?" + paramString;
                HttpGet httpGet = new HttpGet(url);

                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try{
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            jsonData = sb.toString();
        } catch (Exception e) {
            Log.e("BufferReader Error", "Error converting json data " + e.toString());
        }

        try {
            jsonObject = new JSONObject(jsonData);
        } catch (JSONException e) {
            Log.e("JSON Parser Error", "Error parsing the data " + e.toString());
        }

        return jsonObject;
    }
}