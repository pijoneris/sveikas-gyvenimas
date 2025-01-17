package com.sveikata.productions.mabe.sveikasgyvenimas;

import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HealthyLifeActivity extends android.support.v4.app.Fragment {

    private ArrayList<InfoHolder> data = new ArrayList<InfoHolder>();
    public static RecyclerView recyclerView;
    private RecyclerAdapter adapter;
    public final static String DEFAULT_FB_LINK = "https://www.facebook.com";

    //OBJECTS for checking if user is admin
    private JSONArray jsonArray;
    private JSONObject userData;

    private String is_administrator;
    private View rootView;

    private boolean finishedEventsEmpty = true;

    public static boolean addData = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        //Preferences to check if user is admin
        SharedPreferences userPrefs = getActivity().getSharedPreferences("UserData", getActivity().MODE_PRIVATE);
        String user_data = userPrefs.getString("user_data", "");


        try {
            jsonArray = new JSONArray(user_data);
            userData = jsonArray.getJSONObject(0);

//            Log.i("TEST", jsonArray.toString());
            if(!userData.getString("is_admin").isEmpty()) {
                is_administrator = userData.getString("is_admin");
            }



        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(is_administrator.equals("1")){
            rootView = inflater.inflate(R.layout.activity_schedule_layout_admin,container,false);

            recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
            adapter = new RecyclerAdapter(getActivity(), data, this, recyclerView, is_administrator);
            recyclerView.getRecycledViewPool().setMaxRecycledViews(0, 0);


            if(addData) {
                addData = false;
                finishedEvents(adapter);
                if(!finishedEventsEmpty){
                    lineBetweenFisnishedAndNot(adapter);
                }
                initializeData(adapter);
                initializeDataFirstTime(adapter, "1");



            }


            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

            //Swipe to refresh init
            final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh_layout_admin);
            swipeRefreshLayout.setEnabled(true);
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    adapter.currentPos = new LatLng(55.3, 23.7);
                    adapter.mapZoom = 5.8f;
                    adapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                }
            });

        } else{
            rootView = inflater.inflate(R.layout.activity_schedule_layout,container,false);


            recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_client);
            adapter = new RecyclerAdapter(getActivity(), data,this, recyclerView, is_administrator);

            //Swipe to refresh init
            final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh_layout_client);
            swipeRefreshLayout.setEnabled(true);
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    adapter.currentPos = new LatLng(55.3, 23.7);
                    adapter.mapZoom = 5.8f;
                    adapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                }
            });

            if(addData) {
                addData = false;

                finishedEvents(adapter);
                if(!finishedEventsEmpty){
                    lineBetweenFisnishedAndNot(adapter);
                }
                initializeData(adapter);
                initializeDataFirstTime(adapter, "2");


            }


            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        }





        return rootView;

    }

    public void initializeData(RecyclerAdapter adapter){
        //Preferences to fetch all schedule data
        SharedPreferences dataPrefs = getActivity().getSharedPreferences("ScheduleData", getActivity().MODE_PRIVATE);
        String schedule = dataPrefs.getString("schedule_data", "");

        try {
           JSONArray scheduleDataArray = new JSONArray(schedule);
            for (int i = 0; i<scheduleDataArray.length(); i++){
                JSONObject scheduleData = scheduleDataArray.getJSONObject(i);

                String event_description = scheduleData.getString("description");
                String event_name = scheduleData.getString("name");
                String event_location = scheduleData.getString("location_name");
                String event_date = scheduleData.getString("date");
                double latitude = Double.parseDouble(scheduleData.getString("latitude"));
                double longtitude = Double.parseDouble(scheduleData.getString("longtitude"));
                String id = scheduleData.getString("id");
                String fb_link = scheduleData.getString("fb_link");

                adapter.add(new InfoHolder(event_name, event_location + event_date, event_description, is_administrator.equals("1") ? "3" : "0", latitude, longtitude, id, fb_link), 0);

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    public void finishedEvents(RecyclerAdapter adapter){
        //Preferences to fetch all schedule data
        SharedPreferences dataPrefs = getActivity().getSharedPreferences("ScheduleData", getActivity().MODE_PRIVATE);
        String schedule = dataPrefs.getString("finished_schedule_data", "");



        try {
            JSONArray scheduleDataArray = new JSONArray(schedule);
            for (int i = 0; i<scheduleDataArray.length(); i++){
                JSONObject scheduleData = scheduleDataArray.getJSONObject(i);

                String event_description = scheduleData.getString("description");
                String event_name = scheduleData.getString("name");
                String event_location = scheduleData.getString("location_name");
                String event_date = scheduleData.getString("date");
                double latitude = Double.parseDouble(scheduleData.getString("latitude"));
                double longtitude = Double.parseDouble(scheduleData.getString("longtitude"));
                String id = scheduleData.getString("id");
                String fb_link = scheduleData.getString("fb_link");

                adapter.add(new InfoHolder(event_name, event_location + event_date, event_description, "5", latitude, longtitude, id, fb_link), 0);

                if(!event_name.isEmpty()){
                    finishedEventsEmpty = false;
                }

                Log.i("TEST", String.valueOf(event_name.isEmpty()));

            }



        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
    public void lineBetweenFisnishedAndNot(RecyclerAdapter adapter){
        adapter.add(new InfoHolder("", "", "", "4", 4,4, "",""),0);
    }

    public void initializeDataFirstTime(RecyclerAdapter adapter, String type){
                adapter.add(new InfoHolder("", "","",type, 123, 123, "", DEFAULT_FB_LINK), 0);
    }

}
