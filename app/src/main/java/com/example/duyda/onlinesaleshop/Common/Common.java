package com.example.duyda.onlinesaleshop.Common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.duyda.onlinesaleshop.Models.Account;
import com.example.duyda.onlinesaleshop.Models.Request;

import java.util.Calendar;
import java.util.Locale;

public class Common {
    public static Account currentAccount;
    public static Request currentRequest;

    public static boolean isConnectedToInternet(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkInfo[] infos = connectivityManager.getAllNetworkInfo();
            if (infos != null)
                for (int i = 0; i < infos.length; i++)
                    if (infos[i].getState() == NetworkInfo.State.CONNECTED)
                        return true;
        }
        return false;
    }

    public static final String DELETE = "Delete";
    public static final String USER_KEY = "User";
    public static final String PWD_KEY = "Password";

    public static String getDate(long time) {
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        StringBuilder date = new StringBuilder(android.text.format.
                DateFormat.format("dd-MM-yyyy HH:mm", calendar).toString());
        return date.toString();
    }

}
