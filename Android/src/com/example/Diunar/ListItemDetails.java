package com.example.Diunar;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class ListItemDetails extends Activity{
	private MapView mMapView = null;  
	private com.baidu.mapapi.map.BaiduMap mBaiduMap;
	private TextView tag, phonenum, descr, place, type, title, mytitles;
	private double latitude, longitude;
	private Button delete, motify;
	private ImageButton back;
	private boolean ischanged = false;
    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);   
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext  
        //注意该方法要再setContentView方法之前实现  
        SDKInitializer.initialize(getApplicationContext());  
        setContentView(R.layout.list_item_details);  
        //获取地图控件引用  
        mMapView = (MapView) findViewById(R.id.bmapView3);
        mBaiduMap = mMapView.getMap();  
        tag = (TextView)findViewById(R.id.itsTag);
        phonenum = (TextView)findViewById(R.id.itsCont);
        descr = (TextView)findViewById(R.id.itsDescri);
        place = (TextView)findViewById(R.id.itsLocation);
        type = (TextView)findViewById(R.id.itsCata);
        title = (TextView)findViewById(R.id.itsTitle);
        back = (ImageButton)findViewById(R.id.back132);
        back.setOnClickListener(l);
        Bundle bundle = this.getIntent().getExtras();
        init(bundle);
        if(bundle.getBoolean("flag")){
        	delete = (Button)findViewById(R.id.deleteItem);
        	motify = (Button)findViewById(R.id.motifyItem);
        	delete.setVisibility(View.VISIBLE);
        	motify.setVisibility(View.VISIBLE);
        	delete.setOnClickListener(deleteListener);
        	motify.setOnClickListener(motifyListener);
        }
    }  
    private void init(Bundle bundle){
    	tag.setText(bundle.getString("tag"));
        phonenum.setText(bundle.getString("contactInfo"));
        descr.setText(bundle.getString("description"));
        place.setText(bundle.getString("place"));
        type.setText(bundle.getString("type"));
        title.setText(bundle.getString("title"));
        latitude = Double.valueOf(bundle.getString("x"));
        longitude = Double.valueOf(bundle.getString("y"));
        LatLng point = new LatLng(latitude, longitude);  
        float f = mBaiduMap.getMaxZoomLevel();
	    MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(point,f-2);
	    mBaiduMap.animateMapStatus(u);
	    //构建Marker图标  
	    BitmapDescriptor bitmap = BitmapDescriptorFactory  
          .fromResource(R.drawable.mapicon);  
	    //构建MarkerOption，用于在地图上添加Marker  
	    OverlayOptions option = new MarkerOptions()  
          .position(point)  
          .icon(bitmap);  
	    //在地图上添加Marker，并显示  
	    mBaiduMap.clear();
	    mBaiduMap.addOverlay(option);
    }
    private OnClickListener deleteListener = new OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Bundle bundles = getIntent().getExtras();
			Bundle bundle = new Bundle();
			bundle.putBoolean("Delete", true);		
			bundle.putString("_id", bundles.getString("_id"));
			bundle.putInt("position", bundles.getInt("position"));
			Intent intent = new Intent();
			intent.putExtras(bundle);
			//intent.setClass(BaiduMap.this,Release.class);
			//startActivity(intent);
			//overridePendingTransition(R.anim.keep, R.anim.in_from_left);
			setResult(0, intent);
			finish();
		}
    };
    private OnClickListener motifyListener = new OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			ischanged = true;
			Bundle bundles = getIntent().getExtras();
			Bundle bundle = new Bundle();
			bundle.putString("title", bundles.getString("title").toString());
			bundle.putString("tag", bundles.getString("tag").toString());
			bundle.putString("type", bundles.getString("type").toString());
			bundle.putString("description", bundles.getString("description").toString());
			bundle.putString("time", bundles.getString("time").toString());
			bundle.putString("place",bundles.getString("place").toString());
			bundle.putString("x",bundles.getString("x").toString());
			bundle.putString("y",bundles.getString("y").toString());
			bundle.putString("_id",bundles.getString("_id").toString());
			bundle.putInt("position", bundles.getInt("position"));
			bundle.putBoolean("flag", true);
			bundle.putString("contactInfo",bundles.getString("contactInfo").toString());
			Intent intent = new Intent();
			intent.putExtras(bundle);
			//intent.setClass(BaiduMap.this,Release.class);
			//startActivity(intent);
			//overridePendingTransition(R.anim.keep, R.anim.in_from_left);
			intent.setClass(ListItemDetails.this, Release.class);
			startActivityForResult(intent, 0);
		}
    	
    };
    protected void onActivityResult(int requestCode, int resultCode,
            Intent intent) {
        // 当requestCode和resultCode都为0时（处理特定的结果）。
        if (requestCode == 0 && resultCode == 1) {
        	Bundle bundles = intent.getExtras();
        	init(bundles);
        }
    }
    private OnClickListener l = new OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Bundle bundles = getIntent().getExtras();
			Bundle bundle = new Bundle();
			bundle.putBoolean("Delete", true);		
			bundle.putString("_id", bundles.getString("_id"));
			bundle.putInt("position", bundles.getInt("position"));
			Intent intent = new Intent();
			intent.putExtras(bundle);
			if(!ischanged){
				setResult(2, intent);
				finish();
			}
			else{
				//intent.setClass(BaiduMap.this,Release.class);
				//startActivity(intent);
				//overridePendingTransition(R.anim.keep, R.anim.in_from_left);
				setResult(1, intent);
				finish();
			}
		}
    	
    };
	@Override  
	protected void onDestroy() {  
	    super.onDestroy();  
	    //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理  
	    mMapView.onDestroy();  
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
		mMapView.onPause();  
	}  
}
