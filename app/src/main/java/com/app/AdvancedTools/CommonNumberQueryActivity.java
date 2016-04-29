package com.app.AdvancedTools;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.example.administrator.mymobile.R;

/*
    常用号码查询页面
    功能实现：
        1.页面布局使用ExpandableListView；
        2.使用BaseAdapter来配置布局页面
        3.对一个数据库的9个表进行查询显示操作，使得到的结束显示到界面。
 */
public class CommonNumberQueryActivity extends Activity {

    //定义一个可扩展的ListView
     private ExpandableListView elv;

    //数据库在本地的位置
    private static String path="/data/data/com.example.administrator.mymobile/files/commonnum.db";
    //定义一个数据库
    private SQLiteDatabase db;

    /*
        此方法初始化页面布局中用到的控件
     */
    private void assignViews() {
        elv = (ExpandableListView) findViewById(R.id.elv);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //加载布局文件
        setContentView(R.layout.activity_common_number_query);
        assignViews();
        //当页面打开时，数据库根据路径以只读的形式打开数据库，第二个参数为游标工厂，用于查询时返回Cursor的子类对象；或者传入null使用默认的factory构造
        db=SQLiteDatabase.openDatabase(path,null,SQLiteDatabase.OPEN_READONLY);
        elv.setAdapter(new CommonNumberQAdapter());
        //孩子打电话事件
        elv.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                //得到孩子的textview
                TextView tv= (TextView) v;
                //通过TextView获得电话号码
                String number=tv.getText().toString().split("\n")[1].trim();
                Intent intent=new Intent();
                //拨号器调用
                intent.setAction(Intent.ACTION_DIAL);
                //号码拨打
                intent.setData(Uri.parse("tel:" + number));
                startActivity(intent);
                return true;
            }
        });
    }
    /*
        定义一个ExpandableListView的适配器
     */
    private class CommonNumberQAdapter extends BaseExpandableListAdapter{

        //list的组数
        @Override
        public int getGroupCount() {
            return QueryCommonNumber.getGroupCount(db);
        }

        //孩子的数量,第一个下拉中有两个孩子，第二个中有三个
        public int getChildrenCount(int groupPosition) {
            return QueryCommonNumber.getChildCount(db,groupPosition);
        }

        //返回分组对象，在此项目中无用
        @Override
        public Object getGroup(int groupPosition) {
            return null;
        }

        //返回分组中孩子的对象
        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return null;
        }

        //返回分组的位置id
        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }
        //返回孩子的id
        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        //是否允许id相同
        public boolean hasStableIds() {
            return false;
        }

        /*
            分组的自定义
         */
        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            TextView view;
            //为了减少内存消耗，先判断缓存中是否存在view，如果不存在再填充一个view。
            if(convertView!=null){
                view= (TextView) convertView;
            }else {
                view = new TextView(CommonNumberQueryActivity.this);
            }
            //设置分组字体的颜色和大小
            view.setTextColor(Color.RED);
            view.setTextSize(25);
            //空格代表距离左边的宽度
            view.setText("    "+QueryCommonNumber.getGroupName(db,groupPosition));
            return view;
        }

        /*
            孩子分组的自定义
         */
        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            TextView view;
            if(convertView!=null){
                view= (TextView) convertView;
            }else {
                view = new TextView(CommonNumberQueryActivity.this);
            }
            view.setTextColor(Color.BLUE);
            view.setTextSize(20);
            //空格代表距离左边的宽度
            view.setText("   "+QueryCommonNumber.getChildName(db,groupPosition,childPosition));
            return view;
        }

        //是否可以被点击
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //当数据库不为空时再进行关闭
        if(db!=null) {
            //当activity关闭时才关闭数据库
            db.close();
            db=null;
        }
    }
}
