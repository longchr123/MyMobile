package com.example.administrator.mymobile.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.mymobile.R;
import com.example.administrator.mymobile.db.BlackNumber;
import com.example.administrator.mymobile.db.BlackNumberInfo;

import java.util.List;

public class CallSmsSafeActivity extends Activity {
    private BlackNumber bn;
    private List<BlackNumberInfo> infos;//所有的黑名单号码
    private ListView lv_black_number;
    private LinearLayout ll_loding;
    private int index=0;//从哪个位置开始加载20条
    private CallSafeAdapter adapter;
    private boolean isLoading=false;//防止频繁重复加载
    private int dbCount;//数据库的总条数

    private Handler handler=new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(adapter==null){
                adapter=new CallSafeAdapter();
                lv_black_number.setAdapter(adapter);
            }else{
                //刷新数据
                adapter.notifyDataSetChanged();
            }
            isLoading=false;
//            lv_black_number.setSelection(index);//定位到当前位置，但此方法用户体验不好,上面的if更好
            ll_loding.setVisibility(View.INVISIBLE);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_sms_safe);
        lv_black_number= (ListView) findViewById(R.id.lv_black_number);
        ll_loding= (LinearLayout) findViewById(R.id.ll_loding);
        fillData();
        //设置滑动到底部的监听
        lv_black_number.setOnScrollListener(new AbsListView.OnScrollListener() {
            //当状态发生变化时回调，状态：静止<-->滑动；手指滑动--->惯性滚动
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState){
                    case NumberPicker.OnScrollListener.SCROLL_STATE_IDLE://静止状态~~空闲
                        if(isLoading){
                            Toast.makeText(getApplicationContext(),"给我点时间，数据正在加载，请不要一直往上拉请求数据",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        //分批加载：只有当本批加载到最后一个时，才加载下一批
                        int lastPostion=lv_black_number.getLastVisiblePosition();//索引为19
                        int currentTotalSize=infos.size();//20
                        if(index>=dbCount){//dbCounto为一个请求数据库的返回值，请求数据库要在子线程中操作。
                            Toast.makeText(getApplicationContext(),"数据已经全部加载完毕",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(lastPostion==(currentTotalSize-1)){
                            isLoading=true;
                            Toast.makeText(getApplicationContext(),"加载更多数据",Toast.LENGTH_SHORT).show();
                            index+=20;
                            fillData();
                        }

                        break;
                    case NumberPicker.OnScrollListener.SCROLL_STATE_FLING://滚动状态
                        break;
                    case NumberPicker.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL://触摸滑动状态
                        break;
                }
            }

            //当滚动的时候执行这个方法
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    private void fillData() {
        bn=new BlackNumber(this);
        //如果数据正在加载
        ll_loding.setVisibility(View.VISIBLE);
        new Thread() {
            @Override
            public void run() {
                if(infos==null) {
                    infos = bn.queryPart(index);//相当于一个联网的操作，很耗时，只在时间超过一秒都以写入子线程
                }else{
                    //此if的用途，例如：刚打开页面，容器为空，只加载一批，当加载第二批时，第一批和第二批都应该存在，而不是用第二批的覆盖第一批的。
                    infos.addAll( bn.queryPart(index));
                }
               dbCount = bn.queryCount();
                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    private class CallSafeAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return infos.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view;
            ViewHolder holder;
            if(convertView !=null){
                view=convertView;
                System.out.println("使用历史缓存的View=="+position);
                holder= (ViewHolder) view.getTag();
            }else {
                view=View.inflate(CallSmsSafeActivity.this,R.layout.call_sms_safe_item,null);
                System.out.println("重新创建View=="+position);
                //每次查找id都比较耗时，当这个view被创建时，就把ID给查找到了，并且放在容器（类）
                 holder = new ViewHolder();
                holder.tv_number= (TextView) view.findViewById(R.id.tv_number);
                holder.tv_mode= (TextView) view.findViewById(R.id.tv_mode);
                holder.iv_delete= (ImageView) view.findViewById(R.id.iv_delete);
                //view 对象和我们的容器要进行关联，保存VIew对象的层次结果
                view.setTag(holder);
            }

            final BlackNumberInfo info=infos.get(position);
            holder.tv_number.setText(info.getNumber());
            String mode=info.getMode();
            if("0".equals(mode)){
                holder.tv_mode.setText("拦截电话");
            }else if("1".equals(mode)) {
                holder.tv_mode.setText("拦截短信");
            }else {holder.tv_mode.setText("拦截电话+短信");}
            holder.iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //删除数据
                    bn.delete(info.getNumber());
                    //当前列表数据删除
                    infos.remove(info);
                    //刷新数据
                    adapter.notifyDataSetChanged();
                }
            });
            return view;
        }
    }
    //容器
    static class ViewHolder{//加一个static，可以优化一点，
        public TextView tv_number;
        public TextView tv_mode;
        public ImageView iv_delete;
    }
    //弹出添加黑名单对话框
    private AlertDialog dialog;
    public void addBlackNumber(View view){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        dialog=builder.create();
        View contentView=View.inflate(this,R.layout.dialog_add_black_number,null);
        final EditText et_black_number= (EditText) contentView.findViewById(R.id.et_black_number);
        final RadioGroup rg_mode= (RadioGroup) contentView.findViewById(R.id.rg_mode);
        Button ok= (Button) contentView.findViewById(R.id.btn_ok);
        final Button cancel= (Button) contentView.findViewById(R.id.btn_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //得到电话号码,拦截模式
                String blacknumber=et_black_number.getText().toString().trim();
                int checkedid=rg_mode.getCheckedRadioButtonId();
                String mode="2";
                switch (checkedid){
                    case R.id.rb_number:
                        mode="0";
                        break;
                    case R.id.rb_sms:
                        mode="1";
                        break;
                    case R.id.rb_all:
                        mode="2";
                        break;
                }
                //判断是否为空
                if(TextUtils.isEmpty(blacknumber)) {
                    Toast.makeText(CallSmsSafeActivity.this, "电话号码不能为空", Toast.LENGTH_SHORT).show();
                }else {
                    //保存到数据库里
                    bn.add(blacknumber, mode);
                    //保存到当前列表的第一个位置
                    BlackNumberInfo object = new BlackNumberInfo();
                    object.setMode(mode);
                    object.setNumber(blacknumber);
                    infos.add(0,object);
                    //消除对话框,刷新UI
                    dialog.dismiss();
                    adapter.notifyDataSetChanged();
                }
            }
        });
        dialog.setView(contentView, 0, 0, 0, 0);//上下左右边框为0
        dialog.show();
    }
}
