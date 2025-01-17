/*Copyright G�ran B�cklund gbanm06@gmail.com

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0 

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.*/

package com.varoid.exoplanethunter;

import java.util.ArrayList;




import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;
import android.view.Gravity;

import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import android.widget.Toast;
public class MainActivity extends SherlockActivity{


	VaroidJSON j;
	Planet d;
	ListView  planetlist;
	String number;
	Button loadmore;
	int start=0;
	int limit = 5;
	int loopstop = start+limit;
	boolean uri = true;
	String url;
	ProgressDialog dialog;
	ArrayAdapter<String> arrayAdapter;
	ArrayList<String> planet_array_list;
	EditText vy;
	
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        dialog = new ProgressDialog(this);
	    dialog.setCancelable(true);
	    dialog.setMessage(getResources().getString(R.string.somethingelse));
	    dialog.show();
        planet_array_list = new ArrayList<String>();
        
        
        
 	 new LongOperation().execute(""); 
      
        }
    
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       MenuInflater inflater = getSupportMenuInflater();
       inflater.inflate(R.menu.menu, menu);
       return super.onCreateOptionsMenu(menu);
    }
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		
		 
		case R.id.home:
			
			
			
			Intent intent = new Intent(MainActivity.this, com.varoid.exoplanethunter.Splash.class);
	       
	       
	        startActivity(intent);
			return true;
			
		case R.id.back:
			
			
			finish();
	        
	       
	       
			return true;
		
	}
		return false;
	}

        
        private class LongOperation extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {
            	j = new VaroidJSON();
            	if(uri){
            		Bundle bundle = getIntent().getExtras();
            		url = bundle.getString("url");
            	}
              try
              {
            	  String realUrl = url+"fields=[name]&limit="+String.valueOf(limit).toString()+"&start="+ String.valueOf(start).toString();
            	  Log.v("url",realUrl);
            	  j.readJSONFeed(realUrl);
            	
              }
              catch (Exception e)
              {
                  e.printStackTrace();
              }
              return j.get_data();
            }      

            @Override
            protected void onPostExecute(String result) {    
            	dialog.cancel();
   
            	setContentView(R.layout.activity_main);
            	vy= (EditText) findViewById(R.id.editView);
            	if(uri){
            	getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
            	}
            	planetlist = (ListView) findViewById(R.id.list);
             
             	loadmore=(Button) findViewById(R.id.button1);
             	
             	boolean hasmore = false;
            	loopstop = limit;
            
            	int i;
            
            	
            	 for(i=0; i<loopstop; i++){
            		 if(!j.planet(i, "name").equals(""))
                     {
            			 hasmore= true; 
            			 
            			 if (!planet_array_list.contains(j.planet(i, "name")))
             			{
            			 planet_array_list.add(j.planet(i, "name"));
            			 
            			 }
                     }
         			else 
         			{
         				hasmore= false;
         				
         			}
                   }
            		
            	
            	 start=start+loopstop;
            	 if(planet_array_list.isEmpty()){
            		 Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.noplanets) , Toast.LENGTH_SHORT);
                      
            		 toast.setGravity(Gravity.CENTER, 0, 0);
                          toast.show();
                    
             		}
            	 if(hasmore==false){
              		
           		   loadmore.setVisibility(View.GONE);
           		}
              	
            	
               
            		
            		arrayAdapter = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1, planet_array_list);
             planetlist.setAdapter(arrayAdapter); 
             planetlist.setOnItemClickListener(new OnItemClickListener() {

                 @Override
                 public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                	
                     
                 	Intent intent = new Intent(MainActivity.this, Planet.class);
                    
                     intent.putExtra("PLANETNAME",  planet_array_list.get(position));
                     
                     
					 startActivity(intent);
                	 
                	 }
             });
            	
             
             vy.setOnEditorActionListener(new OnEditorActionListener() {
            	  
            	   
					@Override
					public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
						if(arg1==EditorInfo.IME_ACTION_SEARCH){
							vy.clearFocus();
							InputMethodManager in = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
							in.hideSoftInputFromWindow(vy.getWindowToken(), 0);
							url = VaroidJSON.findurl+"?string="+vy.getText()+"&";
							uri=false;
							new LongOperation().execute("");
							planet_array_list.clear();
							start=0;
							getSupportActionBar().setTitle(vy.getText());
						return true;
						}
						return false;
					}
            	});
           loadmore.setOnClickListener(new OnClickListener() {
        	 	@Override
	       		public void onClick(View arg0)
        	 	{
	       			new LongOperation().execute(""); 
	       		}
       		});	
            
            }

    }
      
    


 
  
    
    
    
}

