package com.example.Diunar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class MyInformation extends Activity{
	private static List<Map<String, Object>> mDataList = new ArrayList<Map<String, Object>>();
	private static SimpleAdapter listItemAdapter;
	private ImageButton back;
	private String hint, id;
	private ListView mlv;
	private int myposition;
	private Handler handler;
	private boolean isSuccess, isChanged = false;
	private final String URL = "http://172.18.33.95:8888/mobile/";
	private final String queryURL=URL + "queryMyMessage";
	private final String deleteURL=URL + "deleteMessage";
	private Runnable runable1 = new Runnable(){
	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		HttpPost httppost = new HttpPost(deleteURL);
		// TODO Auto-generated method stub
		MyHttpClient.myHttpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 20000);
		List<NameValuePair>pair = new ArrayList<NameValuePair>();
		Log.i("ids",id);
		pair.add(new BasicNameValuePair("_id", id));
		// 读取超时
		MyHttpClient.myHttpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 20000);
		try {
			httppost.setEntity(new UrlEncodedFormEntity(pair, "utf-8"));
			HttpResponse response = MyHttpClient.myHttpClient.execute(httppost);
			String str = EntityUtils.toString(response.getEntity(), "utf-8");
			Log.i("str", str);
			JSONObject jsonObject = new JSONObject(str); 
			Map result =  new HashMap();
			Iterator iterator = jsonObject.keys();
			String key = null;
			String value = null;      
			while (iterator.hasNext()) {
				key = (String) iterator.next();
				value = jsonObject.getString(key);
				result.put(key, value);
			} 
			hint = result.get("hint").toString();
			if(result.get("status").toString().equals("false")){
				isSuccess = false;
			}
			else {
				isSuccess = true;
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		handler.obtainMessage(2, isSuccess).sendToTarget();
	}
};
	private Runnable runable = new Runnable(){
		@SuppressWarnings("unchecked")
		boolean flag = true;
		public void stopthread(){
			flag = false;
		}
		@Override
		public void run() {
			HttpPost httppost = new HttpPost(queryURL);
			// TODO Auto-generated method stub
			MyHttpClient.myHttpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 15000);
			List<NameValuePair>pair = new ArrayList<NameValuePair>();
			// 读取超时
			MyHttpClient.myHttpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 15000);
			try {
				HttpResponse response = MyHttpClient.myHttpClient.execute(httppost);
				String str = EntityUtils.toString(response.getEntity(), "utf-8");
				JSONObject jsonObject = new JSONObject(str); 
				Map result =  new HashMap();
				Iterator iterator = jsonObject.keys();
				String key = null;
				String value = null;      
				while (iterator.hasNext()) {
					key = (String) iterator.next();
					if(key.equals("messages")){
						JSONArray resultJSON=jsonObject.getJSONArray(key);
						for(int i = 0; i < resultJSON.length();i++){
							mDataList.add(JSONoperate(resultJSON.getJSONObject(i)));
						}
					}
					else{
						value = jsonObject.getString(key);
						result.put(key, value);
					}
				} 
				hint = result.get("hint").toString();
				if(result.get("status").toString().equals("false")){
					isSuccess = false;
				}
				else {
					isSuccess = true;
				}
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(!isChanged){
				handler.obtainMessage(1, isSuccess).sendToTarget();
			}
			else {
				handler.obtainMessage(3, isSuccess).sendToTarget();
			}
		}
	};
	private class MySimpleAdapter extends SimpleAdapter{
    	public MySimpleAdapter(Context context,
    			List<? extends Map<String, ?>> data, int resource,
    					String[] from, int[] to) {
    		 super(context, data, resource, from, to);
    		// TODO Auto-generated constructor stub
    	}
    	//重写getView()
    	@Override
    	public View getView(int position, View convertView, ViewGroup parent) {
    		// TODO Auto-generated method stub    		
    		View v= super.getView(position, convertView, parent);
    		return v;
    	}
    };
	protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);   
        setContentView(R.layout.my_information); 
        back = (ImageButton)findViewById(R.id.back121);
        back.setOnClickListener(backListener);
        mlv =(ListView)findViewById(R.id.listView121);
    	Thread threadMobile = new Thread(runable);
        threadMobile.start();
        handler = new Handler(){
			public void handleMessage(android.os.Message msg){
				super.handleMessage(msg);
				if(msg.what == 1){
					if(isSuccess){
						listItemAdapter = new MySimpleAdapter(MyInformation.this,
				        		mDataList,
				                R.layout.list_view,
				                new String[] {"type","title","description","time"}, 
				                new int[] {R.id.item_cata,R.id.item_title,R.id.item_content,
								R.id.iten_date});
						mlv.setAdapter(listItemAdapter);
						mlv.setOnItemClickListener(mItemClickListener);
						if (mDataList.size()==0){
							Toast.makeText(MyInformation.this, "暂无数据", Toast.LENGTH_LONG).show();
						}
					}
				}
				if(msg.what == 2){
					if(isSuccess){
						mDataList.remove(myposition);
						listItemAdapter.notifyDataSetChanged();
					}
				}
				if(msg.what == 3){
					if(isSuccess){
						isChanged = false;
						listItemAdapter.notifyDataSetChanged();
					}
				}
			}
        };
	}
	private OnItemClickListener mItemClickListener = new OnItemClickListener(){
		@Override
		public void onItemClick(AdapterView<?> arg0, View v, int position,
				long arg3) {
			// TODO Auto-generated method stub
			Map<String, Object> map = mDataList.get(position);
			Bundle bundle = new Bundle();
			bundle.putString("title", map.get("title").toString());
			bundle.putString("tag", map.get("tag").toString());
			bundle.putString("type", map.get("type").toString());
			bundle.putString("description", map.get("description").toString());
			bundle.putString("time", map.get("time").toString());
			bundle.putString("place",map.get("place").toString());
			bundle.putString("x",map.get("x").toString());
			bundle.putString("y",map.get("y").toString());
			bundle.putString("_id",map.get("_id").toString());
			bundle.putInt("position", position);
			bundle.putBoolean("flag", true);
			bundle.putString("contactInfo",map.get("contactInfo").toString());
			Intent intent = new Intent();
			intent.setClass(MyInformation.this, ListItemDetails.class);
			intent.putExtras(bundle);
			startActivityForResult(intent, 0);
		}
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
            Intent intent) {
        // 当requestCode和resultCode都为0时（处理特定的结果）。
        if (requestCode == 0 && resultCode == 0) {
        	Bundle bundle = intent.getExtras();
        	id = bundle.getString("_id");
        	myposition = bundle.getInt("position");
        	if(bundle.getBoolean("Delete")){
        		Thread threadDelete = new Thread(runable1);
        		threadDelete.start();
        	}
        }
        if (requestCode == 0 && resultCode == 1) {
        	mDataList.clear();
        	isChanged = true;
        	Thread threadMotify = new Thread(runable);
        	threadMotify.start();
        }
    }
	private OnClickListener backListener = new OnClickListener(){
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			mDataList.clear();
			finish();
		}
    };
    private Map JSONoperate(JSONObject jsonItem) throws JSONException{
    	Iterator iterator = jsonItem.keys();
    	String key = null;
		String value = null;    
		Map result =  new HashMap();
    	while (iterator.hasNext()) {	
    		key = (String) iterator.next();
    		if(key.equals("location")){
    			JSONObject resultJSON=jsonItem.getJSONObject(key);
    			result.put("x", resultJSON.getString("x"));
    			result.put("y", resultJSON.getString("y"));
    			result.put("place", resultJSON.getString("place"));
    		}
    		else if(key.equals("type")){
    			value = jsonItem.getString(key);
    			value = value.equals("0")?"丢东西":"捡东西";
    			result.put(key, value);
    		}
    		else if (key.equals("description")){
    			value = jsonItem.getString(key);
    			value = value.equals("")?"  该物品没有描述":"  "+value;
    			result.put(key, value);
    		}
    		else if(key.equals("time")){
    			value = jsonItem.getString(key);
    			String year, month, day, hour, minute;
    			year = value.split("-")[0];
    			month = value.split("-")[1];
    			day = value.split("T")[0].split("-")[2];
    			hour = value.split("T")[1].split(":")[0];
    			minute = value.split("T")[1].split(":")[1];
    			Date now = new Date(); 
    			Calendar c = Calendar.getInstance();
    			if(Integer.valueOf(year)==c.get(Calendar.YEAR)){
    				year = "";
    				if(Integer.valueOf(month)==now.getMonth()+1
    						&&Integer.valueOf(day)==c.get(Calendar.DAY_OF_MONTH)){
    					month = "";
    					day = "";
    				} else {
    					month = month+".";
    					day = day+" ";
    				}
    			} else {
    				year = year+".";
    			}
    			value = year+month+day+hour+":"+minute;
    			result.put(key, value);
    		}
    		else {
    			value = jsonItem.getString(key);
        		result.put(key, value);
    		}
    	}
    	return result;
    }
}
