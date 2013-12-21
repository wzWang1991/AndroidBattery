package edu.columbia.batterybenchmark;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class AppShowActivity extends Activity {
	ListView lv;
	MyAdapter adapter;
	ArrayList<HashMap<String, Object>> items = new ArrayList<HashMap<String, Object>>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_show);

		lv = (ListView) findViewById(R.id.lv);

		PackageManager pm = getPackageManager();
		
		Intent intent = new Intent(Intent.ACTION_MAIN, null);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);

		List<ResolveInfo> packs = pm.queryIntentActivities(intent, PackageManager.GET_ACTIVITIES);
		

		// List<ApplicationInfo> packs = pm.getInstalledApplications(0);
		//List<PackageInfo> packs = pm.getInstalledPackages(0);
		int counter = 0;
		for (ResolveInfo pi : packs) {
			HashMap<String, Object> map = new HashMap<String, Object>();

		//	if ((pi.activityInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0
		//			&& (pi.activityInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 0) {
				
				map.put("icon", pi.activityInfo.loadIcon(pm));
				map.put("appName", pi.activityInfo.loadLabel(pm));
				map.put("packageName", pi.activityInfo.packageName);
				map.put("className", pi.activityInfo.name);
				items.add(map);
		//	}
			
			
		}

		adapter = new MyAdapter(this, items, R.layout.piitem, new String[] {
				"icon", "appName", "packageName" }, new int[] { R.id.icon,
				R.id.appName, R.id.packageName });
		lv.setAdapter(adapter);
		
		
		lv.setOnItemClickListener(listener);
		

	}
	private OnItemClickListener listener = new OnItemClickListener() {

	    @Override
	    public void onItemClick(AdapterView<?> parent, View view, int position,
	            long id) {
	    	HashMap<String, Object> map = items.get(position);


	        //package name
	    	String packageName = (String) map.get("packageName");
	        //class name
	    	String className = (String) map.get("className");
	    	
	        Intent i = new Intent();
	        i.putExtra("AppName", (String)map.get("appName"));
	        i.putExtra("PackageName", packageName);
	        i.putExtra("ClassName", className);

	        setResult(RESULT_OK, i);  
	        finish();  

	    }
	};


	
}

class MyAdapter extends SimpleAdapter {
	private int[] appTo;
	private String[] appFrom;
	private ViewBinder appViewBinder;
	private List<? extends Map<String, ?>> appData;
	private int appResource;
	private LayoutInflater appInflater;

	public MyAdapter(Context context, List<? extends Map<String, ?>> data,
			int resource, String[] from, int[] to) {
		super(context, data, resource, from, to);
		appData = data;
		appResource = resource;
		appFrom = from;
		appTo = to;
		appInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		return createViewFromResource(position, convertView, parent,
				appResource);

	}

	private View createViewFromResource(int position, View convertView,
			ViewGroup parent, int resource) {
		View v;
		if (convertView == null) {
			v = appInflater.inflate(resource, parent, false);
			final int[] to = appTo;
			final int count = to.length;
			final View[] holder = new View[count];

			for (int i = 0; i < count; i++) {
				holder[i] = v.findViewById(to[i]);
			}
			v.setTag(holder);
		} else {
			v = convertView;
		}
		bindView(position, v);
		return v;
	}

	private void bindView(int position, View view) {
		final Map dataSet = appData.get(position);
		if (dataSet == null) {
			return;
		}

		final ViewBinder binder = appViewBinder;
		final View[] holder = (View[]) view.getTag();
		final String[] from = appFrom;
		final int[] to = appTo;
		final int count = to.length;

		for (int i = 0; i < count; i++) {
			final View v = holder[i];
			if (v != null) {
				final Object data = dataSet.get(from[i]);
				String text = data == null ? "" : data.toString();
				if (text == null) {
					text = "";
				}

				boolean bound = false;
				if (binder != null) {
					bound = binder.setViewValue(v, data, text);
				}

				if (!bound) {

					if (v instanceof TextView) {

						setViewText((TextView) v, text);
					} else if (v instanceof ImageView) {

						setViewImage((ImageView) v, (Drawable) data);
					} else {
						throw new IllegalStateException(
								v.getClass().getName()
										+ " is not a "
										+ "view that can be bounds by this SimpleAdapter");
					}
				}
			}
		}
	}

	public void setViewImage(ImageView v, Drawable value) {
		v.setImageDrawable(value);
	}
}
