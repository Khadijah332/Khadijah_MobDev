/*  Starter project for Mobile Platform Development - 1st diet 26/27
    You should use this project as the starting point for your assignment.
    This project simply reads the data from the required source and displays the
    raw data in a TextField
*/

//
// Name                 Khadijah Ahmad
// Student ID           S2330453
// Programme of Study   Computing
//

// UPDATE THE PACKAGE NAME to include your Student Identifier
package com.example.ahmad_khadijah_s2330453.view;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import android.content.pm.PackageManager;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ahmad_khadijah_s2330453.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.Base64;

import java.util.ArrayList;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.ahmad_khadijah_s2330453.model.Situation;
import com.google.android.material.navigation.NavigationBarView;

import android.view.View;
import android.widget.ProgressBar;


public class MainActivity extends AppCompatActivity {

    private final ArrayList<Situation> allSituations = new ArrayList<>();
    private ProgressBar mProgressBar;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private final Handler autoUpdateHandler = new Handler(Looper.getMainLooper());
    private String clientId;
    private String clientKey;

    private final Runnable autoUpdate = new Runnable() {
        @Override
        public void run() {
            startProgress();
            autoUpdateHandler.postDelayed(this, 60 * 60 * 1000);
            //autoUpdateHandler.postDelayed(this, 60 * 1000); // 1 minute for testing
            Log.d("AutoUpdate", "Downloading Traffic Scotland data");
        } //download every hour
    };

    private int completedDownloads = 0;

    public ArrayList<Situation> getAllSituations() {
        synchronized (allSituations) {
            return new ArrayList<>(allSituations);
        }
    }
    //DOWNLOADING FROM TRAFFIC SCOTLAND
   final String unplannedEventsURL = "https://datex2.trafficscotland.org/rest/2.3/publications/UnplannedEvents/Content.xml";
    final String roadworksURL = "https://datex2.trafficscotland.org/rest/2.3/publications/CurrentRoadworks/Content.xml";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        mProgressBar = findViewById(R.id.bar1);

        try {
            Bundle metaData = getPackageManager()
                    .getApplicationInfo(
                            getPackageName(),
                            PackageManager.GET_META_DATA
                    ).metaData;

            clientId = metaData.getString(
                    "com.example.ahmad_khadijah_s2330453.MY_API_ID"
            );

            clientKey = metaData.getString(
                    "com.example.ahmad_khadijah_s2330453.MY_API_KEY"
            );

        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }

        //FRAGMENT MANAGER
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        //the nearby poge opens by default
        if (savedInstanceState == null) {
            transaction.replace(R.id.fragholder, new NearbyFragment());
            transaction.commit();
        }

        //DOWNLOAD DATA
        autoUpdateHandler.post(autoUpdate);

        //NAVIGATION
        NavigationBarView nav = findViewById(R.id.bottomNavigation);
        if (savedInstanceState != null) {
            nav.setSelectedItemId(savedInstanceState.getInt("selectedTab", R.id.nav_nearby));
        }

        nav.setOnItemSelectedListener(item -> {

            FragmentTransaction navtransaction =
                    getSupportFragmentManager().beginTransaction();

            if (item.getItemId() == R.id.nav_nearby) {
                navtransaction.replace(R.id.fragholder, new NearbyFragment());
            }
            else if (item.getItemId() == R.id.nav_maps) {
                navtransaction.replace(R.id.fragholder, new MapFragment());
            }
            else if (item.getItemId() == R.id.nav_search) {
                navtransaction.replace(R.id.fragholder, new SearchFragment());
            }
            else {
                return false;
            }

            navtransaction.commit();
            return true;
        });




        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void startProgress()
    {
        // clear previous situations
        allSituations.clear();
        completedDownloads = 0;
        mProgressBar.setVisibility(View.VISIBLE);
        // download unplanned events and roadworks
        new Thread(new Task(unplannedEventsURL)).start();
        new Thread(new Task(roadworksURL)).start();
    }

    // Need separate thread to access the internet resource over network
    // Other neater solutions should be adopted in later iterations.

