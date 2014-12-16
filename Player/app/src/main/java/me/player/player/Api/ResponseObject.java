package me.player.player.Api;

import org.json.JSONObject;

/**
 * Created by stevenstewart on 9/7/14.
 */
public class ResponseObject
{
    public int responseCode;
    public JSONObject jsonResponse;

    public ResponseObject(int responseCodePar, JSONObject jsonResponsePar)
    {
        responseCode = responseCodePar;
        jsonResponse = jsonResponsePar;
    }
}
