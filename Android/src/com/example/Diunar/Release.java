package com.example.Diunar;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.MapView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class Release extends Activity {  
	private EditText title, description,  phonenumber;
	private TextView mlocation, mytitles;
	private RadioGroup catelory;
	private Button mapButton, submit;
	private ImageButton back;
	private ProgressDialog mpd;
	private Spinner spinner;
	private Handler handler;
	private boolean isSuccess, flag;
	private double x = 0, y = 0, type;
	private final String editURL="http://172.18.33.95:8888/mobile/editMessage";
	private final String releaseURL="http://172.18.33.95:8888/mobile/postMessage";
	private String location, titlec, descric, phonec, s, tag="", hint, id;
    @SuppressLint("HandlerLeak") @Override  
    protected void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState);   
        setContentView(R.layout.release); 
        Log.i("?","?");
        title = (EditText)findViewById(R.id.title);
        catelory = (RadioGroup)findViewById(R.id.radioGroup1);
        description = (EditText)findViewById(R.id.descri);
        phonenumber = (EditText)findViewById(R.id.pnumber);
        mapButton = (Button)findViewById(R.id.biaozhub);
        mlocation = (TextView)findViewById(R.id.mlocation);
        submit = (Button)findViewById(R.id.resubmits);
        back = (ImageButton)findViewById(R.id.back1);
        
        mapButton.setOnClickListener(mapButtonListener);
        submit.setOnClickListener(submitListener);
        back.setOnClickListener(backListener);       
    	mytitles = (TextView)findViewById(R.id.mytitles);
        mytitles.setText("信息发布");
        submit.setText("发布");
        ArrayList<String> data_list = new ArrayList<String>();
        data_list.add("书");
        data_list.add("手机");
        data_list.add("电脑");
        data_list.add("钱包");
        data_list.add("证件");
        data_list.add("钥匙");
        data_list.add("衣服");
        data_list.add("数码");
        data_list.add("其他");
        spinner=(Spinner)findViewById(R.id.spinner10);
        ArrayAdapter<String> accountTypesAdapter = new ArrayAdapter<String>(this, 
        		android.R.layout.simple_spinner_item, data_list); 
        accountTypesAdapter.setDropDownViewResource(R.layout.dropdown_item); 
        spinner.setAdapter(accountTypesAdapter);
        spinner.setOnItemSelectedListener(spinnerListener);
        handler = new Handler(){
			public void handleMessage(android.os.Message msg){
				super.handleMessage(msg);
				if(msg.what == 1){
					if(isSuccess){
						Toast.makeText(Release.this, hint,  
		                        Toast.LENGTH_LONG).show();
						mpd.dismiss();
						Bundle bundle = new Bundle();
						bundle.putBoolean("isSuccess", true);
						Intent intent = new Intent();
						intent.putExtras(bundle);
						//intent.setClass(BaiduMap.this,Release.class);
						//startActivity(intent);
						//overridePendingTransition(R.anim.keep, R.anim.in_from_left);
						setResult(1, intent);
						finish();
					}
					else {
						Toast.makeText(Release.this, hint,  
		                        Toast.LENGTH_LONG).show(); 
						mpd.dismiss();
					}
				}
				if(msg.what == 2){
					if(isSuccess){
						Toast.makeText(Release.this, hint,  
		                        Toast.LENGTH_LONG).show();
						mpd.dismiss();
						Bundle bundle = new Bundle();
						bundle.putBoolean("flag", true);
						bundle.putString("title", titlec);
						bundle.putString("place", location);
						bundle.putString("type", s.equals("丢东西")?"0":"1");
						bundle.putString("tag", tag);
						bundle.putString("description", descric);
						bundle.putString("contactInfo", phonec);
						bundle.putString("x", String.valueOf(x));
						bundle.putString("y", String.valueOf(y));
						bundle.putBoolean("isSuccess", false);
						Intent intent = new Intent();
						intent.putExtras(bundle);
						//intent.setClass(BaiduMap.this,Release.class);
						//startActivity(intent);
						//overridePendingTransition(R.anim.keep, R.anim.in_from_left);
						setResult(1, intent);
						finish();
					}
				}
			}
        };
        Bundle bundle = getIntent().getExtras();
        flag = bundle.getBoolean("flag");
        if(flag){
        	 if(bundle.getString("tag").equals("手机")){
        		 spinner.setSelection(2);
        	 }
        	 else if(bundle.getString("tag").equals("电脑")){
        		 spinner.setSelection(3);
        	 }
        	 else if(bundle.getString("tag").equals("钱包")){
        		 spinner.setSelection(4);
        	 }
        	 else if(bundle.getString("tag").equals("证件")){
        		 spinner.setSelection(5);
        	 }
        	 else if(bundle.getString("tag").equals("钥匙")){
        		 spinner.setSelection(6);
        	 }
        	 else if(bundle.getString("tag").equals("衣服")){
        		 spinner.setSelection(7);
        	 }
        	 else if(bundle.getString("tag").equals("数码")){
        		 spinner.setSelection(8);
        	 }
        	 else if(bundle.getString("tag").equals("其他")){
        		 spinner.setSelection(9);
        	 }
         	 mytitles.setText("修改信息");
         	 submit.setText("提交修改");
             phonenumber.setText(bundle.getString("contactInfo"));
             description.setText(bundle.getString("description"));
             mlocation.setText(bundle.getString("place"));
             id = bundle.getString("_id");
             if(bundle.getString("type").equals("捡东西")){
            	 RadioButton checked =(RadioButton)findViewById(R.id.radio11);
            	 checked.setChecked(true);
             }
             title.setText(bundle.getString("title"));
             x = Double.valueOf(bundle.getString("x"));
             y = Double.valueOf(bundle.getString("y"));
             location = bundle.getString("place");
             
        }
    }
    private OnItemSelectedListener spinnerListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			// TODO Auto-generated method stub
			tag = parent.getItemAtPosition(position).toString();
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub
			
		}
    };
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
            Intent intent) {
        // 当requestCode和resultCode都为0时（处理特定的结果）。
        if (requestCode == 0 && resultCode == 0) {
        	Bundle bundle = intent.getExtras();
    		location = bundle.getString("location");
    		mlocation.setText(location);
    		x =  bundle.getDouble("latitude");
    		y =  bundle.getDouble("longitude");
        }
    }
    private OnClickListener mapButtonListener = new OnClickListener(){
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent();
			intent.setClass(Release.this, BaiduMap.class);
			startActivityForResult(intent, 0);
			//Release.this.finish();
		}
    };
    private OnClickListener backListener = new OnClickListener(){
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putBoolean("isSuccess", false);
			intent.putExtras(bundle);
			setResult(2, intent);
			Release.this.finish();
		}
    };
    private OnClickListener submitListener = new OnClickListener(){
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			titlec = title.getText().toString();
			descric = description.getText().toString();
			phonec = phonenumber.getText().toString();
			RadioButton checked =(RadioButton)findViewById(catelory.getCheckedRadioButtonId());
			s = checked.getText().toString();
			//tag = spinner.getT
			if(titlec==""||phonec==""||s==""||x==0||tag==""){
				Toast.makeText(Release.this, "必填项不能为空",  
                        Toast.LENGTH_LONG).show(); 
			}
			else {
				mpd = ProgressDialog.show(Release.this, "Requesting",  "Requesting...");
				Thread threadMobile = new Thread(new Runnable(){
					@Override
					public void run() {
						// TODO Auto-generated method stub
						HttpPost httppost;
						List<NameValuePair>pair = new ArrayList<NameValuePair>();
						if(flag){
							httppost = new HttpPost(editURL);
							pair.add(new BasicNameValuePair("_id", id));
						}
						else {
							httppost = new HttpPost(releaseURL);
						}	
						pair.add(new BasicNameValuePair("title", titlec));
						pair.add(new BasicNameValuePair("place", location));
						pair.add(new BasicNameValuePair("type", s.equals("丢东西")?"0":"1"));
						pair.add(new BasicNameValuePair("tag", tag));
						if(descric != ""){
							pair.add(new BasicNameValuePair("description", descric));
						}
						pair.add(new BasicNameValuePair("contactInfo", phonec));
						pair.add(new BasicNameValuePair("x", String.valueOf(x)));
						pair.add(new BasicNameValuePair("y", String.valueOf(y)));
						try {
							httppost.setEntity(new UrlEncodedFormEntity(pair, "utf-8"));
							MyHttpClient.myHttpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 15000);
			                // 读取超时
							MyHttpClient.myHttpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 15000);
							HttpResponse response = MyHttpClient.myHttpClient.execute(httppost);
							String str = EntityUtils.toString(response.getEntity(), "utf-8");
							//Toast.makeText(Release.this, "shit",  
			                //        Toast.LENGTH_LONG).show(); 
							JSONObject jsonObject = new JSONObject(str);        
							Map result = new HashMap();
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
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
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
						if (flag){
							handler.obtainMessage(2, isSuccess).sendToTarget();
						}
						else {
							handler.obtainMessage(1, isSuccess).sendToTarget();
						}
					}
				});
				threadMobile.start();
			}
		}
    };
    @Override  
    public boolean onKeyDown(int keyCode, KeyEvent event)  
    {  
        if (keyCode == KeyEvent.KEYCODE_BACK )  
        {  
			Release.this.finish();
        }     
        return false;  
    }   
}
