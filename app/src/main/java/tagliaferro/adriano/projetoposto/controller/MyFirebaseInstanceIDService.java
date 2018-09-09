package tagliaferro.adriano.projetoposto.controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import tagliaferro.adriano.projetoposto.R;
import tagliaferro.adriano.projetoposto.model.FireDatabase;

/**
 * Created by Adriano2 on 21/11/2017.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {


    public void checkToken(Context context) {
        try {
            SharedPreferences pref = context.getSharedPreferences(context.getString(R.string.pref_token), MODE_PRIVATE);
            if (pref != null) {
                String key = pref.getString(context.getString(R.string.pref_token_key), null);
                if (key == null) {
                    onTokenRefresh();
                } else {
                    boolean sentToken = pref.getBoolean(context.getString(R.string.pref_token_sent), false);
                    if(!sentToken){
                        FireDatabase mFireDatabase = new FireDatabase();
                        mFireDatabase.sendTokenToServer(key);
                        SharedPreferences.Editor edt = pref.edit();
                        edt.putBoolean(context.getString(R.string.pref_token_sent), true);
                        edt.commit();
                    }
                }
            } else {
                onTokenRefresh();
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void onTokenRefresh() {
        try {
            // Get updated InstanceID token.
            String refreshedToken = FirebaseInstanceId.getInstance().getToken();
            Log.d("Token_LOG", "Refreshed token: " + refreshedToken);

            SharedPreferences pref = getApplicationContext().getSharedPreferences(getApplicationContext().getString(R.string.pref_token), MODE_PRIVATE);
            if(pref != null){
                SharedPreferences.Editor edt = pref.edit();
                edt.putString(getApplicationContext().getString(R.string.pref_token_key), refreshedToken);
                edt.commit();
            }
            // If you want to send messages to this application instance or
            // manage this apps subscriptions on the server side, send the
            // Instance ID token to your app server.
            FireDatabase mFireDatabase = new FireDatabase();
            mFireDatabase.sendTokenToServer(refreshedToken);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
