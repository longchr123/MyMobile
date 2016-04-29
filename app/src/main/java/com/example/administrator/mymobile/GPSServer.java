package com.example.administrator.mymobile;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.TextView;

/**
 * Created by Administrator on 2015/10/5.
 */
public class GPSServer extends Service {

    private LocationManager lm;
    private MyLocationListener listener;
    private SharedPreferences sp;

    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sp=getSharedPreferences("config",MODE_PRIVATE);
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        //在服务里面监听位置的变化
        listener = new MyLocationListener();
        //设置条件
        Criteria criteria=new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);//按照精准度为最高的条件来选择定位系统
        criteria.setAltitudeRequired(true);//要求有海拔信息
        criteria.setBearingRequired(true);//要求有方位信息，
        criteria.setCostAllowed(true);//允许付费
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setPowerRequirement(Criteria.POWER_LOW);//电量要求低
        String provider=lm.getBestProvider(criteria, true);//得到最好的定位系统
        lm.requestLocationUpdates(provider, 10000, 10, listener);
    }
    public void onDestroy() {
        super.onDestroy();
        //取消注册位置监听,释放资源便于回收器回收
        lm.removeUpdates(listener);
        listener = null;
    }
    private class MyLocationListener implements LocationListener {

        //当位置改变时回调
        public void onLocationChanged(Location location) {
            String longitude="j:"+location.getLongitude()+"\n";
            String altitude="w:"+location.getAltitude()+"\n";
            String accuracy="a:"+location.getAccuracy()+"\n";
            SharedPreferences.Editor editor=sp.edit();
            editor.putString("lastlocation",longitude+altitude+accuracy).commit();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        //当某个位置提供者可用时回调
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }
}
