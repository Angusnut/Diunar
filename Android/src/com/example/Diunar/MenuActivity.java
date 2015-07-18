package com.example.Diunar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.mapapi.SDKInitializer;
import com.example.Diunar.AutoListView.OnLoadListener;
import com.example.Diunar.AutoListView.OnRefreshListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MenuActivity extends Activity implements OnRefreshListener,
OnLoadListener
{
	private static List<Map<String, Object>> mDataList = new ArrayList<Map<String, Object>>();
	private static SimpleAdapter listItemAdapter;
	private static List<Map<String, Object>> tempDataLists = new ArrayList<Map<String, Object>>();;
	private static ArrayList<String> nameArrayList = new ArrayList<String>();;
	private TextView tab1,tab3,tab4;
	private ListView mlv, lvPopupList;
	private AutoListView myAutoListView;
	private static SQLHelper database;
	List<Map<String, String>> moreList; 
	private int NUM_OF_VISIBLE_LIST_ROWS = 2;
	private PopupWindow popupwindow; 
	private ImageButton myaccount;
	private String hint;
	private ProgressDialog mpd;
	private Handler handler1;
	private boolean isSuccess;
	private final String URL = "http://172.18.33.95:8888/mobile/";
	private final String queryURL=URL + "queryMessage";
	private final String logoutURL=URL + "logout";
	private int current = 1, pageNumber;
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
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		SDKInitializer.initialize(getApplicationContext()); 
		setContentView(R.layout.slider_layout);
		database = new SQLHelper(this);
		pageNumber = 0;
		myAutoListView=(AutoListView)findViewById(R.id.listView1);
		myaccount=(ImageButton)findViewById(R.id.myaccount);
		myaccount.setOnClickListener(myaccountListener);
		setData();
		if(mDataList.size() == 0){
			threads(pageNumber,0);
        }
		handler1 = new Handler(){
			public void handleMessage(android.os.Message msg){
				super.handleMessage(msg);
				if(msg.what == 1){
					if(isSuccess){
						listItemAdapter = new MySimpleAdapter(MenuActivity.this,
				        		mDataList,
				                R.layout.list_view,
				                new String[] {"type","title","description","time"}, 
				                new int[] {R.id.item_cata,R.id.item_title,R.id.item_content,
								R.id.iten_date});
						myAutoListView.setAdapter(listItemAdapter);
						myAutoListView.setOnRefreshListener(MenuActivity.this);
						myAutoListView.setOnLoadListener(MenuActivity.this);
						myAutoListView.setOnItemClickListener(mItemClickListener);
				        moreList = new ArrayList<Map<String, String>>();
				        listinit(); 
				        if(mpd != null && mpd.isShowing()){
				        	mpd.dismiss();
				        }
					}
				}
				if(msg.what == 2){
					if(isSuccess){
						loadData(AutoListView.REFRESH);
						if(mpd != null && mpd.isShowing()){
				        	mpd.dismiss();
				        }
					}
				}
				if(msg.what == 3){
					if(isSuccess){
						loadData(AutoListView.LOAD);
						mpd.dismiss();
					}
				}
			}
		};
		init();
		isSuccess = true;
		handler1.obtainMessage(1, isSuccess).sendToTarget();
	}
	private void setData(){
		SQLiteDatabase db = database.getReadableDatabase();
		Cursor c = db.query("Item",null,null,null,null,null,null);
		if(c.moveToFirst()){
		    for(int i=0;i<c.getCount();i++){
		        c.moveToPosition(i);
		        Map<String, Object> map = new HashMap<String, Object>();
				map.put("title", c.getString(c.getColumnIndex("title")));
				map.put("tag", c.getString(c.getColumnIndex("tag")));
				map.put("type", c.getString(c.getColumnIndex("type")));
				map.put("description", c.getString(c.getColumnIndex("description")));
				map.put("time", c.getString(c.getColumnIndex("time")));
				map.put("place", c.getString(c.getColumnIndex("place")));
				map.put("x", c.getString(c.getColumnIndex("x")));
				map.put("y", c.getString(c.getColumnIndex("y")));
				map.put("contactInfo", c.getString(c.getColumnIndex("contactInfo")));
				mDataList.add(map);
		    }
		}
		db.close();
	}
	private OnItemClickListener mItemClickListener = new OnItemClickListener(){
		@Override
		public void onItemClick(AdapterView<?> arg0, View v, int position,
				long arg3) {
			// TODO Auto-generated method stub
			Map<String, Object> map = mDataList.get(position-1);
			Bundle bundle = new Bundle();
			bundle.putString("title", map.get("title").toString());
			bundle.putString("tag", map.get("tag").toString());
			bundle.putString("type", map.get("type").toString());
			bundle.putString("description", map.get("description").toString());
			bundle.putString("time", map.get("time").toString());
			bundle.putString("place",map.get("place").toString());
			bundle.putString("x",map.get("x").toString());
			bundle.putString("y",map.get("y").toString());
			bundle.putBoolean("flag", false);
			bundle.putString("contactInfo",map.get("contactInfo").toString());
			Intent intent = new Intent();
			intent.setClass(MenuActivity.this, ListItemDetails.class);
			intent.putExtras(bundle);
			startActivity(intent);
		}
    };
	private void loadData(final int what) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Message msg = handler.obtainMessage();
				msg.what = what;
				msg.obj = tempDataLists;
				handler.sendMessage(msg);
			}
		}).start();
	}
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			List<Map<String, Object>> result = (List<Map<String, Object>>) msg.obj;
			switch (msg.what) {
			case AutoListView.REFRESH:
				myAutoListView.onRefreshComplete();
				mDataList.clear();
				mDataList.addAll(result);
				for(int i = 0; i < mDataList.size();i++){
					SQLiteDatabase db = database.getWritableDatabase();
					db.execSQL("delete from Item");
					ContentValues cv = new ContentValues();
					Map<String, Object> map = mDataList.get(i);
					cv.put("title", map.get("title").toString());
					cv.put("type", map.get("type").toString());
					cv.put("description", map.get("description").toString());
					cv.put("time", map.get("time").toString());
					cv.put("place",map.get("place").toString());
					cv.put("x",map.get("x").toString());
					cv.put("tag",map.get("tag").toString());
					cv.put("y",map.get("y").toString());
					cv.put("contactInfo",map.get("contactInfo").toString());
					db.insert("Item",null,cv);
					db.close();
				}
				break;
			case AutoListView.LOAD:
				myAutoListView.onLoadComplete();
				mDataList.addAll(result);
				break;
			}
			myAutoListView.setResultSize(result.size());
			listItemAdapter.notifyDataSetChanged();
		};
	};
	private OnClickListener myaccountListener  = new OnClickListener(){
		@SuppressLint("HandlerLeak") @Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (popupwindow != null&&popupwindow.isShowing()) {  
                popupwindow.dismiss();  
            } else {  
                initmPopupWindowView();  
                popupwindow.showAsDropDown(myaccount);
            }  
		}	
	};
	private void threads(final int pageNum, final int ActType){
		Thread threadMobile = new Thread(new Runnable(){
			@SuppressWarnings("unchecked")
			@Override
			public void run() {
				HttpPost httppost = new HttpPost(queryURL);
				// TODO Auto-generated method stub
				MyHttpClient.myHttpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 15000);
				List<NameValuePair>pair = new ArrayList<NameValuePair>();
				pair.add(new BasicNameValuePair("page", String.valueOf(pageNum)));
				// 读取超时
				MyHttpClient.myHttpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 15000);
				try {
					httppost.setEntity(new UrlEncodedFormEntity(pair, "utf-8"));
					HttpResponse response = MyHttpClient.myHttpClient.execute(httppost);
					String str = EntityUtils.toString(response.getEntity(), "utf-8");
					JSONObject jsonObject = new JSONObject(str); 
					Map result =  new HashMap();
					tempDataLists.clear();
					Iterator iterator = jsonObject.keys();
					String key = null;
					String value = null;      
					while (iterator.hasNext()) {
						key = (String) iterator.next();
						if(key.equals("messages")){
							JSONArray resultJSON=jsonObject.getJSONArray(key);
							for(int i = 0; i < resultJSON.length();i++){
								if(ActType==0){
									mDataList.add(JSONoperate(resultJSON.getJSONObject(i)));
								}
								else{
									tempDataLists.add(JSONoperate(resultJSON.getJSONObject(i)));
								}
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
				if(ActType==0){
					handler1.obtainMessage(1, isSuccess).sendToTarget();
				} 
				else if(ActType==1){
					handler1.obtainMessage(2, isSuccess).sendToTarget();
				}
				else if(ActType==2){
					handler1.obtainMessage(3, isSuccess).sendToTarget();
				}
			}
		});
		mpd = ProgressDialog.show(MenuActivity.this, "Requesting", "Requesting...");
		threadMobile.start();
	}
	@SuppressLint("InflateParams") public void initmPopupWindowView() {  
		LayoutInflater inflater = (LayoutInflater) this  
                .getSystemService(LAYOUT_INFLATER_SERVICE);  
        View layout = inflater.inflate(R.layout.task_detail_popupwindow, null);  
        lvPopupList = (ListView) layout.findViewById(R.id.lv_popup_list);  
        popupwindow = new PopupWindow(layout);  
        popupwindow.setFocusable(true);// 加上这个popupwindow中的ListView才可以接收点击事件  
        ColorDrawable dw = new ColorDrawable(0000000000);
        popupwindow.setBackgroundDrawable(dw);
        lvPopupList.setAdapter(new SimpleAdapter(MenuActivity.this, moreList,  
                R.layout.list_item_popupwindow, new String[] { "share_key" },  
                new int[] { R.id.tv_list_item }));  
        lvPopupList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
                 String contents = moreList.get(position).get("share_key").toString();
                 if(contents.equals("登录")){
                	 Intent intent = new Intent();
					 intent.setClass(MenuActivity.this, Login.class);
					 startActivityForResult(intent, 0);
					 if(popupwindow.isShowing()){
						 popupwindow.dismiss();
					 }
                 }  
                 if(contents.equals("退出")){
                	 finish();
                 }
                 if(contents.equals("我的信息")){
                	 Intent intent = new Intent();
					 intent.setClass(MenuActivity.this, MyInformation.class);
					 startActivity(intent);
					 if(popupwindow.isShowing()){
						 popupwindow.dismiss();
					 }
                 }
                 if(contents.equals("登出")){
     				Thread threadMobile = new Thread(new Runnable(){
     					@Override
     					public void run() {
     						// TODO Auto-generated method stub
     						HttpPost httppost = new HttpPost(logoutURL);
     						MyHttpClient.myHttpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 15000);
			                // 读取超时
							MyHttpClient.myHttpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 15000);
							try {
								HttpResponse response = MyHttpClient.myHttpClient.execute(httppost);
							} catch (ClientProtocolException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
     					}
     				});
     				threadMobile.start();
                	MyHttpClient.myHttpClient = new DefaultHttpClient(); 
					if(popupwindow.isShowing()){
						popupwindow.dismiss();
					}
					listinit();
                 }  
			}  
  
        });  
  
        // 控制popupwindow的宽度和高度自适应  
        lvPopupList.measure(View.MeasureSpec.UNSPECIFIED,  
                View.MeasureSpec.UNSPECIFIED);  
        popupwindow.setWidth(lvPopupList.getMeasuredWidth());  
        popupwindow.setHeight((lvPopupList.getMeasuredHeight()+10)  
                * NUM_OF_VISIBLE_LIST_ROWS);  
  
        // 控制popupwindow点击屏幕其他地方消失  
        popupwindow.setBackgroundDrawable(this.getResources().getDrawable(  
                R.color.halfnolor));// 设置背景图片，不能在布局中设置，要通过代码来设置  
        popupwindow.setOutsideTouchable(true);// 触摸popupwindow外部，popupwindow消失。
        										//这个要求你的popupwindow要有背景图片才可以成功，如上  
	}
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
    			Log.i("time", value);
    			result.put(key, value);
    		}
    		else {
    			value = jsonItem.getString(key);
        		result.put(key, value);
    		}
    	}
    	return result;
    }
	@Override
    protected void onActivityResult(int requestCode, int resultCode,
            Intent intent) {
        // 当requestCode和resultCode都为0时（处理特定的结果）。
        if (requestCode == 0 && resultCode == 0) {
        	Bundle bundle = intent.getExtras();
        	if(bundle.getBoolean("Success")){
        		listChange();
        	}
        }
        if(requestCode == 0 && resultCode == 1){
        	pageNumber = 0;
    		threads(pageNumber,1);
        }
    }
	private void init()
	{
		tab1 = (TextView) findViewById(R.id.user_ctrl_ch_custom_tab1);
		tab1.setOnClickListener(onClickListener);
		tab3 = (TextView) findViewById(R.id.user_ctrl_ch_custom_tab3);
		tab3.setOnClickListener(onClickListener);
		tab4 = (TextView) findViewById(R.id.user_ctrl_ch_custom_tab4);
		tab4.setOnClickListener(onClickListener);
	}
	private void listinit(){
		moreList.clear();  
		NUM_OF_VISIBLE_LIST_ROWS = 2;
        Map<String, String> map;  
        map = new HashMap<String, String>();  
        map.put("share_key", "登录");  
        moreList.add(map);   
        map = new HashMap<String, String>();  
        map.put("share_key", "退出");  
        moreList.add(map); 
	}
	private void listChange(){
		
		moreList.clear();  
		NUM_OF_VISIBLE_LIST_ROWS = 3;
        Map<String, String> map;  
        map = new HashMap<String, String>();  
        map.put("share_key", "我的信息");  
        moreList.add(map);   
        map = new HashMap<String, String>();  
        map.put("share_key", "登出");  
        moreList.add(map); 
        map = new HashMap<String, String>();  
        map.put("share_key", "退出");  
        moreList.add(map);
	}
	private OnClickListener onClickListener = new OnClickListener()
	{
		public void onClick(View v)
		{
			switch (v.getId())
			{
				case R.id.user_ctrl_ch_custom_tab3:
					if (current != R.id.user_ctrl_ch_custom_tab3)
					{
						Intent intent = new Intent();
						Bundle bundle = new Bundle();
						bundle.putBoolean("flag", false);
						intent.putExtras(bundle);
						intent.setClass(MenuActivity.this, Release.class);
						startActivityForResult(intent, 0);
					}
					break;
				case R.id.user_ctrl_ch_custom_tab4:
					if (current != R.id.user_ctrl_ch_custom_tab4)
					{
						Intent intent = new Intent();
						intent.setClass(MenuActivity.this, Search.class);
						startActivity(intent);
					}
					break;
				default:
					break;
			}
		}
	};
	public void onRefresh() {
		pageNumber = 0;
		threads(pageNumber,1);
	}
	public void onLoad() {
		pageNumber += 1;
		threads(pageNumber,2);
	}
	@Override  
    public boolean onKeyDown(int keyCode, KeyEvent event)  
    {  
        if (keyCode == KeyEvent.KEYCODE_BACK )  
        {  
			MenuActivity.this.finish();
        }        
        return false;     
    }  
}
