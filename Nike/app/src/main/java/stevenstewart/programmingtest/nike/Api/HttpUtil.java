package stevenstewart.programmingtest.nike.Api;

import android.util.Log;

import org.apache.http.HttpStatus;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by stevenstewart on 12/14/14.
 */
public class HttpUtil
{
    public static String CURRENCY_API_CALL = "http://rate-exchange.appspot.com/currency?from=EUR&to=USD";
    public static int API_READ_TIMEOUT = 30000;
    public static int API_CONNECT_TIMEOUT = 30000;

    public static ResponseObject makeRequest(String methodName, String requestType, ArrayList<KeyValuePair> headers, ArrayList<KeyValuePair> parameters, boolean useHttps)
    {
        ResponseObject responseObject = new ResponseObject(-1,null);
        HttpURLConnection httpURLConnection;
        if(useHttps)
            httpURLConnection = HttpUtil.getHttpsUrlConnection(methodName,requestType);
        else
            httpURLConnection = HttpUtil.getHttpUrlConnection(methodName,requestType);
        if(headers != null)
            setHeaders(httpURLConnection, headers);
        if(parameters != null)
            sendParameters(httpURLConnection, parameters);
        int responseCode = HttpUtil.getResponseCode(httpURLConnection);
        responseObject.responseCode = responseCode;
        if(responseCode == HttpStatus.SC_OK)
        {
            JSONObject jsonResponse = HttpUtil.getJsonResponse(httpURLConnection);
            responseObject.jsonResponse = jsonResponse;
            //Log.d("makeHttpRequest", "Jason response: " + jsonResponse.toString());
        }
        else if(responseCode == HttpStatus.SC_BAD_REQUEST)
        {
            Log.d("makeHttpRequest", "ERROR - bad request");
        }
        else
        {
            Log.d("makeHttpRequest","ERROR making http request");
        }
        httpURLConnection.disconnect();

        return responseObject;
    }
    public static ResponseObject makeRequestWithJsonParameters(String methodName, String requestType, ArrayList<KeyValuePair> headers, JSONObject jsonObject)
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

    public static HttpsURLConnection getHttpsUrlConnection(String apiCall, String requestType)
    {
        try
        {
            URL url = new URL(apiCall);
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
            httpsURLConnection.setReadTimeout(API_READ_TIMEOUT);
            httpsURLConnection.setConnectTimeout(API_CONNECT_TIMEOUT);
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

    public static HttpURLConnection getHttpUrlConnection(String apiCall, String requestType)
    {
        try
        {
            URL url = new URL(apiCall);
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setReadTimeout(API_READ_TIMEOUT);
            httpURLConnection.setConnectTimeout(API_CONNECT_TIMEOUT);
            httpURLConnection.setRequestMethod(requestType);
            //httpsURLConnection.setDoInput(true);
            //httpsURLConnection.setDoOutput(true);

            return httpURLConnection;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return null;
        }
    }

    public static int getResponseCode(HttpURLConnection httpURLConnection)
    {
        try
        {
            return httpURLConnection.getResponseCode();
        }
        catch (Exception ex)
        {
            return -1;
        }
    }

    public static JSONObject getJsonResponse(HttpURLConnection httpURLConnection)
    {
        try
        {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
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

    public static void setHeaders(HttpURLConnection httpURLConnection, ArrayList<KeyValuePair> headers)
    {
        try
        {
            for (KeyValuePair header : headers) {
                //String encodedKey = URLEncoder.encode(header.key, "utf-8");
                //String encodedValue = URLEncoder.encode(header.value, "utf-8");
                httpURLConnection.setRequestProperty(header.key,header.value);
            }
        }
        catch(Exception ex) { }
    }

    public static void sendParametersAsJsonObject(HttpURLConnection httpURLConnection, JSONObject jsonObject)
    {
        try
        {
            DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());
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
    public static void sendParameters(HttpURLConnection httpURLConnection, ArrayList<KeyValuePair> parameters)
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

            DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());
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
