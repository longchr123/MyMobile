package com.example.administrator.mymobile.activity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.administrator.mymobile.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SelectContactActivity extends AppCompatActivity {

    private List<Map<String, String>> data;
    private ListView lv_select_contact;
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            lv_select_contact.setAdapter(new SimpleAdapter(SelectContactActivity.this, data, R.layout.select_contact_item,
                    new String[]{"name", "number"}, new int[]{R.id.tv_name, R.id.tv_number}));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_contact);
        lv_select_contact = (ListView) findViewById(R.id.lv_select_contact);
        //子线程加载数据
        new Thread() {
            @Override
            public void run() {
                super.run();
                data = getAllContacts();
                handler.sendEmptyMessage(0);
            }
        }.start();

        lv_select_contact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String number = data.get(position).get("number");
                Intent intent = new Intent();
                intent.putExtra("number", number);
                //1.回传数据
                setResult(1, intent);
                finish();
            }
        });
    }

    //得到手机里所有的联系人
    private List<Map<String, String>> getAllContacts() {
        //List为一个接口，所以实例化时不能直接new，而是new一个他的子类
        List<Map<String, String>> maps = new ArrayList<Map<String, String>>();
        //内容提供者
        ContentResolver resovler = getContentResolver();
        Uri raw_contacts_uri = Uri.parse("content://com.android.contacts/raw_contacts");
        Uri data_uri = Uri.parse("content://com.android.contacts/data");
        Cursor cursor = resovler.query(raw_contacts_uri, new String[]{"contact_id"}, null, null, null);
        while (cursor.moveToNext()) {
            String contact_id = cursor.getString(0);
            if (contact_id != null) {
                //如果列表中不为空，即有联系人
                Map<String, String> map = new HashMap<>();
                //读取data数据
                Cursor datacursor = resovler.query(data_uri, new String[]{"data1", "mimetype"},
                        "raw_contact_id=?", new String[]{contact_id}, null);
                while (datacursor.moveToNext()) {
                    String data1 = datacursor.getString(0);
                    String mimetype = datacursor.getString(1);
                    System.out.println("--------" + data1 + "-------------------------" + mimetype + "-------------------------");
                    if ("vnd.android.cursor.item/phone_v2".equals(mimetype)) {
                        //电话号码
                        map.put("number", data1);
                    } else if ("vnd.android.cursor.item/name".equals(mimetype)) {
                        //姓名
                        map.put("name", data1);
                    }
                }
                datacursor.close();
                if (map.get("name") != null && map.get("number") != null) {//这行为了防止手机上删除了联系人而数据中还没删除而选择联系人中出现的空白
                    maps.add(map);
                }
            }
        }
        return maps;
    }
    //得到图片，后来加的，和前面没有联系。
    private static Bitmap getContactPhoto(Context c, String personId, int defaultIco) {
        byte[] data = new byte[0];
        Uri u = Uri.parse("content://com.android.contacts/data");
        String where = "raw_contact_id = " + personId + " AND mimetype ='vnd.android.cursor.item/photo'";
        Cursor cursor = c.getContentResolver() .query(u, null, where, null, null);
        if (cursor.moveToFirst())
        {
            data = cursor.getBlob(cursor.getColumnIndex("data15"));
        }
        cursor.close();
        if (data == null || data.length == 0)
        {
            return BitmapFactory.decodeResource(c.getResources(), defaultIco);
        } else
            return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

}
