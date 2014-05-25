package com.stuffsels.muzei.deviantart;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import com.google.android.apps.muzei.api.Artwork;
import com.google.android.apps.muzei.api.RemoteMuzeiArtSource;
import com.stuffsels.muzei.deviantart.classes.Deviation;
import com.stuffsels.muzei.deviantart.helpers.DeviantArtDDParser;
import com.stuffsels.muzei.deviantart.helpers.DeviantArtRssParser;
import com.stuffsels.muzei.deviantart.helpers.PreferenceHelper;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.*;

public class DeviantARTSource extends RemoteMuzeiArtSource{
    public static final String ACTION_FORCE_UPDATE = "action_force_update";

    private static final String TAG = "DeviantART";
    private static final String SOURCE_NAME = "DeviantARTSource";

    private int ROTATE_TIME_MILLIS = 3 * 60 * 60 * 1000; // rotate every 3 hours

    private Handler handler;

    public DeviantARTSource() {
        super(SOURCE_NAME);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent == null) {
            super.onHandleIntent(intent);
            return;
        }

        String action = intent.getAction();
        if (ACTION_FORCE_UPDATE.equals(action)) {
            scheduleUpdate(System.currentTimeMillis() + 1000);
        }

        super.onHandleIntent(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
        setUserCommands(BUILTIN_COMMAND_ID_NEXT_ARTWORK);
    }

    //Part of code found on the web
    private static boolean isWifiConnected(Context c) {
        if (c!= null) {
            ConnectivityManager connManager = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo[] connections = connManager.getAllNetworkInfo();
            int count = connections.length;
            for (int i = 0; i < count; i++)
                if (connections[i]!= null && connections[i].getType() == ConnectivityManager.TYPE_WIFI && connections[i].isConnectedOrConnecting() ||
                        connections[i]!= null &&  connections[i].getType() == ConnectivityManager.TYPE_ETHERNET && connections[i].isConnectedOrConnecting())
                    return true;
        }
        return false;
    }

    @Override
    protected void onTryUpdate(int i) throws RetryException {
        Random random = new Random();
        List<Deviation> deviations = new ArrayList<Deviation>();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        PreferenceHelper prefs = new PreferenceHelper(sharedPref);
        String query = prefs.getQuery();
        String mode = prefs.getMode();
        Boolean nfsw = prefs.getNfsw();
        Boolean wifionly = prefs.getWifiOnly();
        Integer refreshtime = prefs.getRefreshTime();
        Integer deviationCount = prefs.getNrOfDeviations();
        ROTATE_TIME_MILLIS = refreshtime * 60 * 1000;
        try {
            if (wifionly && !isWifiConnected(this)) {
                scheduleUpdate(System.currentTimeMillis() + ROTATE_TIME_MILLIS);
                return;
            }
        } catch (NullPointerException e)
        {
            return;
        }

        String currentToken = (getCurrentArtwork() != null) ? getCurrentArtwork().getToken() : null;
        if (mode.equals("search")) {
            DeviantArtRssParser parser = new DeviantArtRssParser(query, deviationCount, nfsw);
            try {
                deviations = parser.getDeviations();
            } catch (XmlPullParserException e) {
                throw new RetryException();
            } catch (IOException e) {
                throw new RetryException();
            }
        } else if (mode.equals("dd")) {
            DeviantArtDDParser parser = new DeviantArtDDParser(nfsw);
            try {
                deviations = parser.getDeviations();
            } catch (IOException e) {
                throw new RetryException();
            }
        }

        if (deviations == null || deviations.size() == 0) {
            scheduleUpdate(System.currentTimeMillis() + ROTATE_TIME_MILLIS);
            return;
        }
        Deviation deviation;
        String token;
        while (true) {
            deviation = deviations.get(random.nextInt(deviations.size()));
            token = deviation.guid;
            if (deviations.size() <= 1 || !TextUtils.equals(token, currentToken)) {
                break;
            }
        }

        publishArtwork(new Artwork.Builder()
                .title(deviation.title)
                .byline(deviation.userName)
                .imageUri(Uri.parse(deviation.imageUrl))
                .token(token)
                .viewIntent(new Intent(Intent.ACTION_VIEW,
                        Uri.parse(deviation.link)))
                .build());

        scheduleUpdate(System.currentTimeMillis() + ROTATE_TIME_MILLIS);
    }

}
