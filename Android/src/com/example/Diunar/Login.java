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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.baidu.mapapi.SDKInitializer;

public class Login extends Activity{
	private EditText account,password;
	private Button register, login;
	private CheckBox cb;
	private SharedPreferences sp;
	private Handler handler;
	private boolean isSuccess;
	private ProgressDialog mpd;
	private ImageButton ib;
	private String userNameValue, passwordValue, hint;
	private final String loginURL="http://172.18.33.95:8888/mobile/login";
    private OnClickListener registerListener = new OnClickListener(){
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent();
			intent.setClass(Login.this, Register.class);
			startActivityForResult(intent, 0);
		}	
    };
    private OnClickListener loginListener = new OnClickListener(){
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			userNameValue = account.getText().toString();
	        passwordValue = password.getText().toString();
			if(userNameValue.length()==0){
				Toast.makeText(Login.this, "用户名不能为空",  
                        Toast.LENGTH_LONG).show(); 
			}
			else if( passwordValue.length()==0){
				Toast.makeText(Login.this, "密码不能为空",  
                        Toast.LENGTH_LONG).show(); 
			}
			else {
				mpd = ProgressDialog.show(Login.this, "Requesting", "Requesting...");
				Thread threadMobile = new Thread(new Runnable(){
					@Override
					public void run() {
						// TODO Auto-generated method stub
						HttpPost httppost = new HttpPost(loginURL);
						List<NameValuePair>pair = new ArrayList<NameValuePair>();
						pair.add(new BasicNameValuePair("username", account.getText().toString()));
						pair.add(new BasicNameValuePair("password", password.getText().toString()));
						try {
							httppost.setEntity(new UrlEncodedFormEntity(pair, "utf-8"));
							MyHttpClient.myHttpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 15000);
			                // 读取超时
							MyHttpClient.myHttpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 15000);
							HttpResponse response = MyHttpClient.myHttpClient.execute(httppost);
							String str = EntityUtils.toString(response.getEntity(), "utf-8");
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
			}
		}	
    };
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
            Intent intent) {
        // 当requestCode和resultCode都为0时（处理特定的结果）。
        if (requestCode == 0 && resultCode == 0) {
        	String s = intent.getExtras().getString("account");
    		account.setText(s);
    		password.setText("");
        }
    }
    @SuppressLint("HandlerLeak") @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
        SDKInitializer.initialize(getApplicationContext());  
        setContentView(R.layout.login);
        
        account = (EditText)findViewById(R.id.account);
        password = (EditText)findViewById(R.id.password);
        register = (Button)findViewById(R.id.register);
        login = (Button)findViewById(R.id.login);
        cb = (CheckBox)findViewById(R.id.checkBox1);
        ib = (ImageButton)findViewById(R.id.imageButton111);
        sp = this.getSharedPreferences("user_info", Context.MODE_PRIVATE);
        if(sp.getBoolean("ISCHECK",true)){
			cb.setChecked(true);
			account.setText(sp.getString("USER_NAME",""));
			password.setText(sp.getString("PASSWORD",""));
		}
        cb.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if(cb.isChecked()){
					sp.edit().putBoolean("ISCHECK", true).commit();
					System.out.println("checkbox is changing to true");
				} else {
					sp.edit().putBoolean("ISCHECK", false).commit();
					System.out.println("checkbox is changing to false");
				}
			}	
		});
        ib.setOnClickListener(ibListener);
        register.setOnClickListener(registerListener);
        login.setOnClickListener(loginListener);
        handler = new Handler(){
			public void handleMessage(android.os.Message msg){
				super.handleMessage(msg);
				if(msg.what == 1){
					if(isSuccess){
						Toast.makeText(Login.this, hint,  
		                        Toast.LENGTH_LONG).show();
						if(cb.isChecked()){
							Editor editor = sp.edit();
							editor.putString("USER_NAME", userNameValue);
							editor.putString("PASSWORD", passwordValue);
							editor.commit();
						}
						mpd.dismiss();
						Bundle bundle = new Bundle();
						Intent intent = new Intent();
						bundle.putBoolean("Success", true);
						intent.putExtras(bundle);
						Login.this.setResult(0, intent);
						Login.this.finish();	
					}
					else {
						Toast.makeText(Login.this, hint,  
		                        Toast.LENGTH_LONG).show(); 
						mpd.dismiss();
					}
				}
			}
		};
    }
    private OnClickListener ibListener = new OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Bundle bundle = new Bundle();
			Intent intent = new Intent();
			bundle.putBoolean("Success", false);
			intent.putExtras(bundle);
			Login.this.setResult(0, intent);
			Login.this.finish();
		}
    	
    };
    @Override  
    public boolean onKeyDown(int keyCode, KeyEvent event)  
    {  
        if (keyCode == KeyEvent.KEYCODE_BACK )  
        {  
        	Bundle bundle = new Bundle();
			Intent intent = new Intent();
			bundle.putBoolean("Success", false);
			intent.putExtras(bundle);
			Login.this.setResult(0, intent);
			Login.this.finish();
        }  
          
        return false;  
          
    } 
}
