package com.example.amit.newsapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class NewsArrayAdapter extends ArrayAdapter<NewsItem> {

    public NewsArrayAdapter(@NonNull Context context, List<NewsItem> list) {
        super(context, 0, list);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //return super.getView(position, convertView, parent);
        View rootView=convertView;
        if(rootView==null){
            rootView= LayoutInflater.from(getContext()).inflate(R.layout.news_list_item,parent,false);
        }

        NewsItem item=getItem(position);
        TextView titleTextView=rootView.findViewById(R.id.news_title_text_view);
        TextView descriptionTextView=rootView.findViewById(R.id.news_description_text_view);
        ImageView imageView=rootView.findViewById(R.id.item_image_view);

        titleTextView.setText(item.getTitle());
        descriptionTextView.setText(item.getDescription());
        //setting default image
        imageView.setImageDrawable(getContext().getDrawable(R.drawable.latest_news_image));
        //setting originaa image from source or from object itself if image is stored already
        if(item.getBitmap()==null) {
            ImageLoader imageLoader = new ImageLoader(imageView,item);
            String urlToImage=item.getUrlToImage();
            if(!urlToImage.equals("null")) {

                URL url = null;
                try {
                    url = new URL(urlToImage);
                } catch (MalformedURLException e) {
                    Log.e("News ArrayAdapter", "exception in creating url urlToImage="+urlToImage, e);
                }
                imageLoader.execute(url);
            }

        }else{
            imageView.setImageBitmap(item.getBitmap());
        }

        return rootView;
    }
    private class ImageLoader extends AsyncTask<URL,Void,Bitmap>{

        ImageView imageView;
        NewsItem item;
        ImageLoader(ImageView imageView,NewsItem item){
            this.imageView=imageView;
            this.item=item;
        }
        @Override
        protected Bitmap doInBackground(URL... urls) {
            Bitmap bitmap=null;
            InputStream inputStream=null;
            try {
                inputStream=urls[0].openStream();
                bitmap= BitmapFactory.decodeStream(inputStream);
            } catch (IOException e) {
                Log.e("News ArrayAdapter","exception in parsing image",e);
            }finally {
                try{
                    if(inputStream!=null)
                        inputStream.close();
                }catch(Exception e){
                    Log.e("News ArrayAdapter","exception in closing imagestream",e);
                }
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if(bitmap!=null){
                imageView.setImageBitmap(bitmap);
                item.setBitmap(bitmap);
            }
        }
    }

}