    private class Task implements Runnable
    {
        private String result="";
        private String myurl;
        BufferedReader in = null;
        String inputLine = "";
        public Task(String aurl){
            myurl = aurl;
        }
        @Override
        public void run(){
            Log.d("MyTask","in run");
            try {
                URL url = new URL(myurl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                //ADD AUTHORISATION (string with clientIID:clientKey)
                urlConnection.setRequestProperty("Authorization",Base64.getEncoder().encodeToString((clientId + ":" + clientKey).getBytes()));
                try {
                    in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder resultBuilder = new StringBuilder();

                    while ((inputLine = in.readLine()) != null) {
                        resultBuilder.append(inputLine);
                    }

                    result = resultBuilder.toString();
                    in.close();
                } catch (IOException e) {
                    Log.e("urlConnection", "Download failed", e);
                }
                finally {
                    urlConnection.disconnect();
                }
            }catch (IOException e) {
                Log.e("urlConnection", "Download failed: " + myurl, e);

            }

            int i = result.indexOf("<?");

            if (i == -1) {
                Log.e("MyTask", "No valid XML received");

                mHandler.post(() -> {
                    mProgressBar.setVisibility(View.GONE);

                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Connection Error")
                            .setMessage("Unable to download traffic updates. Please check your internet connection.")
                            .setPositiveButton("OK", null)
                            .show();
                });

                return;
            }

            result = result.substring(i);

            try {
                XmlPullParserFactory factory =
                        XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput( new StringReader( result ) );

                //PARSING

                Situation aSituation = null;
                //booleans to check whether we are in that tag or not
                boolean insideASituation = false;
                boolean insideDescription = false;
                boolean insideRoadName = false;
                // for longitude and latitude
                boolean coordinates = false;

                int eventType = xpp.getEventType();

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) //if a start tag is found
                    {
                        //check what start tag we have
                        if (xpp.getName().equalsIgnoreCase("situation")) {
                            insideASituation = true;

                            //start a new situation object
                            aSituation = new Situation();

                            Log.d("Parsing", "New Situation found!");
                        }

                        //  retreiving the attribute at position 0
                        // for situation type
                        else if (xpp.getName().equalsIgnoreCase("situationRecord")) {
                            String temp = xpp.getAttributeValue(0);

                            if (insideASituation) {
                                aSituation.setSituationType(temp);
                                Log.d("Parsing", "Situation type is " + temp);
                            }

                        //for start time
                        } else if (xpp.getName().equalsIgnoreCase("overallStartTime")) {
                            String temp = xpp.nextText();

                            if (insideASituation) {
                                aSituation.setStartTime(temp);
                                Log.d("Parsing", "Start time is " + temp);
                            }

                        //for end time
                        } else if (xpp.getName().equalsIgnoreCase("overallEndTime")) {
                            String temp = xpp.nextText();

                            if (insideASituation) {
                                aSituation.setEndTime(temp);
                                Log.d("Parsing", "End time is " + temp);
                            }

                        // for delay type
                        } else if (xpp.getName().equalsIgnoreCase("delaysType")) {
                            String temp = xpp.nextText();

                            if (insideASituation) {
                                aSituation.setDelaysType(temp);
                                Log.d("Parsing", "Delays Type is " + temp);
                            }



                        } else if (xpp.getName().equalsIgnoreCase("generalPublicComment")) {
                            insideDescription = true;
                        } else if (xpp.getName().equalsIgnoreCase("name")
                                || xpp.getName().equalsIgnoreCase("ilc")) {
                            insideRoadName = true;
                        } else if (xpp.getName().equalsIgnoreCase("value")) {
                            String temp = xpp.nextText();

                            //check if its inside a situation and the description section
                            if (insideASituation && insideDescription) {
                                aSituation.setDescription(temp);
                                Log.d("Parsing", "Description is " + temp);
                            // if its not a description then check if its a road name
                            } else if (insideASituation && insideRoadName) {
                                if (aSituation.getRoadName() == null) {
                                    aSituation.setRoadName(temp);
                                    Log.d("Parsing", "Road name is " + temp);
                                }
                            }

                        // for latitude and longitude
                        } else if (xpp.getName().equalsIgnoreCase("latitude")) {
                            String temp = xpp.nextText();

                            // check we are in a situation and don't have coordinates
                            if (insideASituation && !coordinates) {
                                //store as double
                                aSituation.setLatitude(Double.parseDouble(temp));
                                Log.d("Parsing", "Latitude is " + temp);
                            }
                        } else if (xpp.getName().equalsIgnoreCase("longitude")) {
                            String temp = xpp.nextText();

                            // check we are in a situation and don't have coordinates
                            if (insideASituation && !coordinates) {
                                //store as double
                                aSituation.setLongitude(Double.parseDouble(temp));
                                // set coordinates as true
                                coordinates = true;

                                Log.d("Parsing", "Longitude is " + temp);
                            }
                        }
                    } //END OF START_TAG

                    else if (eventType == XmlPullParser.END_TAG) {
                        if (xpp.getName().equalsIgnoreCase("situation")) {
                            if (insideASituation && aSituation != null) {
                                // check which feed the situation is from

                                if (myurl.equals(unplannedEventsURL))
                                {
                                    aSituation.setFeedType("Unplanned Events");
                                }
                                else if (myurl.equals(roadworksURL))
                                {
                                    aSituation.setFeedType("Roadworks");
                                }

                                synchronized (allSituations)
                                {
                                    allSituations.add(aSituation);
                                }
                                //reset the booleans
                                insideASituation = false;
                                coordinates = false;



                                Log.d("Parsing", "Situation parsing completed!");
                            }
                        } else if (xpp.getName().equalsIgnoreCase("generalPublicComment")) {
                            insideDescription = false;
                        } else if (xpp.getName().equalsIgnoreCase("name")
                                || xpp.getName().equalsIgnoreCase("ilc")) {
                            insideRoadName = false;
                        }
                    } //END OF END_TAG

                    eventType = xpp.next(); //before looping get the next event
                }

                Log.d("Parsing", "Total situations: " + allSituations.size());


            } catch (XmlPullParserException e) {
                Log.e("Parsing","EXCEPTION" + e);
                //throw new RuntimeException(e);
            }
            catch (IOException e) {
                Log.e("Parsing", "IO error during parsing", e);
            }

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    completedDownloads++;

                    if (completedDownloads == 2) {
                        mProgressBar.setVisibility(View.GONE);
                    }
                }
            });


        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        NavigationBarView nav = findViewById(R.id.bottomNavigation);
        outState.putInt("selectedTab", nav.getSelectedItemId());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        autoUpdateHandler.removeCallbacks(autoUpdate);
        super.onDestroy(); //stops hourly updates when actuvuty ended
    }

}