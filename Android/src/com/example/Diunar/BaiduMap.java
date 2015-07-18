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
import com.baidu.location.LocationClientOption;//�����õ�λ�����ѹ��ܣ���Ҫimport����
//���ʹ�õ���Χ�����ܣ���Ҫimport������
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
        //��ʹ��SDK�����֮ǰ��ʼ��context��Ϣ������ApplicationContext  
        //ע��÷���Ҫ��setContentView����֮ǰʵ��  
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
        option.setLocationMode(LocationMode.Hight_Accuracy);//���ö�λģʽ
        option.setOpenGps(true);
        option.setCoorType("bd09ll");//���صĶ�λ����ǰٶȾ�γ��,Ĭ��ֵgcj02
        option.setScanSpan(10000);//���÷���λ����ļ��ʱ��Ϊ5000ms
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
    			Toast.makeText(BaiduMap.this,  
                        "λ�ã�" + location.getAddrStr(), Toast.LENGTH_LONG)  
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
        // ����������ѯ����ص�����  
        @Override  
        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {  
            if (result == null  
                    || result.error != SearchResult.ERRORNO.NO_ERROR) {  
                // û�м�⵽���  
                Toast.makeText(BaiduMap.this, "��Ǹ��δ���ҵ����",  
                        Toast.LENGTH_LONG).show();  
            }  
            else {
            	Toast.makeText(BaiduMap.this,  
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
        //��activityִ��onDestroyʱִ��mMapView.onDestroy()��ʵ�ֵ�ͼ�������ڹ���  
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
        //��activityִ��onResumeʱִ��mMapView. onResume ()��ʵ�ֵ�ͼ�������ڹ���  
        mMapView.onResume();  
    }  
    @Override  
    protected void onPause() {  
        super.onPause();  
        //��activityִ��onPauseʱִ��mMapView. onPause ()��ʵ�ֵ�ͼ�������ڹ���  
       if(mMapView != null){
    	   mMapView.onPause();
       }
    }  
}
