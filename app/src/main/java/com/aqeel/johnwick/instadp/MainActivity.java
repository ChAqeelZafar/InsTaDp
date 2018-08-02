package com.aqeel.johnwick.instadp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

public class MainActivity extends AppCompatActivity {

    TextInputLayout textInputLayout;
    Button searchBtn, downloadBtn;
    ImageView dpImg;
    String imgUrlGlobal = "";
    CardView showLoadingCard ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textInputLayout = findViewById(R.id.main_input_username);
        searchBtn = findViewById(R.id.main_btn_search);
        dpImg = findViewById(R.id.main_img_dp);
        downloadBtn = findViewById(R.id.main_btn_download);

        showLoadingCard = findViewById(R.id.img_card_loading);

        showLoadingCard.setVisibility(View.GONE);
        dpImg.setVisibility(View.GONE);
        downloadBtn.setVisibility(View.GONE);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if((textInputLayout.getEditText().getText().toString().trim().equals(""))){
                    Toast.makeText(MainActivity.this, "Username is empty", Toast.LENGTH_LONG).show();

                }
                else{
                    showLoading(true);
                    getId(textInputLayout.getEditText().getText().toString().trim());
                }

            }
        });

        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dpImg.buildDrawingCache();
                Bitmap img=dpImg.getDrawingCache();
                MediaStore.Images.Media.insertImage(getContentResolver(), img, "Insta Dp" , "Downloded from (Ins Ta Dp) available on playstore");
                Toast.makeText(MainActivity.this, "Saved in Pictures", Toast.LENGTH_LONG).show();

            }
        });


    }




    void getId(String username){
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        String url = "https://apinsta.herokuapp.com/u/"+username;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String id;
                try {
                    id = response.getJSONObject("graphql").getJSONObject("user").getString("id");
                     getDp(id);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Username doesn't exist", Toast.LENGTH_LONG).show();
                    showLoading(false);


                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                showLoading(false);


            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    void getDp(String id){
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        String url = "https://i.instagram.com/api/v1/users/"+id+"/info/";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String imgUrl ;
                try {
                    imgUrl = response.getJSONObject("user").getJSONObject("hd_profile_pic_url_info").getString("url");
                    imgUrlGlobal = imgUrl;
                    Glide.with(MainActivity.this).load(imgUrl).into(dpImg);
                    showLoading(false);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, e.getLocalizedMessage() , Toast.LENGTH_LONG).show();
                    showLoading(false);


                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, error.getLocalizedMessage()+ "Username doesn't exist", Toast.LENGTH_LONG).show();
                showLoading(false);

            }
        });
        requestQueue.add(jsonObjectRequest);
    }


    void showLoading(boolean isShow){
        showLoadingCard.setVisibility(isShow?View.VISIBLE:View.GONE);
        dpImg.setVisibility(isShow?View.GONE:View.VISIBLE);
        downloadBtn.setVisibility(isShow?View.GONE:View.VISIBLE);

    }

    @Override

    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);

        return super.onCreateOptionsMenu(menu);

    }

    @Override

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.mShare:

                Intent i = new Intent(

                        android.content.Intent.ACTION_SEND);

                i.setType("text/plain");

                i.putExtra(

                        android.content.Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=ccom.aqeel.johnwick.instadp");

                startActivity(Intent.createChooser(

                        i,

                        "Share Using"));

                break;

        }

        return super.onOptionsItemSelected(item);

    }


}
