package com.example.location;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class CityActivity extends Activity {
	
	private String url = "http://169.254.218.153:8080/China_city.json";
	private TextView textView;
	private ListView listView;

	private List<City> city;
	private String name;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.city_layout);
		
		Intent it = getIntent();
		name = it.getStringExtra("name");
		
		listView = (ListView) findViewById(R.id.listview);
		textView = (TextView) findViewById(R.id.list_text);
		
		textView.setText(name);
		
		new Thread(){
			public void run() {
				getXml();
			};
		}.start();
	}

	protected void getXml() {
		// TODO Auto-generated method stub
		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(url);
		try {
			HttpResponse execute = client.execute(get);
			if(execute.getStatusLine().getStatusCode()==200){
				HttpEntity entity = execute.getEntity();
				String string = EntityUtils.toString(entity, "utf-8");
				Gson gson = new Gson();
				Type type = new TypeToken<List<City>>(){}.getType();
				city = gson.fromJson(string, type);
			}
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					MyAdapter adapter = new MyAdapter(CityActivity.this,city);
					listView.setAdapter(adapter);
				}
			});
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}