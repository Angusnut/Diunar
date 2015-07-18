package com.example.Diunar;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;//假如用到位置提醒功能，需要import该类
//如果使用地理围栏功能，需要import如下类
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.*;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class BaiduMap extends Activity {  
    private MapView mMapView = null;  
    private ImageButton back;
    private com.baidu.mapapi.map.BaiduMap mBaiduMap;
    private LocationClient locationClient = null;
    private Marker marker;
    private boolean flag = false;
    private Button myLocation;
    private TextView submit;
    private double longitude,latitude; 
    private String locations;
    private GeoCoder geocoder;
    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);   
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext  
        //注意该方法要再setContentView方法之前实现  
        SDKInitializer.initialize(getApplicationContext());  
        setContentView(R.layout.baidumap);      
        mMapView = (MapView) findViewById(R.id.bmapView);  
        back = (ImageButton)findViewById(R.id.backs);
        back.setOnClickListener(backListener);
        mBaiduMap = mMapView.getMap(); 
        myLocation = (Button)findViewById(R.id.mylocation);
        myLocation.setOnClickListener(myLocationListener);
        submit = (TextView)findViewById(R.id.mapsubmit);
        submit.setOnClickListener(submitListener);
        locationClient = new LocationClient(this);
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationMode.Hight_Accuracy);//设置定位模式
        option.setOpenGps(true);
        option.setCoorType("bd09ll");//返回的定位结果是百度经纬度,默认值gcj02
        option.setScanSpan(10000);//设置发起定位请求的间隔时间为5000ms
        option.setIsNeedAddress(true);//返回的定位结果包含地址信息
        option.setNeedDeviceDirect(true);//返回的定位结果包含手机机头的方向
        locationClient.setLocOption(option);
        //注册位置监听器
        locationClient.registerLocationListener(new BDLocationListener() {        
        	@Override
    		public void onReceiveLocation(BDLocation location) {
    			if (location == null)
    		            return ;
    			mBaiduMap.setMyLocationEnabled(true); 
    			MyLocationData locData = new MyLocationData.Builder()  
    		    .accuracy(location.getRadius())  
    		    // 此处设置开发者获取到的方向信息，顺时针0-360  
    		    .direction(100).latitude(location.getLatitude())  
    		    .longitude(location.getLongitude()).build();  
    			mBaiduMap.setMyLocationData(locData); 
    			Toast.makeText(BaiduMap.this,  
                        "位置：" + location.getAddrStr(), Toast.LENGTH_LONG)  
                        .show();
    			LatLng ll = new LatLng(location.getLatitude(),
    		            location.getLongitude());
    			longitude = location.getLongitude();
    			latitude = location.getLatitude();
    			locations = location.getAddrStr();
    			float f = mBaiduMap.getMaxZoomLevel();
    		    MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll,f-2);
    		    mBaiduMap.animateMapStatus(u);
    			//mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration
    			//		(null, true, mCurrentMarker));
    			//mBaiduMap.setMyLocationEnabled(false);
    		}
        });
        mBaiduMap.setOnMapClickListener(mBaiduMapListerner);
        locationClient.start();
        locationClient.requestLocation(); 
    } 
    private OnClickListener submitListener = new OnClickListener(){
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Bundle bundle = new Bundle();
			bundle.putDouble("longitude", longitude);
			bundle.putDouble("latitude", latitude);
			bundle.putString("location", locations);
			Intent intent = new Intent();
			intent.putExtras(bundle);
			//intent.setClass(BaiduMap.this,Release.class);
			//startActivity(intent);
			//overridePendingTransition(R.anim.keep, R.anim.in_from_left);
			BaiduMap.this.setResult(0, intent);
			BaiduMap.this.finish();
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
			longitude = arg0.longitude;
			latitude = arg0.latitude;
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
        // 反地理编码查询结果回调函数  
        @Override  
        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {  
            if (result == null  
                    || result.error != SearchResult.ERRORNO.NO_ERROR) {  
                // 没有检测到结果  
                Toast.makeText(BaiduMap.this, "抱歉，未能找到结果",  
                        Toast.LENGTH_LONG).show();  
            }  
            else {
            	Toast.makeText(BaiduMap.this,  
                    "位置：" + result.getAddress(), Toast.LENGTH_LONG)  
                    .show();
            	locations = result.getAddress();
            }
        }  

        // 地理编码查询结果回调函数  
        @Override  
        public void onGetGeoCodeResult(GeoCodeResult result) {  
            if (result == null  
                    || result.error != SearchResult.ERRORNO.NO_ERROR) {  
                // 没有检测到结果  
            }  
        }  
    };  
    private OnClickListener backListener = new OnClickListener(){
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Bundle bundle = new Bundle();
			bundle.putDouble("longitude", 0);
			bundle.putDouble("latitude", 0);
			bundle.putString("location", null);
			Intent intent = new Intent();
			intent.putExtras(bundle);
			//intent.setClass(BaiduMap.this,Release.class);
			//startActivity(intent);
			//overridePendingTransition(R.anim.keep, R.anim.in_from_left);
			BaiduMap.this.setResult(0, intent);
			BaiduMap.this.finish();
		}
    };
    @Override  
    protected void onDestroy() {  
        super.onDestroy();  
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理  
        mMapView.onDestroy();  
        if (locationClient != null && locationClient.isStarted()) {
            locationClient.stop();
            locationClient = null;
        }
    }  
    @Override  
    public boolean onKeyDown(int keyCode, KeyEvent event)  
    {  
        if (keyCode == KeyEvent.KEYCODE_BACK )  
        {  
        	Bundle bundle = new Bundle();
			bundle.putDouble("longitude", 0);
			bundle.putDouble("latitude", 0);
			bundle.putString("location", null);
			bundle.putBoolean("mapback", true);
			Intent intent = new Intent();
			intent.putExtras(bundle);
			BaiduMap.this.setResult(0, intent);
			BaiduMap.this.finish();
        }  
          
        return false;  
          
    }   
    
	@Override  
    protected void onResume() {  
        super.onResume();  
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理  
        mMapView.onResume();  
    }  
    @Override  
    protected void onPause() {  
        super.onPause();  
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理  
       if(mMapView != null){
    	   mMapView.onPause();
       }
    }  
}
