package com.example.administrator.mymobile.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.mymobile.R;
import com.example.administrator.mymobile.TaskInfo;
import com.example.administrator.mymobile.engine.TaskInfoProvider;
import com.example.administrator.mymobile.utils.SystemInfoUtils;

import java.util.ArrayList;
import java.util.List;


public class TaskManagerActivity extends Activity {

    private TextView tvRunProcess;
    private TextView tvAvailRom;
    private ListView lvTaskmanager;
    private LinearLayout llLoading;
    private TextView tvStatus;
    private ActivityManager am;
    private int runningProcessCount;//得到当前手机运行进程数量
    private long availRam;//可用内存
    private long totalRam;
    private List<TaskInfo> taskInfos;//所有在运行的进程列表信息
    private List<TaskInfo> systemTaskInfos;//所有在运行的进程列表信息
    private List<TaskInfo> userTaskInfos;//所有在运行的进程列表信息
    private TaskInfoAdapter adapter;
    private SharedPreferences sp;

    private Handler handler=new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            adapter=new TaskInfoAdapter();
            lvTaskmanager.setAdapter(adapter);
            llLoading.setVisibility(View.INVISIBLE);
        }
    };

    private void assignViews() {
        tvRunProcess = (TextView) findViewById(R.id.tv_run_process);
        tvAvailRom = (TextView) findViewById(R.id.tv_avail_rom);
        lvTaskmanager = (ListView) findViewById(R.id.lv_taskmanager);
        llLoading = (LinearLayout) findViewById(R.id.ll_loading);
        tvStatus = (TextView) findViewById(R.id.tv_status);
        am= (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        runningProcessCount= SystemInfoUtils.getRunningProcessCount(this);
        availRam=  SystemInfoUtils.getAvailRam(this);
        totalRam=  SystemInfoUtils.getTotalRam(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_manager);
        assignViews();
        tvRunProcess.setText("正在运行的进程有" + runningProcessCount + "个");
        tvAvailRom.setText("剩余/总内存:" + Formatter.formatFileSize(this, availRam) +
                "/" + Formatter.formatFileSize(this, totalRam));
        fillDate();
        lvTaskmanager.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object obj=lvTaskmanager.getItemAtPosition(position);
                if(obj !=null){
                    TaskInfo taskInfo= (TaskInfo) obj;
                    if(getPackageName().equals(taskInfo.getPackageName())){//如果进程为自己就不处理
                        return;
                    }
                    CheckBox cb_status= (CheckBox) view.findViewById(R.id.cb_status);
                    if(taskInfo.isChecked()){
                        taskInfo.setIsChecked(false);
                        cb_status.setChecked(false);
                    }else {
                        taskInfo.setIsChecked(true);
                        cb_status.setChecked(true);
                    }
                }

            }
        });
    }
    private class TaskInfoAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            sp=getSharedPreferences("config",MODE_PRIVATE);
            boolean showsystem=sp.getBoolean("showsystem",true);
            if(showsystem){
                return systemTaskInfos.size()+userTaskInfos.size()+2;
            }
            return userTaskInfos.size()+1;
        }

        @Override
        public Object getItem(int position) {
            TaskInfo taskInfo;
            if(position==0){
                return null;
            }else if(position==(userTaskInfos.size()+1)) {
                return null;
            }else if(position<=userTaskInfos.size()){
                taskInfo=userTaskInfos.get(position-1);
            }else {
                taskInfo=systemTaskInfos.get(position-userTaskInfos.size()-2);
            }
            return taskInfo;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TaskInfo taskInfo;
            if(position==0){
                TextView tv=new TextView(TaskManagerActivity.this);
                tv.setBackgroundColor(Color.GRAY);
                tv.setText("用户程序（" + userTaskInfos.size() + ")");
                tv.setTextColor(Color.WHITE);
                return tv;
            }else if(position==(userTaskInfos.size()+1)) {
                TextView tv = new TextView(TaskManagerActivity.this);
                tv.setBackgroundColor(Color.GRAY);
                tv.setText("系统程序（" + systemTaskInfos.size() + ")");
                tv.setTextColor(Color.WHITE);
                return tv;
            }else if(position<=userTaskInfos.size()){
                taskInfo=userTaskInfos.get(position-1);
            }else {
                taskInfo=systemTaskInfos.get(position-userTaskInfos.size()-2);
            }
            View view;
            TaskManagerActivity.ViewHolder holder;
            if(convertView !=null &&convertView instanceof RelativeLayout){
                view=convertView;
                holder= (TaskManagerActivity.ViewHolder) view.getTag();
            }else{
                view= View.inflate(TaskManagerActivity.this,R.layout.taskmanager_item,null);
                holder=new TaskManagerActivity.ViewHolder();
                holder.tv_name= (TextView) view.findViewById(R.id.tv_name);
                holder.tv_meminfosize= (TextView) view.findViewById(R.id.tv_meminfosixze);
                holder.iv_icon= (ImageView) view.findViewById(R.id.iv_icon);
                holder.cb_status= (CheckBox) view.findViewById(R.id.cb_status);
                view.setTag(holder);
            }
            //根据位置得到进程信息
            taskInfo=taskInfos.get(position);
            holder.tv_name.setText(taskInfo.getName());
            holder.tv_meminfosize.setText(Formatter.formatFileSize(TaskManagerActivity.this, taskInfo.getMemInfoSize()));
            holder.iv_icon.setImageDrawable(taskInfo.getIcon());
            if(taskInfo.isChecked()){
                holder.cb_status.setChecked(true);
            }else {
                holder.cb_status.setChecked(false);
            }
            if(getPackageName().equals(taskInfo.getPackageName())){
                holder.cb_status.setVisibility(View.INVISIBLE);
            }
            return view;
        }
    }

    static class ViewHolder{
        TextView tv_name;
        TextView tv_meminfosize;
        ImageView iv_icon;
        CheckBox cb_status;
    }
    private void fillDate() {
        llLoading.setVisibility(View.VISIBLE);
        new Thread(){
            @Override
            public void run() {
                super.run();
                taskInfos= TaskInfoProvider.getAllTaskInfos(TaskManagerActivity.this);
                systemTaskInfos=new ArrayList<TaskInfo>();
                userTaskInfos=new ArrayList<TaskInfo>();
                for(TaskInfo taskInfo: taskInfos){
                    if(taskInfo.isUser()){
                        userTaskInfos.add(taskInfo);
                    }else{systemTaskInfos.add(taskInfo);}
                }
                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    public void selectAll(View view){
        for(TaskInfo taskInfo:userTaskInfos){
            taskInfo.setIsChecked(true);
            if(getPackageName().equals(taskInfo.getPackageName())){//如果进程为自己就不处理
                continue;//跳出本次循环,下面继续执行
            }
        }
        for(TaskInfo taskInfo:systemTaskInfos){
            taskInfo.setIsChecked(true);
        }
        //刷新页面
        adapter.notifyDataSetChanged();//先调用getCount再调用getView方法
    }
    public void unSelect(View view){//反选
        for(TaskInfo taskInfo:userTaskInfos){
            taskInfo.setIsChecked(!taskInfo.isChecked());
            if(getPackageName().equals(taskInfo.getPackageName())){//如果进程为自己就不处理
                continue;//跳出本次循环,下面继续执行
            }
        }
        for(TaskInfo taskInfo:systemTaskInfos){
            taskInfo.setIsChecked(!taskInfo.isChecked());
        }
        //刷新页面
        adapter.notifyDataSetChanged();//先调用getCount再调用getView方法
    }
    public void killAll(View view){
        int killedCount=0;
        long addram=0;//结束进程后增加的空间
        List<TaskInfo>killedTaskInfos=new ArrayList<TaskInfo>();//将勾选的要清理的放入一个集合再清理
        for(TaskInfo taskInfo:userTaskInfos){
            if(taskInfo.isChecked()){
//                android.os.Process.killProcess(android.os.Process.myPid());//杀死进程-自杀型，但是会退出程序，带有不安全隐患
                am.killBackgroundProcesses(taskInfo.getPackageName());
//                userTaskInfos.remove(taskInfo);
                killedTaskInfos.add(taskInfo);
                killedCount++;
                addram +=taskInfo.getMemInfoSize();
            }
        }
        for(TaskInfo taskInfo:systemTaskInfos){
            if(taskInfo.isChecked()){
                am.killBackgroundProcesses(taskInfo.getPackageName());
//                systemTaskInfos.remove(taskInfo);//有些系统进程杀不死，当用户钩选了之后清理，就把这些给隐藏，已经隐藏了就可以刷新页面了
                killedTaskInfos.add(taskInfo);
                killedCount++;
                addram +=taskInfo.getMemInfoSize();
            }else {
                taskInfo.setIsChecked(false);
            }
        }
        for(TaskInfo taskInfo :killedTaskInfos){
            if(taskInfo.isUser()){
                userTaskInfos.remove(taskInfo);
            }else {
                systemTaskInfos.remove(taskInfo);
            }
        }
        runningProcessCount-=killedCount;
        availRam+=addram;
        tvRunProcess.setText("正在运行的进程有" + runningProcessCount + "个");
        tvAvailRom.setText("剩余/总内存:" + Formatter.formatFileSize(TaskManagerActivity.this, availRam) +
                "/" + Formatter.formatFileSize(this, totalRam));
        Toast.makeText(this, "杀死了:"+killedCount+"个进程，释放了："+Formatter.formatFileSize(TaskManagerActivity.this, addram), Toast.LENGTH_SHORT).show();
        //刷新页面
        adapter.notifyDataSetChanged();//在此处不能用此方法，因为systemTaskInfos和systemTaskInfos的数量不会改变，所以界面没有反应
//        fillDate();//所以得重新加载数据
    }

    public void enterSetting(View view){
        Intent intent=new Intent(this,TaskManagerSettingActivity.class);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //不需要重新加载，但刷新
        adapter.notifyDataSetChanged();
    }
}
