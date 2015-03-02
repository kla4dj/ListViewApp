package de.denjo.listviewapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class OpenCalls extends Activity {

    private JSONArray jsonArray;

    private ListView AllOpenCallsListView;


    TeleportClient mTeleportClient;
    TeleportClient.OnSyncDataItemTask mSubmit;


    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.open_calls_list);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        mTeleportClient = new TeleportClient(this);

        mSubmit = new OnSyncDataItemToDatabaseTask();
        mTeleportClient.setOnSyncDataItemTask(mSubmit);


        //Connecting the variable with the actual layout
        AllOpenCallsListView = (ListView) findViewById(R.id.AllOpenCallsListView);


        new GetAllOpenCallsTask().execute(new ApiConnector());

        AllOpenCallsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //----------Depending on which position it will be clicked, the JSONArray will be the one which got clicked

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                String clickedAnfrageRufenderName = null;

                try {


                    JSONObject anfrageClicked = jsonArray.getJSONObject(position);

                    System.out.println(anfrageClicked);


                } catch (JSONException e) {
                    e.printStackTrace();
                }


                String msg = "Do you want to answer " + clickedAnfrageRufenderName + "`s call?";

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(OpenCalls.this);
                dialogBuilder.setTitle("Callreply");
                dialogBuilder.setMessage(msg);
                dialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                dialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog dialog = dialogBuilder.create();
                dialog.show();

            }});
    }



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

                //--------------------------------new GET-Part - START------------------------------------------------
                HttpClient httpclient = new DefaultHttpClient();

                HttpResponse response = httpclient.execute(new HttpGet("http://IPIPIPIPIPIP/workstation_lib/gear/call.php?idstring="+idstring+"&timestamp="+timestamp));
                is = response.getEntity().getContent();
                //--------------------------------new GET-Part - End------------------------------------------------

                //Displaying a toast if the data is entered successfully
                String msg = "Data entered successfully in Database - ";
                String time = "  Timestamp : ";
                String callerID = " ID : ";
                Toast.makeText(getApplicationContext(), msg + callerID + idstring + time + timestamp, Toast.LENGTH_LONG).show();
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

    
    public void setListAdapter(JSONArray jsonArray)
    {

        AllOpenCallsListView.setAdapter(new OpenCallsListViewAdapter(jsonArray,this));

    }



    private class GetAllOpenCallsTask extends AsyncTask<ApiConnector,Long,JSONArray>
    {
        @Override
        protected JSONArray doInBackground(ApiConnector... params) {

            // it is executed on Background thread
            return params[0].OpenCalls();
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {

            //setTextToTextView(jsonArray);
            setListAdapter(jsonArray);


        }
    }

}


