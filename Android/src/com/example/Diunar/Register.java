package com.example.Diunar;

import java.io.IOException;
import java.io.StringReader;
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


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class Register extends Activity{
	private EditText account, password, epw;
	private Button register;
	private ImageButton back;
	private Handler handler;
	private String hint;
	private boolean isSuccess;
	private ProgressDialog mpd;
	private final String registerURL="http://172.18.33.95:8888/mobile/register";
	private OnClickListener registerListener = new OnClickListener(){
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(account.getText().toString().length() == 0){
				password.setText("");
				epw.setText("");
				Toast.makeText(Register.this, "用户名不能为空",  
                        Toast.LENGTH_LONG).show();  
			}
			else if(password.getText().toString().equals(epw.getText().toString())&&password.getText().toString().length()>5){
				mpd = ProgressDialog.show(Register.this, "Requesting", "Requesting...");
				Thread threadMobile = new Thread(new Runnable(){
					@Override
					public void run() {
						// TODO Auto-generated method stub
						HttpPost httppost = new HttpPost(registerURL);
						List<NameValuePair>pair = new ArrayList<NameValuePair>();
						pair.add(new BasicNameValuePair("username", account.getText().toString()));
						pair.add(new BasicNameValuePair("password", password.getText().toString()));
						try {
							httppost.setEntity(new UrlEncodedFormEntity(pair, "utf-8"));
							HttpClient defaultHttpClient = new DefaultHttpClient();
							defaultHttpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 15000);
			                // 读取超时
							defaultHttpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 15000);
							HttpResponse response = defaultHttpClient.execute(httppost);
							String str = EntityUtils.toString(response.getEntity(),"utf-8");
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
							Log.i("str", result.get("status").toString());
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
						handler.obtainMessage(1, isSuccess).sendToTarget();
					}
				});
				threadMobile.start();
				
				//Intent intent = new Intent();
				//intent.setClass(Register.this, MainActivity.class);
				//startActivity(intent);
				//Register.this.finish();
			}
			else if(password.getText().toString().length()<6){
				password.setText("");
				epw.setText("");
				Toast.makeText(Register.this, "密码长度过短",  
                        Toast.LENGTH_LONG).show();  
			}
			else {
				password.setText("");
				epw.setText("");
				Toast.makeText(Register.this, "密码不一致，请重新输入",  
                        Toast.LENGTH_LONG).show();  
			}
		}	
	};
	private OnClickListener backListener = new OnClickListener(){
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent();
			intent.putExtra("account", "");
			Register.this.setResult(1, intent);
			Register.this.finish();
		}	
	};
	@SuppressLint("HandlerLeak") @Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.account_register);  
	    
	    account = (EditText)findViewById(R.id.new_account);
        password = (EditText)findViewById(R.id.new_password);
        epw = (EditText)findViewById(R.id.ensurepw);
        register = (Button)findViewById(R.id.new_account_submit);
        back = (ImageButton)findViewById(R.id.register_back);
        
        register.setOnClickListener(registerListener);
        back.setOnClickListener(backListener);
        handler = new Handler(){
			public void handleMessage(android.os.Message msg){
				super.handleMessage(msg);
				if(msg.what == 1){
					if(isSuccess){
						Toast.makeText(Register.this, hint,  
	                        Toast.LENGTH_LONG).show(); 
						Intent intent = new Intent();
						intent.putExtra("account", account.getText().toString());
						Register.this.setResult(0, intent);
						Register.this.finish();
					}
					else{
						account.setText("");
						password.setText("");
						epw.setText("");
						Toast.makeText(Register.this, hint,  
		                        Toast.LENGTH_LONG).show(); 
					}
					mpd.dismiss();
				}
			}
        };
	}
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
			Register.this.setResult(1, intent);
			Register.this.finish();
        }  
          
        return false;  
          
    } 
}
