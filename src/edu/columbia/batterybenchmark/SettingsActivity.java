package edu.columbia.batterybenchmark;



import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;


public class SettingsActivity extends Activity {
	static Context context;
	protected static SettingsFragment settingsFragment;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        settingsFragment = new SettingsFragment();
        context = getApplicationContext();
        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, settingsFragment)
                .commit();
        
    }
    
    
    public static class SettingsFragment extends PreferenceFragment {
    	
    	
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);
            
            initView();
            
            //Get the manual app selection.
            Preference chooseApp = (Preference)findPreference("pref_manual_app");
            chooseApp.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                	Intent intent = new Intent(context, AppShowActivity.class);
            		startActivityForResult(intent, 1); 
					return true;
                }});
            
            
            //Enable or disable settings depending on pref_testMode.
            Preference testMode = (Preference)findPreference("pref_testMode");
            final Preference autoTestSettings = (Preference)findPreference("pref_autoTestSettings");
            final Preference manualTestSettings = (Preference)findPreference("pref_manualTestSettings");
            
            testMode.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
				
				@Override
				public boolean onPreferenceChange(Preference preference, Object newValue) {
					final String val = newValue.toString();
					if(val.equals("auto")){
						autoTestSettings.setEnabled(true);
						manualTestSettings.setEnabled(false);
					}else{
						autoTestSettings.setEnabled(false);
						manualTestSettings.setEnabled(true);
					}
					return true;
				}
			});
            
            //Enable or disable test finish condition settings depending on pref_testFinishMode.
            Preference testFinishMode = (Preference)findPreference("pref_testFinishMode");
            final Preference testTimeSettings = (Preference)findPreference("pref_testTime");
            final Preference testPercentSettings = (Preference)findPreference("pref_testPercent");
            
            testFinishMode.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
				
				@Override
				public boolean onPreferenceChange(Preference preference, Object newValue) {
					final String val = newValue.toString();
					if(val.equals("time")){
						testTimeSettings.setEnabled(true);
						testPercentSettings.setEnabled(false);
					}else{
						testTimeSettings.setEnabled(false);
						testPercentSettings.setEnabled(true);
					}
					return true;
				}
			});
            
        } 
        
        
        
    	@Override
		public void onActivityResult(int requestCode, int resultCode, Intent data) {
    		//super.onActivityResult(resultCode, resultCode, data);
    		switch (resultCode) {
    			case RESULT_OK: {
    				if (data != null) {
    					SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
    					Bundle b = data.getExtras();
    					String appName = b.getString("AppName");
    					String packageName = b.getString("PackageName");
    					String className = b.getString("ClassName");
    					
    					Preference chooseAppDisplayName = (Preference) findPreference("pref_manual_app_display_name");
    					chooseAppDisplayName.setSummary(appName);
    					Preference chooseAppDisplayPackage = (Preference) findPreference("pref_manual_app_display_package");
    					chooseAppDisplayPackage.setSummary(packageName);
    					Preference chooseAppDisplayClass = (Preference) findPreference("pref_manual_app_display_class");
    					chooseAppDisplayClass.setSummary(className);
    					
    					SharedPreferences.Editor editor = sp.edit();
    					editor.putString("pref_manual_app_display_name", appName);
    					editor.putString("pref_manual_app_display_package", packageName);
    					editor.putString("pref_manual_app_display_class", className);
    					editor.apply();
    				}

    			}
    		}
    	}
    	
    	
        //Enable and disable some settings depends on saved values.
    	//And set the manual app information.
        private void initView(){
        	SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        	
        	//Auto or manual.
            final Preference autoTestSettings = (Preference)findPreference("pref_autoTestSettings");
            final Preference manualTestSettings = (Preference)findPreference("pref_manualTestSettings");
            String testMode = sp.getString("pref_testMode", "auto");
            if(testMode.equals("auto")){
            	autoTestSettings.setEnabled(true);
            	manualTestSettings.setEnabled(false);
            }else{
            	autoTestSettings.setEnabled(false);
            	manualTestSettings.setEnabled(true);
            }
            
            //Test time or percentage.
            final Preference testTimeSettings = (Preference)findPreference("pref_testTime");
            final Preference testPercentSettings = (Preference)findPreference("pref_testPercent");
            String testFinishModeString = sp.getString("pref_testFinishMode", "time");
			if(testFinishModeString.equals("time")){
				testTimeSettings.setEnabled(true);
				testPercentSettings.setEnabled(false);
			}else{
				testTimeSettings.setEnabled(false);
				testPercentSettings.setEnabled(true);
			}
			
			final Preference appDisplayName = (Preference)findPreference("pref_manual_app_display_name");
            final Preference appDisplayPackage = (Preference)findPreference("pref_manual_app_display_package");
            final Preference appDisplayClass = (Preference)findPreference("pref_manual_app_display_class");
            
            appDisplayName.setSummary(sp.getString("pref_manual_app_display_name", ""));
            appDisplayPackage.setSummary(sp.getString("pref_manual_app_display_package", ""));
            appDisplayClass.setSummary(sp.getString("pref_manual_app_display_class", ""));
        }
    	
    }
    

    

    

    
    
}
