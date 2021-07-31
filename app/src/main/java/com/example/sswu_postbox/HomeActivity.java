package com.example.sswu_postbox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import android.widget.Button;

import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    //Recyclerview
    private RecyclerView recyclerView;
    private MyRecyclerAdapter adapter;

    //listView
    ListView postList;
    ArrayList<String> post_title = new ArrayList<String>();
    ArrayList<String> post_date = new ArrayList<String>();
    MyListAdapter myListAdapter;


    private BottomNavigationView bottomNavigationView;
    private FragmentManager fm;
    private FragmentTransaction ft;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        // recyclerView
        home_keyword_list();


        // listView
        postList = findViewById(R.id.home_post_list);
        myListAdapter = new MyListAdapter(this, post_title, post_date);
        postList.setAdapter(myListAdapter);

        notice_list();


        Button plus_keyword_btn = findViewById(R.id.my_keyword_list_plus_btn);
        plus_keyword_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), KeywordSettingActivity.class);
                startActivity(intent);
            }
        });


        Button check_keyword_post_btn = findViewById(R.id.check_keyword_post_btn);
        check_keyword_post_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CheckKeywordPostActivity.class);
                startActivity(intent);
            }
        });

/*
        ImageButton notification_btn = (ImageButton)findViewById(R.id.notification_btn);
        notification_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NotificationListActivity.class);
                startActivity(intent);
            }
        });


        ImageButton locker_btn = (ImageButton)findViewById(R.id.locker_btn);
        locker_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LockerActivity.class);
                startActivity(intent);
            }
        });

*/

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem menuItem) {

                switch (menuItem.getItemId()){

                    case R.id.notification_btn:
                        setFrag(0);
                        break;
                    case R.id.locker_btn:
                        setFrag(1);
                        break;
                    case R.id.home_btn:
                        setFrag(2);
                        break;
                    case R.id.setting_btn:
                        setFrag(3);
                        break;
                }

                return true;
            }
        });

    }


    private void setFrag(int n) {
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();

        switch (n){
            case 0:
                //ft.replace(R.id.home_frame, frag1);
                //ft.commit();
                Intent notification = new Intent(this, NotificationListActivity.class);
                startActivity(notification);
                break;
            case 1:
                //ft.replace(R.id.home_frame, frag2);
                //ft.commit();
                Intent locker = new Intent(this, LockerActivity.class);
                startActivity(locker);
                break;
            case 2:
                //ft.replace(R.id.home_frame, frag3);
                //ft.commit();
                Intent home = new Intent(this, HomeActivity.class);
                startActivity(home);
                break;
            case 3:
                //ft.replace(R.id.home_frame, frag4);
                //ft.commit();
                Intent setting = new Intent(this, SettingActivity.class);
                startActivity(setting);
                break;
        }
    }


    //Recyclerview
    private void home_keyword_list(){

        recyclerView = findViewById(R.id.home_keyword_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        ArrayList<String> itemList = new ArrayList<>();
        itemList.add("키워드1");
        itemList.add("키워드2");
        itemList.add("키워드3");
        itemList.add("키워드4");
        itemList.add("키워드5");
        itemList.add("키워드6");
        itemList.add("키워드7");
        itemList.add("키워드8");
        itemList.add("키워드9");

        adapter = new MyRecyclerAdapter(this, itemList, onClickItem);
        recyclerView.setAdapter(adapter);

    }

    private View.OnClickListener onClickItem = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            String str = (String) v.getTag();
            Toast.makeText(HomeActivity.this, str, Toast.LENGTH_SHORT).show();
        }
    };


    void notice_list() {
        String TAG = HomeActivity.class.getSimpleName();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String token = sharedPreferences.getString("access_token", "null");

        String url = "http://3.37.68.242:8000/userNotice/";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject notice = response.getJSONObject(i).getJSONObject("notice");
                                post_title.add(notice.getString("title"));
                                post_date.add(notice.getString("date"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        myListAdapter.notifyDataSetChanged();
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Log.d(TAG, "notice user list error");
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return give_token(token);
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    Map<String, String> give_token(String token) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + token);

        return headers;
    }


}