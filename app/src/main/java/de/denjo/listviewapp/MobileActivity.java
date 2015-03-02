package de.denjo.listviewapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.wearable.DataMap;
import com.mariux.teleport.lib.TeleportClient;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class MobileActivity extends Activity {

    TeleportClient mTeleportClient;
    TeleportClient.OnSyncDataItemTask mSubmit;


    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.start_screen);

        mTeleportClient = new TeleportClient(this);

        mSubmit = new OnSyncDataItemToDatabaseTask();
        mTeleportClient.setOnSyncDataItemTask(mSubmit);

    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        mTeleportClient.connect();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        mTeleportClient.disconnect();
//
//    }

    @Override
    protected void onStart() {
        super.onStart();
        mTeleportClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mTeleportClient.disconnect();
    }



    //----------Taseen Part-------------
    public void taseenbutton(View view){
        startActivity(new Intent(MobileActivity.this, OpenCalls.class));
    }



    //-------ShowToast Class für SyncDataItem and Databaseinput!!! That's where the wearable rock'n'roll happens--------
    public class OnSyncDataItemToDatabaseTask extends TeleportClient.OnSyncDataItemTask {

        InputStream is = null;

        @Override
        protected void onPostExecute(DataMap dataMap) {


//            https://www.youtube.com/watch?v=MdyZKewSwFg

            String s = dataMap.getString("userIDstring");

            String idstring = s.substring(0,1);
            String timestamp = s.substring(1);

            //Setting the nameValuePairs
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            //Adding the string variabls inside the nameValuePairs
            nameValuePairs.add(new BasicNameValuePair("userIDstring", idstring));
            nameValuePairs.add(new BasicNameValuePair("userIDstring", timestamp));

            // Setting up the connection inside the try catch block

            try {
//                //Setting up the default http client
//                HttpClient httpClient = new DefaultHttpClient();
//
//                //Setting up the http post method and passing the url in case
//                // of online database and the ip address in case of localhost database.
//                //And the php file which serves as the link between the android app
//                // and the database
//
////                HttpPost httpPost = new HttpPost("http://IPIPIPIPIPIP/tutorial.php");



                //--------------------------------new GET-Part - START------------------------------------------------
                HttpClient httpclient = new DefaultHttpClient();

                HttpResponse response = httpclient.execute(new HttpGet("http://IPIPIPIPIPIP/workstation_lib/gear/call.php?idstring="+idstring+"&timestamp="+timestamp));
                is = response.getEntity().getContent();
                //--------------------------------new GET-Part - End------------------------------------------------





//                //Passing the nameValuePairs inside the httpPost
//                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
//
//                //Getting the response
//                HttpResponse response = httpClient.execute(httpPost);
//
//                //Setting up the entity
//                HttpEntity entity = response.getEntity();
//
//                //Setting up the content inside an input stream reader
//                is = entity.getContent();

                //Displaying a toast if the data is entered successfully
                String msg = "Data entered successfully in Database - ";
                String time = "  Timestamp : ";
                String workerID = " WorkerID : ";
                Toast.makeText(getApplicationContext(), msg + workerID + idstring + time + timestamp, Toast.LENGTH_LONG).show();
            }

            //Writing the catch blocks to handle the exceptions
            catch (ClientProtocolException e) {
                Log.e("ClientProtocol", "Log_tag");
                e.printStackTrace();
            } catch (IOException e) {
                Log.e("Log_tag", "IOException");
                e.printStackTrace();
            }



            //let's reset the task (otherwise it will be executed only once)
            mTeleportClient.setOnSyncDataItemTask(new OnSyncDataItemToDatabaseTask());

        }
    }




}