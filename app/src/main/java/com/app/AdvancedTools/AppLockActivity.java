package com.app.AdvancedTools;
//导入架包
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.example.administrator.mymobile.R;
import java.util.ArrayList;
import java.util.List;
import static android.graphics.Color.BLUE;
/*
    程序锁页面
 */
public class AppLockActivity extends Activity implements View.OnClickListener {

    private TextView tvUnlock;
    private TextView tvLocked;
    private LinearLayout llUnlock;
    private TextView tvUnlockInfo;
    private ListView lvUnlock;
    private TextView tvLockedInfo;
    private ListView lvLocked;
    private List<AppInfo> appInfos;//所有应用列表
    private List<AppInfo> unLockAppInfos;//所有未加锁应用
    private List<AppInfo> lockedAppInfos;
    private AppLockDao dao;
    private AppLockAdapter unLockAdapter;
    private AppLockAdapter lockedAdapter;

    private void assignViews() {
        tvUnlock = (TextView) findViewById(R.id.tv_unlock);
        tvLocked = (TextView) findViewById(R.id.tv_locked);
        llUnlock = (LinearLayout) findViewById(R.id.ll_unlock);
        tvUnlockInfo = (TextView) findViewById(R.id.tv_unlock_info);
        lvUnlock = (ListView) findViewById(R.id.lv_unlock);
        tvLockedInfo = (TextView) findViewById(R.id.tv_locked_info);
        lvLocked = (ListView) findViewById(R.id.lv_locked);
        appInfos = new ArrayList<AppInfo>();
        unLockAppInfos = new ArrayList<AppInfo>();
        lockedAppInfos = new ArrayList<AppInfo>();
        dao = new AppLockDao(this);
        unLockAdapter = new AppLockAdapter(true);
        lockedAdapter = new AppLockAdapter(false);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_lock);
        assignViews();
        //设置两个按钮的点击事件
        tvUnlock.setOnClickListener(this);
        tvLocked.setOnClickListener(this);
        //得到所有包的所需信息
        appInfos = AppInfoProvider.getAllAppInfos(this);
        //对集合中的元素进行遍历
        for (AppInfo appInfo : appInfos) {
            //利用每次遍历应用的包名信息与数据库中的内容进行判断
            if (dao.query(appInfo.getPackName())) {
                //已加锁
                lockedAppInfos.add(appInfo);
            } else {
                //未加锁
                unLockAppInfos.add(appInfo);
            }
        }
        //设置适配
        lvUnlock.setAdapter(unLockAdapter);
        lvLocked.setAdapter(lockedAdapter);
    }
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //刷新是不能在子线程中进行的，只能在此进行刷新页面
            unLockAdapter.notifyDataSetChanged();
            lockedAdapter.notifyDataSetChanged();
        }
    };

    private class AppLockAdapter extends BaseAdapter {
        //为true表示未加锁
        private boolean isFlag = true;
        //将局部变量赋值给成员变量
        public AppLockAdapter(boolean isFlag) {
            this.isFlag = isFlag;
        }
        @Override
        public int getCount() {
            if (isFlag) {
                tvUnlockInfo.setText("未加锁应用" + unLockAppInfos.size());
                //返回未加锁应用的数量
                return unLockAppInfos.size();
            } else {
                //返回已加锁应用的数量
                tvLockedInfo.setText("已加锁应用" + lockedAppInfos.size());
                return lockedAppInfos.size();
            }
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
        public View getView(int position, View convertView, ViewGroup parent) {
            final View view;
            ViewHolder holder;
            if (convertView != null) {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            } else {
                view = View.inflate(AppLockActivity.this, R.layout.applock_item, null);
                holder = new ViewHolder();
                holder.ivIcon = (ImageView) view.findViewById(R.id.iv_icon);
                holder.tvName = (TextView) view.findViewById(R.id.tv_name);
                holder.ivStatus = (ImageView) view.findViewById(R.id.iv_status);
                //将holder与view进行绑定
                view.setTag(holder);
            }
            final AppInfo appInfo;
            if (isFlag) {
                //当应用成为未加锁时，改变图标
                holder.ivStatus.setImageResource(R.drawable.btn_zoom_up_disabled);
                appInfo = unLockAppInfos.get(position);
            } else {
                //当应用成为已加锁时，改变图标
                appInfo = lockedAppInfos.get(position);
                holder.ivStatus.setImageResource(R.drawable.btn_zoom_up_disabled_focused);
            }
            holder.ivIcon.setImageDrawable(appInfo.getIcon());
            holder.tvName.setText(appInfo.getName());
            //图标的点击事件
            holder.ivStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isFlag) {
                        //把页面显示的每一条view增加动漫，相对自身移动
                        TranslateAnimation ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
                                Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0f);
                        //完成移动所需要的时间
                        ta.setDuration(500);
                        //启动动画
                        view.startAnimation(ta);
                        //添加子线程的原因：我要删除这条记录，等这条记录先播放完动漫之后再移动，所以需要等等，如果不等将会是先删除再播放下一个view的动漫
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                try {
                                    //休眠0.5s
                                    sleep(500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                //1.当点击图片时，添加到已加数据库
                                dao.add(appInfo.getPackName());
                                //2.添加已加锁当前列表
                                lockedAppInfos.add(appInfo);
                                //3.未加锁列表要把这个应用的信息移除
                                unLockAppInfos.remove(appInfo);
                                //发送消息
                                handler.sendEmptyMessage(0);
                            }
                        }.start();
                    } else {
                        TranslateAnimation ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
                                Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0f);
                        ta.setDuration(500);
                        view.startAnimation(ta);
                        //第二种handler处理方法，如果hand了让运行在主线程中，run就运行在主线程中（附属线程）b
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //当在已加锁页面点击item时
                                dao.delete(appInfo.getPackName());
                                unLockAppInfos.add(appInfo);
                                lockedAppInfos.remove(appInfo);
                                //在此处刷新是可以的
                                unLockAdapter.notifyDataSetChanged();
                                lockedAdapter.notifyDataSetChanged();
                            }
                        }, 500);
                    }
                }
            });
            return view;
        }
    }
    /*
        定义一个容器，存放布局文件中的控件
     */
    static class ViewHolder {
        ImageView ivIcon;
        TextView tvName;
        ImageView ivStatus;
    }
/*
    未加锁与已加锁的点击事件
 */
    public void onClick(View view) {
        switch (view.getId()) {
            //点击了已加锁时，未加锁界面消失，出现已加锁页面
            case R.id.tv_locked:
                llUnlock.setVisibility(View.GONE);
                tvLocked.setBackgroundColor(BLUE);//点击的颜色
                tvUnlock.setBackgroundColor(Color.YELLOW);
                break;
            //点击了已加锁时，已加锁界面消失，出现未加锁页面
            case R.id.tv_unlock:
                llUnlock.setVisibility(View.VISIBLE);
                tvLocked.setBackgroundColor(Color.YELLOW);
                tvUnlock.setBackgroundColor(Color.BLUE);//点击的颜色
                break;
        }
    }
}
