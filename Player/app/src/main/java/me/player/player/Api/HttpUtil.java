package me.player.player.Api;
import android.util.Log;

import org.apache.http.HttpStatus;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

import me.player.player.Constants.TimeMeasurements;
import me.player.player.Constants.Names;

/**
 * Created by stevenstewart on 9/6/14.
 */
public class HttpUtil
{
    public static ResponseObject makeHttpRequest(String methodName, String requestType, ArrayList<KeyValuePair> headers, ArrayList<KeyValuePair> parameters)
    {
        ResponseObject responseObject = new ResponseObject(-1,null);
        HttpsURLConnection httpsURLConnection = HttpUtil.getHttpsUrlConnection(methodName,requestType);
        if(headers != null)
            setHeaders(httpsURLConnection, headers);
        if(parameters != null)
            sendParameters(httpsURLConnection, parameters);
        int responseCode = HttpUtil.getResponseCode(httpsURLConnection);
        responseObject.responseCode = responseCode;
        if(responseCode == HttpStatus.SC_OK)
        {
            JSONObject jsonResponse = HttpUtil.getJsonResponse(httpsURLConnection);
            responseObject.jsonResponse = jsonResponse;
            //Log.d("makeHttpRequest", "Jason response: " + jsonResponse.toString());
        }
        else if(responseCode == HttpStatus.SC_BAD_REQUEST)
        {
            Log.d("makeHttpRequest","ERROR - bad request");
        }
        else
        {
            Log.d("makeHttpRequest","ERROR making http request");
        }
        httpsURLConnection.disconnect();

        return responseObject;
    }
    public static ResponseObject makeHttpRequestWithJsonParameters(String methodName, String requestType, ArrayList<KeyValuePair> headers, JSONObject jsonObject)
    {
        ResponseObject responseObject = new ResponseObject(-1,null);
        HttpsURLConnection httpsURLConnection = HttpUtil.getHttpsUrlConnection(methodName,requestType);
        if(headers != null)
            setHeaders(httpsURLConnection, headers);
        if(jsonObject != null)
            sendParametersAsJsonObject(httpsURLConnection, jsonObject);
        int responseCode = HttpUtil.getResponseCode(httpsURLConnection);
        responseObject.responseCode = responseCode;
        if(responseCode == HttpStatus.SC_OK)
        {
            JSONObject jsonResponse = HttpUtil.getJsonResponse(httpsURLConnection);
            responseObject.jsonResponse = jsonResponse;
            //Log.d("makeHttpRequest", "Jason response: " + jsonResponse.toString());
        }
        else if(responseCode == HttpStatus.SC_BAD_REQUEST)
        {
            Log.d("makeHttpRequest","ERROR - bad request");
        }
        else
        {
            Log.d("makeHttpRequest","ERROR making http request");
        }
        httpsURLConnection.disconnect();

        return responseObject;
    }

    public static ResponseObject makeHttpRequestWithAppendedParameters(String methodName, String requestType, ArrayList<KeyValuePair> headers, ArrayList<KeyValuePair> parameters)
    {
        ResponseObject responseObject = new ResponseObject(-1,null);
        String methodNameWithParameters = methodName;

        if(parameters != null)
        {
            methodNameWithParameters = appendParameters(methodName, parameters);
        }

        HttpsURLConnection httpsURLConnection = HttpUtil.getHttpsUrlConnection(methodNameWithParameters,requestType);
        if(headers != null)
            setHeaders(httpsURLConnection, headers);

        int responseCode = HttpUtil.getResponseCode(httpsURLConnection);
        if(responseCode == HttpStatus.SC_OK)
        {
            JSONObject jsonResponse = HttpUtil.getJsonResponse(httpsURLConnection);
            responseObject.responseCode = responseCode;
            responseObject.jsonResponse = jsonResponse;
            //Log.d("makeHttpRequest", "Jason response: " + jsonResponse.toString());
        }
        else if(responseCode == HttpStatus.SC_BAD_REQUEST)
        {
            Log.d("makeHttpRequest","ERROR - bad request");
        }
        else
        {
            Log.d("makeHttpRequest","ERROR making http request");
        }
        httpsURLConnection.disconnect();

        return responseObject;
    }

    public static HttpsURLConnection getHttpsUrlConnection(String methodName, String requestType)
    {
        try
        {
            URL url = new URL(Names.API_SERVER_URL + methodName);
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
            httpsURLConnection.setReadTimeout(TimeMeasurements.API_READ_TIMEOUT);
            httpsURLConnection.setConnectTimeout(TimeMeasurements.API_CONNECT_TIMEOUT);
            httpsURLConnection.setRequestMethod(requestType);
            //httpsURLConnection.setDoInput(true);
            //httpsURLConnection.setDoOutput(true);

            return httpsURLConnection;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return null;
        }
    }

    public static int getResponseCode(HttpsURLConnection httpsURLConnection)
    {
        try
        {
            return httpsURLConnection.getResponseCode();
        }
        catch (Exception ex)
        {
            return -1;
        }
    }

    public static JSONObject getJsonResponse(HttpsURLConnection httpsURLConnection)
    {
        try
        {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpsURLConnection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = bufferedReader.readLine()) != null)
            {
                response.append(inputLine);
            }
            bufferedReader.close();
            JSONObject jsonObject = new JSONObject(response.toString());
            return jsonObject;
        }
        catch(Exception ex)
        {
            return null;
        }
    }

    public static void setHeaders(HttpsURLConnection httpsURLConnection, ArrayList<KeyValuePair> headers)
    {
        try
        {
            for (KeyValuePair header : headers) {
                //String encodedKey = URLEncoder.encode(header.key, "utf-8");
                //String encodedValue = URLEncoder.encode(header.value, "utf-8");
                httpsURLConnection.setRequestProperty(header.key,header.value);
            }
        }
        catch(Exception ex) { }
    }

    public static void sendParametersAsJsonObject(HttpsURLConnection httpsURLConnection, JSONObject jsonObject)
    {
        try
        {
            DataOutputStream dataOutputStream = new DataOutputStream(httpsURLConnection.getOutputStream());
            String jsonString = jsonObject.toString();
            dataOutputStream.writeBytes(jsonString);
            dataOutputStream.flush();
            dataOutputStream.close();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }
    public static void sendParameters(HttpsURLConnection httpsURLConnection, ArrayList<KeyValuePair> parameters)
    {
        try
        {
            String parameterString = "";
            for (KeyValuePair parameter : parameters)
            {
                if(parameterString.length() > 0)
                    parameterString += "&";
                String encodedKey = URLEncoder.encode(parameter.key, "utf-8");
                String encodedValue = URLEncoder.encode(parameter.value, "utf-8");
                parameterString += encodedKey + "=" + encodedValue;
            }

            DataOutputStream dataOutputStream = new DataOutputStream(httpsURLConnection.getOutputStream());
            dataOutputStream.writeBytes(parameterString);
            dataOutputStream.flush();
            dataOutputStream.close();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }
    public static String appendParameters(String url, ArrayList<KeyValuePair> parameters)
    {
        try
        {
            String parameterString = "";
            for (KeyValuePair parameter : parameters)
            {
                if (parameterString.length() > 0)
                    parameterString += "&";
                String encodedKey = URLEncoder.encode(parameter.key, "utf-8");
                String encodedValue = URLEncoder.encode(parameter.value, "utf-8");
                parameterString += encodedKey + "=" + encodedValue;
            }
            return url + "?" + parameterString;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return url;
        }
    }

}
