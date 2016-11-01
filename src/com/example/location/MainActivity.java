package com.example.location;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

public class MainActivity extends Activity {
	private TextView textView;
	MapView mMapView = null;
	// ���� BaiduMap ��ͼ����Ĳ���������ӿ�
	BaiduMap mBaiduMap;
	BitmapDescriptor mCurrentMarker;
	// ��λͼ����ʾ��ʽ
	private LocationMode mCurrentMode;
	// ��λ���(��λ����Ŀͻ��ˡ����������ڿͻ����������࣬�����ã�Ŀǰֻ֧�������߳�������)
	LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();
	public static LatLng nodeLocation;// ��γ��
	boolean isFirstLoc = true; // �Ƿ��״ζ�λ
	private String city;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// ��ʹ��SDK�����֮ǰ��ʼ��context��Ϣ������ApplicationContext
		// ע��÷���Ҫ��setContentView����֮ǰʵ��
		SDKInitializer.initialize(getApplicationContext());

		setContentView(R.layout.activity_main);

		mMapView = (MapView) findViewById(R.id.bmapView);
		
		mBaiduMap = mMapView.getMap();
		
		// ������λͼ��
		mBaiduMap.setMyLocationEnabled(true);

		mCurrentMarker = BitmapDescriptorFactory
				.fromResource(R.drawable.icon_geo);

		mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
				mCurrentMode, true, mCurrentMarker));
		// ��λ��ʼ��
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		// ���ö�λSDK�����ò��������綨λģʽ����λʱ����������ϵ���͵�
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true); // ��gps
		// ���ö�λͼ��������Ϣ��ֻ��������λͼ������ö�λͼ��������Ϣ�Ż���Ч
		// mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
		// mCurrentMode, true, null));
		option.setCoorType("bd09ll"); // ������������
		// ����ɨ��������λ�Ǻ��� ��<1000(1s)ʱ����ʱ��λ��Ч
		option.setScanSpan(1000);
		
		//���ö�λ��ɺ���Ҫ���ص�ַ
		option.setIsNeedAddress(true);
		//���ö�λ��ɺ���Ҫ�Ķ�λ����
		option.setIsNeedLocationDescribe(true);
		
		mLocClient.setLocOption(option);
		mLocClient.start();
		textView = (TextView) findViewById(R.id.city_text);

		textView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent it = new Intent(MainActivity.this, CityActivity.class);
				it.putExtra("name", city);
				startActivity(it);
			}
		});
	}

	/**
	 * ��λ ��λSDK�������� BDLocationListener��λ����ص��ӿ�
	 */
	public class MyLocationListenner implements BDLocationListener {


		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view ���ٺ��ڴ����½��յ�λ��
			if (location == null || mBaiduMap == null) {
				return;
			}
			// MyLocationData��λ���� //MyLocationData.Builder��λ���ݽ�����
			MyLocationData locData = new MyLocationData.Builder()
					// ���ö�λ���ݵľ�����Ϣ����λ����
					.accuracy(location.getRadius())
					// �˴����ÿ����߻�ȡ���ķ�����Ϣ��˳ʱ��0-360
					// direction���ö�λ���ݵķ�����Ϣ
					// latitude���ö�λ���ݵ�γ��
					// longitude���ö�λ���ݵľ���
					// build�������ɶ�λ���ݶ���
					.direction(100).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			
			//��ǰ��λ����
			city = location.getCity();
			textView.setText(city);
//			Toast.makeText(getApplicationContext(), "��ǰ��λ����"+location.getCity(), 1).show();
			mBaiduMap.setMyLocationData(locData);
			// �Ƿ��һ�ζ�λ
			if (isFirstLoc) {
				isFirstLoc = false;
				// LatLng��������������ݽṹ
				nodeLocation = new LatLng(location.getLatitude(),
						location.getLongitude());
				// MapStatus�����ͼ״̬ MapStatus.Builder��ͼ״̬������
				MapStatus.Builder builder = new MapStatus.Builder();
				// target���õ�ͼ���ĵ�
				// zoom���õ�ͼ���ż���
				builder.target(nodeLocation).zoom(18.0f);
				// animateMapStatus�Զ�����ʽ���µ�ͼ״̬��������ʱ 300 ms
				// MapStatusUpdateFactory���ɵ�ͼ״̬��Ҫ�����ı仯
				// newMapStatus���õ�ͼ��״̬
				mBaiduMap.animateMapStatus(MapStatusUpdateFactory
						.newMapStatus(builder.build()));
			}
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// ��activityִ��onDestroyʱִ��mMapView.onDestroy()��ʵ�ֵ�ͼ�������ڹ���
		mLocClient.stop();
		//��λ��ر�
		mBaiduMap.setMyLocationEnabled(false);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// ��activityִ��onResumeʱִ��mMapView. onResume ()��ʵ�ֵ�ͼ�������ڹ���
		mMapView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// ��activityִ��onPauseʱִ��mMapView. onPause ()��ʵ�ֵ�ͼ�������ڹ���
		mMapView.onPause();
	}
}