package org.izv.pgc.firebaserealtimedatabase.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;

import org.izv.pgc.firebaserealtimedatabase.connectivity.Connection;
import org.izv.pgc.firebaserealtimedatabase.connectivity.ResponseConnectivityListener;

public class ConnectivityStateReceiver extends BroadcastReceiver {

    private final static String URL = "https://pokemondb-ee97d.firebaseio.com";

    private ResponseConnectivityListener listener = new ResponseConnectivityListener() {
        @Override
        public void onResponse(boolean resultado) {
            if(resultado) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
            }
            Log.v("xyz","connectivity");
        }
    };
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v("xyz","on receive");
        Connection connection = new Connection(context, listener);
        if(connection.isActiveConnection()){
            connection.checkConnected(URL);
        }
    }
}
