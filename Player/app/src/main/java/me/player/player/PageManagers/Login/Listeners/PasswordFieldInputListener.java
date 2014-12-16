package me.player.player.PageManagers.Login.Listeners;

import android.text.Editable;
import android.text.TextWatcher;

import me.player.player.AppState;

/**
 * Created by stevenstewart on 9/7/14.
 */
public class PasswordFieldInputListener implements TextWatcher
{
    public void afterTextChanged(Editable editable)
    {

    }
    public void beforeTextChanged(CharSequence charSequence, int start, int count, int after)
    {

    }
    @Override
    public void onTextChanged(CharSequence charSequence, int start, int before, int count)
    {
        AppState appState = AppState.getInstance();
        if(appState.accessToken.length() > 0 || appState.refreshToken.length() > 0) //if the user starts typing a new password, clear the access token
        {
            appState.accessToken = "";
            appState.refreshToken = "";
        }
        appState.enteredPassword = charSequence.toString();
    }
}
