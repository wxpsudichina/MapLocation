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
	// 定义 BaiduMap 地图对象的操作方法与接口
	BaiduMap mBaiduMap;
	BitmapDescriptor mCurrentMarker;
	// 定位图层显示方式
	private LocationMode mCurrentMode;
	// 定位相关(定位服务的客户端。宿主程序在客户端声明此类，并调用，目前只支持在主线程中启动)
	LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();
	public static LatLng nodeLocation;// 经纬度
	boolean isFirstLoc = true; // 是否首次定位
	private String city;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 在使用SDK各组件之前初始化context信息，传入ApplicationContext
		// 注意该方法要再setContentView方法之前实现
		SDKInitializer.initialize(getApplicationContext());

		setContentView(R.layout.activity_main);

		mMapView = (MapView) findViewById(R.id.bmapView);
		
		mBaiduMap = mMapView.getMap();
		
		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);

		mCurrentMarker = BitmapDescriptorFactory
				.fromResource(R.drawable.icon_geo);

		mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
				mCurrentMode, true, mCurrentMarker));
		// 定位初始化
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		// 配置定位SDK各配置参数，比如定位模式、定位时间间隔、坐标系类型等
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true); // 打开gps
		// 设置定位图层配置信息，只有先允许定位图层后设置定位图层配置信息才会生效
		// mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
		// mCurrentMode, true, null));
		option.setCoorType("bd09ll"); // 设置坐标类型
		// 设置扫描间隔，单位是毫秒 当<1000(1s)时，定时定位无效
		option.setScanSpan(1000);
		
		//设置定位完成后需要返回地址
		option.setIsNeedAddress(true);
		//设置定位完成后需要的定位描述
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
	 * 定位 定位SDK监听函数 BDLocationListener定位请求回调接口
	 */
	public class MyLocationListenner implements BDLocationListener {


		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null || mBaiduMap == null) {
				return;
			}
			// MyLocationData定位数据 //MyLocationData.Builder定位数据建造器
			MyLocationData locData = new MyLocationData.Builder()
					// 设置定位数据的精度信息，单位：米
					.accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					// direction设置定位数据的方向信息
					// latitude设置定位数据的纬度
					// longitude设置定位数据的经度
					// build构建生成定位数据对象
					.direction(100).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			
			//当前定位城市
			city = location.getCity();
			textView.setText(city);
//			Toast.makeText(getApplicationContext(), "当前定位城市"+location.getCity(), 1).show();
			mBaiduMap.setMyLocationData(locData);
			// 是否第一次定位
			if (isFirstLoc) {
				isFirstLoc = false;
				// LatLng地理坐标基本数据结构
				nodeLocation = new LatLng(location.getLatitude(),
						location.getLongitude());
				// MapStatus定义地图状态 MapStatus.Builder地图状态构造器
				MapStatus.Builder builder = new MapStatus.Builder();
				// target设置地图中心点
				// zoom设置地图缩放级别
				builder.target(nodeLocation).zoom(18.0f);
				// animateMapStatus以动画方式更新地图状态，动画耗时 300 ms
				// MapStatusUpdateFactory生成地图状态将要发生的变化
				// newMapStatus设置地图新状态
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
		// 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
		mLocClient.stop();
		//定位层关闭
		mBaiduMap.setMyLocationEnabled(false);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
		mMapView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// 在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
		mMapView.onPause();
	}
}