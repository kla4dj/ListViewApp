package de.denjo.listviewapp;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class OpenCallsListViewAdapter extends BaseAdapter {

    private JSONArray dataArray;
    private Activity activity;

    private static LayoutInflater inflater = null;



    public OpenCallsListViewAdapter(JSONArray jsonArray, Activity a){

        dataArray = jsonArray;
        activity = a;

        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

//    Array length

    @Override
    public int getCount() {

        return this.dataArray.length();
    }

    @Override
    public Object getItem(int position) {

        return position;
    }

    @Override
    public long getItemId(int position) {

        return position;
    }




    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        //set up convert view if it is null
        ListCell cell;


//Check if the convertview is null, if it is null it probably means that this //is the first time the view has been displayed
        if (convertView == null)
        {
            convertView = inflater.inflate(R.layout.open_calls_cell, null);
            cell = new ListCell();

            //------------------------------------------------------------------------------------------------------
            //--------------------------------Cellpointer to the Cells----------------------------------------------
            //------------------------------------------------------------------------------------------------------

            cell.CallingName = (TextView) convertView.findViewById(R.id.CallingName);
            cell.CallingTime = (TextView) convertView.findViewById(R.id.Anfragezeitpunkt);
            cell.Callingtyp = (TextView) convertView.findViewById(R.id.Callingtyp);


//          Setting the setTag to be able to reuse the convertView

            convertView.setTag(cell);

        }
        else
        {
            cell = (ListCell) convertView.getTag();
        }




        // change the data of cell
        try {


            JSONObject jsonObject = this.dataArray.getJSONObject(position);


            String requestTime = jsonObject.getString("request_date");
            cell.CallingTime.setText(" " + requestTime);


            String Callingtyp = jsonObject.getString("operation_type");
            if (Callingtyp.equals("replacement")){
                cell.Callingtyp.setText("Springer");
            }else if (Callingtyp.equals("expert")){
                cell.Callingtyp.setText("Experte");
            }


            String CallingName = jsonObject.getString("calling_worker_ID");
            if (CallingName.equals("0")) {

                cell.CallingName.setText("Ulrich");
            } else if (CallingName.equals("1")) {

                cell.CallingName.setText("Tobias");
            } else if (CallingName.equals("2")) {

                cell.CallingName.setText("Jan-Fabian");
            } else if (CallingName.equals("3")) {

                cell.CallingName.setText("Christopher");
            } else if (CallingName.equals("4")) {

                cell.CallingName.setText("Christian");
            } else if (CallingName.equals("5")) {

                cell.CallingName.setText("Christiane");
            }else if (CallingName.equals("6")) {

                cell.CallingName.setText("Deniz");
            }

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return convertView;
    }


//------------------------------------------------------------------------------------------------------
//--------------------------------Methode: Change for where to put that parsed data at------------------
//------------------------------------------------------------------------------------------------------



//  Methode for creating ceveral cells

    private class ListCell
    {

        private TextView CallingName;
        private TextView CallingTime;
        private TextView Callingtyp;


    }
}


