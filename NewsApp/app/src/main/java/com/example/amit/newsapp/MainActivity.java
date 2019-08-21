package com.example.amit.newsapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

    ProgressDialog dialog;
    final String QUERY_URL="https://newsapi.org/v2/top-headlines?country=in&apiKey=db2cd603f03d4f0892ce0b5225e05193";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dialog=new ProgressDialog(this);
        dialog.setTitle("Loading");
        dialog.setMessage("wait while news is loading");
        dialog.setCancelable(false);
        dialog.show();

        LoadNewsAsync load=new LoadNewsAsync();
        load.execute();

    }

    private class LoadNewsAsync extends AsyncTask<URL,Void,ArrayList<NewsItem>>{

        @Override
        protected ArrayList<NewsItem> doInBackground(URL... urls) {
            ArrayList<NewsItem> list=new ArrayList<>();
            URL url=createURL(QUERY_URL);
            if(url==null){
                Log.i("Main Activity","Empty Url returning empty list");
                return list;//empty list
            }
            InputStream inputStream=null;
            String responseRawData=null;
            HttpURLConnection httpURLConnection=null;
            try {
                httpURLConnection= (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();
                inputStream=httpURLConnection.getInputStream();
                responseRawData=getRawDataFromStream(inputStream);
                Log.i("Main Activity","Response data="+responseRawData);
                list=getListFromRawData(responseRawData);
            } catch (IOException e) {
                Log.e("Main Activity","exception in openConnection",e);
            }finally {
                if(httpURLConnection!=null)
                    httpURLConnection.disconnect();
                if(inputStream!=null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        Log.e("Main Activity","exception in closing connection",e);
                    }
                }
            }
            return list;
        }

        private ArrayList<NewsItem> getListFromRawData(String responseRawData) {
            ArrayList<NewsItem> list=new ArrayList<>();

            try{
                JSONObject resultJSONObject=new JSONObject(responseRawData);
                JSONArray array=resultJSONObject.getJSONArray("articles");
                for(int i=0;i<array.length();++i){
                    JSONObject tmp=array.getJSONObject(i);
                    String title=tmp.getString("title");
                    String description=tmp.getString("description");
                    String url=tmp.getString("url");
                    String imgUrl=tmp.getString("urlToImage");
                    list.add(new NewsItem(title,description,url,imgUrl));
                }
            }catch (JSONException e) {

                Log.e("Main Activity", "Problem parsing the JSON results", e);
            }
            return list;
        }

        private String getRawDataFromStream(InputStream inputStream) {

            StringBuilder stringBuilder=new StringBuilder("");
            BufferedReader reader=new BufferedReader(
                    new InputStreamReader(inputStream, Charset.forName("UTF-8")));
            try {
                String line=reader.readLine();
                while(line!=null){
                    stringBuilder.append(line);
                    line=reader.readLine();
                }
            } catch (IOException e) {
                Log.e("Main Activity","exception in getRawDataFromStream ",e);
            }
            return stringBuilder.toString();
        }

        private URL createURL(String strURL){
            URL url=null;
            try {
                url=new URL(strURL);
            } catch (MalformedURLException e) {
                Log.e("Main Activity","exception in create URL ",e);
            }
            return url;
        }

        @Override
        protected void onPostExecute(ArrayList<NewsItem> newsItems) {

            if(newsItems==null){
                Log.i("Main Activity","empty arrayList");
                return;
            }
            final NewsArrayAdapter adapter=new NewsArrayAdapter(MainActivity.this,newsItems);
            ListView listView=findViewById(R.id.news_list_view);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Uri uri=Uri.parse(adapter.getItem(position).getUrl());
                    Intent intent=new Intent(Intent.ACTION_VIEW,uri);
                    startActivity(intent);
                }
            });
            Iterator<NewsItem> iterator=newsItems.iterator();
            Log.i("Main Activity","displaying data "+iterator.hasNext());
            while(iterator.hasNext()){
                NewsItem item=iterator.next();
                Log.i("NewsItem","title="+item.getTitle());
            }
            dialog.dismiss();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.refresh_menu_item:
                dialog=new ProgressDialog(this);
                dialog.setTitle("Loading");
                dialog.setMessage("wait while news is loading");
                dialog.setCancelable(false);
                dialog.show();

                LoadNewsAsync load=new LoadNewsAsync();
                load.execute();

        }
        return super.onOptionsItemSelected(item);
    }
}
