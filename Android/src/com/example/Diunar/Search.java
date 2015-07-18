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

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.example.Diunar.AutoListView.OnLoadListener;
import com.example.Diunar.AutoListView.OnRefreshListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class Search extends Activity implements OnLoadListener, OnRefreshListener{
	private EditText searchcontent, year, month, day, hour;
	private static List<Map<String, Object>> mDataList = new ArrayList<Map<String, Object>>();
	private static SimpleAdapter listItemAdapter;
	private com.baidu.mapapi.map.BaiduMap mBaiduMap, mBaiduMap1;
	private MapView mMapView = null, mMapView1 = null; 
	private ImageButton back;
	private InfoWindow mInfoWindow;
	private Button search, switchs;
	private String hint;
	private boolean isSuccess, isOK = false;
	private final String URL = "http://172.18.33.95:8888/mobile/";
	private final String queryURL=URL + "queryMessage";
	private Spinner spinner;
	private int pageNumber;
	private CheckBox tag, catalory, date, location;
    private double mylongitude, mylatitude;
    private double locallatitude, locallongitude;
    private int size, sizepre = -1, sizenow = -1; 
    private RadioGroup radiogroup;
    private String locations, type, years, months, days, hours, tags;
    private GeoCoder geocoder;
    private LocationClient locationClient;
    private AutoListView myAutoListView;
    private Marker marker;
    private boolean flag = false;
    private class MySimpleAdapter extends SimpleAdapter{
    	public MySimpleAdapter(Context context,
    			List<? extends Map<String, ?>> data, int resource,
    					String[] from, int[] to) {
    		 super(context, data, resource, from, to);
    		// TODO Auto-generated constructor stub
    	}
    	//��дgetView()
    	@Override
    	public View getView(int position, View convertView, ViewGroup parent) {
    		// TODO Auto-generated method stub    		
    		View v= super.getView(position, convertView, parent);
    		return v;
    	}
    };
	protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);   
        SDKInitializer.initialize(getApplicationContext()); 
        setContentView(R.layout.search); 
        mMapView = (MapView) findViewById(R.id.bmapView2); 
        mMapView1 = (MapView) findViewById(R.id.bmapView3);
        back = (ImageButton)findViewById(R.id.back2);
        back.setOnClickListener(backListener);  
        search = (Button)findViewById(R.id.searchfor);
        catalory = (CheckBox)findViewById(R.id.checkBox12);
        date = (CheckBox)findViewById(R.id.checkBox13);
        location = (CheckBox)findViewById(R.id.checkBox14);
        switchs = (Button)findViewById(R.id.switchs);
        switchs.setOnClickListener(switchsListener);
        switchs.setText("��ͼ");
        catalory.setOnCheckedChangeListener(checkboxlistener);
        date.setOnCheckedChangeListener(checkboxlistener);
        location.setOnCheckedChangeListener(checkboxlistener);
        search.setOnClickListener(searchListener);
        myAutoListView = (AutoListView)findViewById(R.id.autoListView2);
        ArrayList<String> data_list = new ArrayList<String>();
        data_list.add("����");
        data_list.add("��");
        data_list.add("�ֻ�");
        data_list.add("����");
        data_list.add("Ǯ��");
        data_list.add("֤��");
        data_list.add("Կ��");
        data_list.add("�·�");
        data_list.add("����");
        data_list.add("����");
        spinner=(Spinner)findViewById(R.id.spinner1);
        ArrayAdapter<String> accountTypesAdapter = new ArrayAdapter<String>(this, 
        		android.R.layout.simple_spinner_item, data_list); 
        accountTypesAdapter.setDropDownViewResource(R.layout.dropdown_item); 
        spinner.setAdapter(accountTypesAdapter);
        spinner.setOnItemSelectedListener(spinnerListener);
		listItemAdapter = new MySimpleAdapter(Search.this,
        		mDataList,
                R.layout.list_view,
                new String[] {"type","title","description","time"}, 
                new int[] {R.id.item_cata,R.id.item_title,R.id.item_content,
				R.id.iten_date});
		
		 mBaiduMap = mMapView.getMap(); 
		 mBaiduMap1 = mMapView1.getMap();
		 Button myLocation;
		 myLocation = (Button)findViewById(R.id.mylocation1);
		 myLocation.setOnClickListener(myLocationListener);
		 locationClient = new LocationClient(this);
		 LocationClientOption option = new LocationClientOption();
	     option.setLocationMode(LocationMode.Hight_Accuracy);//���ö�λģʽ
	     option.setOpenGps(true);
	     option.setCoorType("bd09ll");//���صĶ�λ����ǰٶȾ�γ��,Ĭ��ֵgcj02
	     option.setScanSpan(60000);//���÷���λ����ļ��ʱ��Ϊ5000ms
	     option.setIsNeedAddress(true);//���صĶ�λ���������ַ��Ϣ
	     option.setNeedDeviceDirect(true);//���صĶ�λ��������ֻ���ͷ�ķ���
	     locationClient.setLocOption(option);
	     //ע��λ�ü�����
	     locationClient.registerLocationListener(new BDLocationListener() {        
	    	@Override
	    	public void onReceiveLocation(BDLocation location) {
	    		if (location == null)
	    			return ;
	    		mBaiduMap.setMyLocationEnabled(true); 
	    		MyLocationData locData = new MyLocationData.Builder()  
	    		.accuracy(location.getRadius())  
	    		// �˴����ÿ����߻�ȡ���ķ�����Ϣ��˳ʱ��0-360  
	    		.direction(100).latitude(location.getLatitude())  
	    		.longitude(location.getLongitude()).build();  
	    		mBaiduMap.setMyLocationData(locData); 
	    		LatLng ll = new LatLng(location.getLatitude(),
	    		    location.getLongitude());
	    		mylongitude = location.getLongitude();
	    		mylatitude = location.getLatitude();
	    		Log.i("location", String.valueOf(mylongitude));
	    		locations = location.getAddrStr();
	    		if(isOK){
	    			Toast.makeText(Search.this,  
	                    "λ�ã�" + location.getAddrStr(), Toast.LENGTH_LONG)  
	                    .show();
	    		}
	    		isOK = true;
	    		float f = mBaiduMap.getMaxZoomLevel();
	    		MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll,f-2);
	    		mBaiduMap.animateMapStatus(u);
	    		locationClient.stop();
	    			//mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration
	    			//		(null, true, mCurrentMarker));
	    			//mBaiduMap.setMyLocationEnabled(false);
	    	}
	    });
	    mBaiduMap.setOnMapClickListener(mBaiduMapListerner);
    	locationClient.start();
    	mBaiduMap1.setOnMarkerClickListener(markerListener);
    }
	private OnClickListener switchsListener = new OnClickListener(){
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(switchs.getText().equals("�б�")){
				switchs.setText("��ͼ");
				myAutoListView.setVisibility(View.VISIBLE);
				mMapView1.setVisibility(View.GONE);
			}
			else {
				switchs.setText("�б�");
				myAutoListView.setVisibility(View.GONE);
				mMapView1.setVisibility(View.VISIBLE);
			}
		}		
	};
	@SuppressWarnings("unused")
	private void mark(Double latitude, Double longitude, boolean flag){
		LatLng point = new LatLng(latitude, longitude);  
		float f = mBaiduMap1.getMaxZoomLevel();
	    MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(point,f-4);
	    mBaiduMap1.animateMapStatus(u);
	    BitmapDescriptor bitmap;
	    //����Markerͼ��  
	    if(flag){
	    	bitmap = BitmapDescriptorFactory  
	    			.fromResource(R.drawable.mapicon1);  
	    }
	    else {
	    	bitmap = BitmapDescriptorFactory  
	    			.fromResource(R.drawable.mapicon);  
	    }
	    //����MarkerOption�������ڵ�ͼ�����Marker  
	    OverlayOptions option = new MarkerOptions()  
          .position(point)  
          .icon(bitmap);  
	    //�ڵ�ͼ�����Marker������ʾ  
	    Marker mark = (Marker) mBaiduMap1.addOverlay(option);
	};
	private OnMarkerClickListener markerListener = new OnMarkerClickListener() {  
	      
	    @SuppressLint("ResourceAsColor") @Override  
	    public boolean onMarkerClick(Marker arg0) {  
	        // TODO Auto-generated method stub  
	    	LatLng pt1 = arg0.getPosition();
	    	float f = mBaiduMap1.getMaxZoomLevel();
	    	Button button = new Button(getApplicationContext());  
	    	button.setBackgroundResource(R.drawable.popup);
	    	button.setText("������Ϣ");
	    	button.setTag(pt1);
	    	button.setOnClickListener(buttonListener);
	    	button.setTextColor(R.color.black);
	    	//����������ʾ��InfoWindow�������  
	    	LatLng pt = new LatLng(pt1.latitude+0.00055, pt1.longitude+0.0007);  
	    	//����InfoWindow , ���� view�� �������꣬ y ��ƫ���� 
	    	mInfoWindow = new InfoWindow(button, pt, -47);  
	    	//��ʾInfoWindow  
	    	mBaiduMap1.showInfoWindow(mInfoWindow);  
	    	MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(pt1,f-4);
		    mBaiduMap1.animateMapStatus(u);
	        return false;  
	    }  
	};
	private OnClickListener buttonListener = new OnClickListener(){
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			LatLng pt = (LatLng) v.getTag();
			for(int i = 0; i < mDataList.size(); i++){
				Map<String, Object> map = mDataList.get(i);
				if(map.get("x").toString().equals(String.valueOf(pt.latitude)) 
						&&map.get("y").toString().equals(String.valueOf(pt.longitude))){
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
					intent.setClass(Search.this, ListItemDetails.class);
					intent.putExtras(bundle);
					startActivity(intent);	
				}
			}
		}
	};
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if(msg.what==1){
				if(isSuccess){
					myAutoListView.setResultSize(size);
					sizenow = mDataList.size();
					if(sizenow != 0){
						listItemAdapter.notifyDataSetChanged();
					}
					//isOK = false;
					//locationClient.start();
				}
			}
			if(msg.what==2){
				if(isSuccess){
					myAutoListView.onLoadComplete();
					myAutoListView.setResultSize(size);
					listItemAdapter.notifyDataSetChanged();
				}
			}
		};
	};
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
			intent.setClass(Search.this, ListItemDetails.class);
			intent.putExtras(bundle);
			startActivity(intent);	
		}
    };
    private OnItemSelectedListener spinnerListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			// TODO Auto-generated method stub
			tags = parent.getItemAtPosition(position).toString();
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub
			
		}
    };
	private OnClickListener searchListener = new OnClickListener(){
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			pageNumber = 0;
			mDataList.clear();
			mBaiduMap1.clear();
			myAutoListView.setVisibility(View.VISIBLE);
			switchs.setVisibility(View.VISIBLE);
			mark(mylatitude, mylongitude, true);
			location.setChecked(false);
			myAutoListView.setAdapter(listItemAdapter);
			myAutoListView.setOnItemClickListener(mItemClickListener);
			myAutoListView.setOnRefreshListener(Search.this);
			myAutoListView.setOnLoadListener(Search.this);
			Thread thread = new Thread(runable);
			thread.start();
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
			if(catalory.isChecked()){
				radiogroup = (RadioGroup)findViewById(R.id.radioGroup111);
				RadioButton radiobutton = (RadioButton)findViewById(radiogroup.getCheckedRadioButtonId());
				type = radiobutton.getText().toString().equals("������")?"0":"1";
				pair.add(new BasicNameValuePair("type", type));
			}
			if(date.isChecked()){
				year = (EditText)findViewById(R.id.years);
				month = (EditText)findViewById(R.id.months);
				day = (EditText)findViewById(R.id.days);
				years = year.getText().toString();
				String temp = day.getText().toString();
				days = Integer.valueOf(temp)>9?temp:"0"+temp;
				temp = month.getText().toString();
				months = Integer.valueOf(temp)>9?temp:"0"+temp;
				pair.add(new BasicNameValuePair("time", years+"-"+months+"-"+days));
				Log.i("time", years+"-"+months+"-"+days);
			}
			Log.i("mylocation1",String.valueOf(mylatitude));
			if(mylatitude != 0.0){
				Log.i("str", String.valueOf(location.isChecked()));
				pair.add(new BasicNameValuePair("x", String.valueOf(mylatitude)));
				pair.add(new BasicNameValuePair("y", String.valueOf(mylongitude)));
			}
			Log.i("mylocation2",String.valueOf(mylatitude));
			if(!tags.equals("����")){
				pair.add(new BasicNameValuePair("tag", tags));
			}
			pair.add(new BasicNameValuePair("page", String.valueOf(pageNumber)));
			// ��ȡ��ʱ
			MyHttpClient.myHttpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 15000);
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
					if(key.equals("messages")){
						JSONArray resultJSON=jsonObject.getJSONArray(key);
						for(int i = 0; i < resultJSON.length();i++){
							mDataList.add(JSONoperate(resultJSON.getJSONObject(i)));
						}
						size = resultJSON.length();
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
			if(pageNumber == 0){
				handler.obtainMessage(1, isSuccess).sendToTarget();
			}
			else {
				handler.obtainMessage(2, isSuccess).sendToTarget();
			}
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
    			mark(Double.valueOf(resultJSON.getString("x")),Double.valueOf(resultJSON.getString("y")),false);
    			result.put("x", resultJSON.getString("x"));
    			result.put("y", resultJSON.getString("y"));
    			result.put("place", resultJSON.getString("place"));
    		}
    		else if(key.equals("type")){
    			value = jsonItem.getString(key);
    			value = value.equals("0")?"������":"����";
    			result.put(key, value);
    		}
    		else if (key.equals("description")){
    			value = jsonItem.getString(key);
    			value = value.equals("")?"  ����Ʒû������":"  "+value;
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
	private OnCheckedChangeListener checkboxlistener = new OnCheckedChangeListener(){

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			// TODO Auto-generated method stub
			switch(buttonView.getId())
			{
				case R.id.checkBox12:
					LinearLayout l1 = (LinearLayout)findViewById(R.id.leibie);
					if(l1.getVisibility() == View.GONE){
						l1.setVisibility(View.VISIBLE);
					} else {
						l1.setVisibility(View.GONE);
					}
					break;
				case R.id.checkBox13:
					LinearLayout l2 = (LinearLayout)findViewById(R.id.shijian);
					if(l2.getVisibility()== View.GONE){
						l2.setVisibility(View.VISIBLE);
						Time t=new Time(); // or Time t=new Time("GMT+8"); ����Time Zone���ϡ�  
						t.setToNow(); // ȡ��ϵͳʱ�䡣
						EditText y, m, d, h;
						y = (EditText)findViewById(R.id.years);
						m = (EditText)findViewById(R.id.months);
						d = (EditText)findViewById(R.id.days);
						y.setText(String.valueOf(t.year));
						m.setText(String.valueOf(t.month+1));
						d.setText(String.valueOf(t.monthDay));
					} else {
						l2.setVisibility(View.GONE);
					}
					break;
				case R.id.checkBox14:
					LinearLayout l3 = (LinearLayout)findViewById(R.id.map);
					if(l3.getVisibility()== View.GONE){
						l3.setVisibility(View.VISIBLE);
						mMapView.setClickable(true);
						mBaiduMap = mMapView.getMap();
					} else {
						l3.setVisibility(View.GONE);
						if (locationClient != null && locationClient.isStarted()) {
							locationClient.stop();
							mMapView.setClickable(false);
							mBaiduMap = null;
						}
					}
					break;
				default:
					break;
			}
		}
		
	};
	private OnMapClickListener mBaiduMapListerner = new OnMapClickListener(){
		@SuppressWarnings("null")
		@Override
		public void onMapClick(LatLng arg0) {
			// TODO Auto-generated method stub
			if(locationClient.isStarted()){
				locationClient.stop();
			}
			if(flag){
				marker.remove();
			}
			mylongitude = arg0.longitude;
			mylatitude = arg0.latitude;
			Log.i("locationsssssss", String.valueOf(mylongitude));
			flag = true;
			BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.mapicon);
			OverlayOptions option = new MarkerOptions().position(arg0)
					.icon(bitmap);
			marker = (Marker) mBaiduMap.addOverlay(option);

			geocoder = GeoCoder.newInstance();
			geocoder.setOnGetGeoCodeResultListener(listener); 
			geocoder.reverseGeoCode(new ReverseGeoCodeOption().location(arg0));
			MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(arg0);
			mBaiduMap.animateMapStatus(u);
		}
		@Override
		public boolean onMapPoiClick(MapPoi arg0) {
			// TODO Auto-generated method stub
			return false;
		}	
	 };
	private OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {  
	        // ����������ѯ����ص�����  
	        @Override  
	        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {  
	            if (result == null  
	                    || result.error != SearchResult.ERRORNO.NO_ERROR) {  
	                // û�м�⵽���  
	                Toast.makeText(Search.this, "��Ǹ��δ���ҵ����",  
	                        Toast.LENGTH_LONG).show();  
	            }  
	            else {
	            	Toast.makeText(Search.this,  
	                    "λ�ã�" + result.getAddress(), Toast.LENGTH_LONG)  
	                    .show();
	            	locations = result.getAddress();
	            }
	        }  

	        // ��������ѯ����ص�����  
	        @Override  
	        public void onGetGeoCodeResult(GeoCodeResult result) {  
	            if (result == null  
	                    || result.error != SearchResult.ERRORNO.NO_ERROR) {  
	                // û�м�⵽���  
	            }  
	        }  
	    };  
	private OnClickListener myLocationListener = new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!locationClient.isStarted()){
					locationClient.start();
				}
				if(flag){
					marker.remove();
					flag = false;
				}
			}
	};
	private OnClickListener backListener = new OnClickListener(){
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Search.this.finish();
		}
    };  
    @Override  
    public boolean onKeyDown(int keyCode, KeyEvent event)  
    {  
        if (keyCode == KeyEvent.KEYCODE_BACK )  
        {  
			Search.this.finish();
        }       
        return false;     
    }  
    public void onRefresh() {
    	myAutoListView.onRefreshComplete();
	}
	public void onLoad() {
		pageNumber += 1;
		Thread thread = new Thread(runable);
		thread.start();
	}
    @Override  
    protected void onDestroy() {  
        super.onDestroy();  
        //��activityִ��onDestroyʱִ��mMapView.onDestroy()��ʵ�ֵ�ͼ�������ڹ���  
        mMapView.onDestroy();  
        if (locationClient != null && locationClient.isStarted()) {
            locationClient.stop();
            locationClient = null;
        }
    } 
    @Override  
    protected void onResume() {  
        super.onResume();  
        //��activityִ��onResumeʱִ��mMapView. onResume ()��ʵ�ֵ�ͼ�������ڹ���  
        mMapView.onResume();  
    }  
    @Override  
    protected void onPause() {  
        super.onPause();  
        //��activityִ��onPauseʱִ��mMapView. onPause ()��ʵ�ֵ�ͼ�������ڹ���  
        mMapView.onPause();  
    }  

}