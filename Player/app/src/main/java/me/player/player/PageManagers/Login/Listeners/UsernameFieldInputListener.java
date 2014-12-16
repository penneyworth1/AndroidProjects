package me.player.player.PageManagers.Login.Listeners;

import android.text.Editable;
import android.text.TextWatcher;

import me.player.player.AppState;

/**
 * Created by stevenstewart on 9/7/14.
 */
public class UsernameFieldInputListener implements TextWatcher
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
        appState.enteredUsername = charSequence.toString();
    }
}
