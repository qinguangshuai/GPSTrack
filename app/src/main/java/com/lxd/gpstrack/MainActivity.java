package com.lxd.gpstrack;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.lxd.gpstrack.Draw.DrawMap;
import com.lxd.gpstrack.Entiy.GouPlan;

import com.lxd.gpstrack.util.CrcCheckUtil;
import com.lxd.gpstrack.util.HexUtil;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends SerialPortActivity implements View.OnClickListener {

    private ListView gouPlanListView;
    private TextView planTime,diaoHao,danhao,mattersNeedingAttention,empty,guacheshu,shuaicheshu;

    private MyAdapter myAdapter = null;
    private List<GouPlan> mGouPlan = null;
    private Context mContext = null;

    private Timer canvasTimer = null;
    private TimerTask canvasTimerTask = null;

    private Timer gpsTrainTimer = null;
    private TimerTask gpsTrainTimerTask = null;


    private DrawMap map;


    /**
     * 该类用来处理handler
     */
    static class MyHandler extends Handler{


        private WeakReference<MainActivity> activity;
        int num = 1;
        int pianyi=5;
        boolean open = true;
        public MyHandler(WeakReference<MainActivity> activity){
            this.activity = activity;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0x1233://移动画布
                   /* if (open == false){
                        if(num == 20){
                            activity.get().stopTimer();
                            open = true;
                        }
                        if (activity.get().map.getGpsPointTwo().getX() >300){
                            pianyi = pianyi+5; num++;}
                        //scroll方式     只能滑动内容，不能把背景跟着一起滑动，不影响布局参数
                        //translate方式  真正的平移滑动，可以移动整个视图， 不影响布局参数
                        //activity.get().map.setTranslationX(pianyi);
                        activity.get().map.setScrollX(pianyi);
                        //activity.get().map.invalidate();
                        //pianyi = num*(-5);
                        //activity.get().map.setTranslationY(pianyi);

                    }*/
                   /*if (activity.get().map.getGpsPointTwo().getX()<300){
                       activity.get().map.invalidate();
                   }else if (activity.get().map.getGpsPointTwo().getX()>300&&activity.get().map.getGpsPointTwo().getX()<700){
                       pianyi = pianyi+5;
                       activity.get().map.setScrollX(pianyi);
                       //activity.get().map.setScrollY(pianyi+10);
                   }else if (activity.get().map.getGpsPointTwo().getX()>700&&activity.get().map.getGpsPointTwo().getX()<1048){
                       activity.get().map.invalidate();
                   }else if(activity.get().map.getGpsPointTwo().getX()>1048){
                       activity.get().stopTimer();
                   }*/
                   if (activity.get().map.getGpsPointTwo().getX()>300&&activity.get().map.getGpsPointTwo().getX()<700){
                       activity.get().map.setScrollX(pianyi);
                   }else {
                       activity.get().gpscar = true;
                       activity.get().gpsmap = false;
                   }

                   if (!activity.get().gpscar){
                       if (activity.get().map.getGpsPointTwo().getX()<700){
                           activity.get().gpscar = true;
                       }
                       pianyi = pianyi+5;
                       activity.get().map.setScrollX(pianyi);
                   }
                    break;
                case 0x1234:
                    //该消息用来刷新画布，呈现车辆移动的效果
                    /*if (activity.get().gpscar){
                        activity.get().map.invalidate();
                        activity.get().stopGpsTrainTimer();
                        Log.i("TAG",String.valueOf(activity.get().map.getGpsPointTwo().getX()));
                    }*/

                    if (activity.get().gpscar){
                        if (activity.get().map.getGpsPointTwo().getX()>300&&activity.get().map.getGpsPointTwo().getX()<700){
                            activity.get().gpscar = false;
                        }
                        if(activity.get().map.getGpsPointTwo().getX()>1000){
                            activity.get().stopTimer();
                        }
                        activity.get().map.invalidate();
                    }
                    break;
            }
        }
    }

    MyHandler myHandler = new MyHandler(new WeakReference<MainActivity>(this));

    private boolean gpscar = true;
    private boolean gpsmap = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = MainActivity.this;
        //初始化控件
        initViews();
        //定时器，用来移动控件
        startTimer();
        //startGpsTimer();

        //初始化listview数据
        initListViewData();

        //隐藏状态栏和导航栏
        setSystemUIVisible(false);

        //getNavigationBarHeight();


    }

    /****************************************************************
     *                       调车单相关开始
     ***************************************************************/

    boolean isEnd232=true;
    boolean isStart232=false;
    boolean flag=false;
    byte formData[]=new byte[1024];
    int len485=0;
    int len232=0;//接收到的数据长度
    int len = 0;//数据总长度

    /**
     * 接收串口数据
     * @param buffer 接收到的字节数组
     * @param size 数据大小
     * @param type 自定义的类型，区分232串口和485串口
     */
    @Override
    protected void onDataReceived(final byte[] buffer,final int size,final int type) {
        runOnUiThread(new Runnable() {
            public void run() {
                Log.i("testData","ddddddddd");
                if(type==485){
                    Log.i("testData","485555数据: "+ HexUtil.encodeHexStr(buffer,false,size));
//                    Log.i("这是485收到的数据",MainActivity.this.toString(buffer,size,0));
                    Log.i("TAG串口打开",String.valueOf(type));
                    Log.i("TAGbuffer[0]",String.valueOf(buffer[0]));
                    Log.i("TAG_size","定位协议的长度"+String.valueOf(size));
                    //帧头A8
                    if (buffer[0]==-88){
                        Log.i("TAG测试toInt",String.valueOf(toInt(buffer[4])));
                        //对数据进行校验
                        if(checkData(buffer,size)){

                            //将接收到的坐标封装成一个点对象，存到list列表中
                            Log.i("TAGx坐标：","485接收X坐标"+String.valueOf(toInt2(new byte[]{buffer[2],buffer[3]},2)));
                            Log.i("TAGy坐标：","485接收Y坐标"+String.valueOf(toInt2(new byte[]{buffer[4],buffer[5]},2)));


                            //发送数据

                            String dat="AB   CA    DB";
                            sendHexString(dat.replaceAll("\\s*",""),"485");


                        }

                        /**
                         *   0  1  2  3  4  5  6  7  8
                         *  A8 01 12 34 56 78 00 AA 99
                         *  帧头 A8   标志号 01  x坐标 0x1234  y坐标 0x5678  z坐标 0x 00AA
                         */
                        Log.i("size",String.valueOf(size));

                        //Log.i("toInt2",String.valueOf(toInt2(new byte[]{buffer[3],buffer[4]},2)));

                    }


                }
                else if (type == 232) {
                    Log.i("testData","ssssssss");
                    Log.i("testDataSize",String.valueOf(size));
                    String hexStr = HexUtil.encodeHexStr(buffer, false, size);
                    String substring = hexStr.substring(hexStr.length() - 2, hexStr.length());
                    Log.i("hexStr",hexStr+"    hexStr");
                    Log.i("substring",hexStr+"    substring");
                    if (size==32 &&buffer[0]==-35&&buffer[1]==-103){
                        Log.i("testData","hhhhhhhhh");
                        for (int i = 0;i<size;i++){
                            formData[len232+i]=buffer[i];
                        }
                        len232 = len232+size;
                        //获取调车单数据中所描述的数据长度
                        len=toInt2(new byte[]{buffer[2],buffer[3]},2)+4;
                        Log.i("TAG_len",String.valueOf(len));
                    }else if (!isStart232){
                        for (int i = 0; i < size; i++) {
                            formData[len232 + i] = buffer[i];
                        }
                        len232 += size;
                        Log.i("TAG_len232",String.valueOf(len232));
                        //如果接收到的数据长度等于调车单数据所描述的数据长度
                        if (len232 == len){
                            Log.i("TAG_len232=len","len232=len");

                            myAdapter.clear();
                            Log.i("TAG_len232_len",String.valueOf(len232)+":"+String.valueOf(len));
                            empty.setVisibility(View.GONE);
                            //对数据进行解析
                            processData(formData,len);
                            len232 = 0;
                            len = 0;
                            /*if (preBuffer[0]==formData[len-2]&&preBuffer[1]==formData[len-1]){
                                Log.i("TAG_wocao","wocao");
                                len232=0;
                                len=0;
                            }else {
                                preBuffer[0]=formData[len-2];
                                preBuffer[1]=formData[len-1];

                            }*/
                        }


                    }
                }
            }
        });
        //NativeInterface.setRS485ttyS2(1);
    }

    /***====================================
     *          485口开始
     */
    /**
     * 校验数据
     * @param buffer 接收到的数据
     * @param size 数据长度
     */
    private boolean checkData(byte[] buffer, int size) {
        String strData = HexUtil.encodeHexStr(buffer,false,size);
        Log.i("TAGstrData收到的数据",strData);
        /**
         * 0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15
         * A 8 0 1 1 2 3 4 5 6  7  8  0  0  A  A
         *
         */
        //total里存放的是累加和
        int total = 0;
        for (int i = 0;i<strData.length()-2;i+=2){
            //strB.append("0x").append(strData.substring(i,i+2));  //0xC30x3C0x010x120x340x560x780xAA
            total = total + Integer.parseInt(strData.substring(i,i+2),16);

        }
        //noTotal为累加和取反加一
        int noTotal = ~total +1;
        Log.i("total",String.valueOf(noTotal));
        String hex = Integer.toHexString(noTotal).toUpperCase();
        Log.i("TAGhex",hex);
        String key = hex.substring(hex.length()-2);
        Log.i("TAG校验码key",key);
        Log.i("TAGhex",key);
        if (key.equals(strData.substring(strData.length()-2))){
            Log.i("jiaoyan","校验成功");
            return true;
        }else {

            Log.i("jiaoyan","校验失败");
            return false;
        }
        //Log.i("total", hex.substring(hex.length()-2));

    }

    /**
     * 将字节数转换为int类型数据
     * @param b
     * @return
     */
    private int toInt(byte b){
        if(b<0){
            return b+256;
        }else{
            return b;
        }
    }
    /************===================
     *         485口结束
     *****************************/


    /**
     * crc校验
     * @param buffer 接收到的字节数组
     * @param size  数据的长度
     * @return true or false  true表示校验成功，false表示校验失败
     */
    public  boolean crcCheck(byte[] buffer,int size){
        String sr = HexUtil.encodeHexStr(buffer,buffer.length);
        StringBuilder trueData = new StringBuilder(sr.substring(0,size*2));
        String trueDataEnd = trueData.substring(trueData.length()-4,trueData.length()).toString();
        String crc = CrcCheckUtil.CRC_XModem(CrcCheckUtil.HexString2Bytes(trueData.substring(0,trueData.length()-4)));
        if (trueDataEnd.equals(crc)){
            return true;
        }
        return false;
    }

    /**
     * 处理数据
     * @param buffer 接收到的字节数组
     * @param size 数据长度
     */
    private void processData(byte[] buffer,int size) {
        boolean crcCheck = crcCheck(buffer,size);
        if (crcCheck){
            //数据校验成功
            //调号
            int Diaohao = toInt2(new byte[]{buffer[20]},1);
            diaoHao.setText(String.valueOf(Diaohao));
            //单号
            int Danhao = toInt2(new byte[]{buffer[21],buffer[22]},2);
            danhao.setText(String.valueOf(Danhao));
            //调车长
            String diaochezhang = toString(buffer,8,27);
            Log.i("TAG_调车长",diaochezhang);

            //编制人
            String bianzhiren = toString(buffer,8,35);
            Log.i("TAG_编制人",bianzhiren);

            //内容
            //内容长度
            int neirongLength = toInt2(new byte[]{buffer[43+2]},1);
            Log.i("TAG_内容长度",String.valueOf(neirongLength));
            String neirong = toString(buffer,neirongLength-1,43+3);
            Log.i("TAG_内容",neirong);

            //车次长度
            int checiLength = toInt2(new byte[]{buffer[43+2+neirongLength]},1);
            Log.i("TAG_车次长度",String.valueOf(checiLength));
            //车次
            String checi=toString(buffer,checiLength-1,43+2+neirongLength+1);
            Log.i("TAG_车次",checi);
            //注意事项
            int zhuyisixiangLength=toInt2(new byte[]{buffer[43+2+neirongLength+checiLength+1],buffer[43+2+neirongLength+checiLength+2]},2);
            Log.i("TAG_注意事项长度",String.valueOf(zhuyisixiangLength));
            String zhuyishixiang=toString(buffer,zhuyisixiangLength-2,43+2+neirongLength+checiLength+1+2);
            mattersNeedingAttention.setText(zhuyishixiang);
            Log.i("TAG_注意事项",zhuyishixiang);

            /*String Time1 = String.valueOf(toInt2(new byte[]{buffer[23]},1));
            String second1 = ("0".equals(String.valueOf(toInt2(new byte[]{buffer[24]},1)))) ? "0" :"00";
            String Time2 = String.valueOf(toInt2(new byte[]{buffer[25]},1));
            String second2 = ("0".equals(String.valueOf(toInt2(new byte[]{buffer[26]},1)))) ? "0" :"00";*/

            //计划时间
            StringBuilder Time=new StringBuilder(HexUtil.encodeHexStr(new byte[]{buffer[23],buffer[24],buffer[25],buffer[26]},4));
            String PlanTime="00:00 至 00:00";

            if (Time.length() == 8){
                PlanTime = Time.substring(0,2)+":"+Time.substring(2,4)+" 至 "+Time.substring(4,6)+":"+Time.substring(6,8);
            }
            Log.i("TAG_计划时间",PlanTime);
            planTime.setText(PlanTime);
            int contentCheciSumLength = toInt2(new byte[]{buffer[43+0],buffer[43+1]},2);
            Log.i("TAG_内容车次总长度",String.valueOf(contentCheciSumLength));


            /*** 勾计划相关 ==========*/

            //总勾数
            int zonggoushu = toInt2(new byte[]{buffer[13]},1);
            Log.i("TAG_总勾数",String.valueOf(zonggoushu));
            if(zonggoushu > 0 ){
                int index=43+contentCheciSumLength; //100
                for (int i=0;i<zonggoushu;i++){
                    //1勾长度
                    int gouchangdu=toInt2(new byte[]{buffer[index]},1)-64;
                    Log.i("TAG_勾长度",String.valueOf(i+1)+" 勾长度 "+String.valueOf(gouchangdu));
                    Log.i("TAG_index",String.valueOf(index));
                    //int gouchangdu=toInt2(new byte[]{buffer[index]},1);
                    Log.i("TAG_1勾长度",String.valueOf(gouchangdu));
                    //股道长度
                    int gudaochangdu = toInt2(new byte[]{buffer[index+1]},1);
                    Log.i("TAG_股道长度",String.valueOf(gudaochangdu));
                    //股道内容
                    String gudaoneirong= toString(buffer,gudaochangdu-1,index+2);
                    Log.i("TAG_股道内容",gudaoneirong);
                    //摘挂车数
                    int zhaiguacheshu=toInt2(new byte[]{buffer[index+gudaochangdu+1]},1);
                    String zhaigua = zhaiguacheshu >= 128 ? "+"+String.valueOf(zhaiguacheshu-128) : "-"+String.valueOf(zhaiguacheshu);
                    Log.i("TAG_摘挂车数",zhaigua);

                    //记事内容
                    String jishineirong = toString(buffer,gouchangdu-1-gudaochangdu-1,index+1+gudaochangdu+1);
                    Log.i("TAG_记事内容",jishineirong);

                    //勾计划
                    GouPlan gouPlan = new GouPlan();
                    gouPlan.setId(i);
                    gouPlan.setXuhao(String.valueOf(i+1));//序号
                    gouPlan.setGudao(gudaoneirong);
                    if (zhaiguacheshu>=128){
                        gouPlan.setGuacheshu(String.valueOf(zhaiguacheshu-128));
                        Log.i("TAG_摘挂车数","挂车数"+String.valueOf(zhaiguacheshu-128));
                    }else {
                        gouPlan.setShuaicheshu(String.valueOf(zhaiguacheshu));
                        Log.i("TAG_摘挂车数","甩车数"+String.valueOf(zhaiguacheshu));
                    }
                    gouPlan.setJishi(jishineirong);
                    myAdapter.add(gouPlan);
                    index+=gouchangdu; // 123  147
                }
            }
            //股道
        }else {
            len=0;
            len232=0;
        }
        //发送数据
        String dat="AB   CA    DB";
        sendHexString(dat.replaceAll("\\s*",""),"232");
    }

    /**
     * 转化为int类型
     * @param bytes
     * @param size
     * @return
     */
    private int toInt2(byte[] bytes,int size){
        return Integer.parseInt(new BigInteger((HexUtil.encodeHexStr(bytes,size).replace(
                " ",""
        )),16).toString(10));
    }

    /**
     * 将字节数组转化为字符串，采用GBK编码
     * @param buffer 要转换的字节数组
     * @param len 长度
     * @param pos 起始位置
     * @return  转换后的字符串
     */
    private String toString(byte[] buffer,int len,int pos){
        byte a = 0;
        while (buffer[pos+len-1]==a) {
            len--;
        }
        byte bytes[]=new byte[len];
        for(int i=0;i<len;i++){
            bytes[i]=buffer[pos+i];
        }
        try {
            return new String(bytes,"GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    /******************************************************
                         调车单相关结束
    *******************************************************/

    /**
     * 初始化listView数据
     */
    private void initListViewData() {
        mGouPlan = new LinkedList<GouPlan>();
        /*GouPlan MyGouPlanText = new GouPlan(1, "1", "你好", "你好","你好");
        mGouPlan.add(MyGouPlanText);

        GouPlan MyGouPlanText2 = new GouPlan(1, "1", "你好", "你好","你好");
        mGouPlan.add(MyGouPlanText2);

       */
        myAdapter = new MyAdapter(mContext, (LinkedList<GouPlan>) mGouPlan);
        gouPlanListView.setAdapter(myAdapter);
    }

    /**
     * 打开定时器
     */
    private void  startTimer(){
        if (canvasTimer==null){
            canvasTimer = new Timer();
        }
        if (canvasTimerTask == null){
            canvasTimerTask = new TimerTask() {
                @Override
                public void run() {
                    myHandler.sendEmptyMessage(0x1233);
                    myHandler.sendEmptyMessage(0x1234);
                }
            };
        }
        if (canvasTimer != null && canvasTimerTask != null){
            canvasTimer.schedule(canvasTimerTask,0,1000);
        }
    }
    /**
     * 关闭定时器
     */
    private void stopTimer(){
        if (canvasTimer!=null){
            canvasTimer.cancel();
            canvasTimer=null;  //这里需要将定时器置为空，否则再次启动定时器会报错
        }
        if (canvasTimerTask!=null){
            canvasTimerTask.cancel();
            canvasTimerTask = null;
        }
    }
    /**
     * 初始化控件
     */
    private void initViews() {
        gouPlanListView = findViewById(R.id.gouPlan);
        map = findViewById(R.id.Map);
        Button previousItem = findViewById(R.id.previousItem);
        previousItem.setOnClickListener(MainActivity.this);
        Button nextItem = findViewById(R.id.nextItem);
        nextItem.setOnClickListener(MainActivity.this);
        planTime = findViewById(R.id.planTime);
        diaoHao = findViewById(R.id.diaoHao);
        danhao = findViewById(R.id.danhao);
        mattersNeedingAttention = findViewById(R.id.mattersNeedingAttention);
        empty = findViewById(R.id.empty);
        guacheshu = findViewById(R.id.guacheshu);
        shuaicheshu=findViewById(R.id.shuaicheshu);
    }
    /**
     * 按钮点击事件
     * @param v 按钮
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.previousItem:
                listScrollUp();
                break;
            case R.id.nextItem:
                listScrollDown();
                break;
        }
    }

    /**
     * 数据向上移动
     * 方法postDelayed的作用是延迟多少毫秒后开始运行，
     * 而removeCallbacks方法是删除指定的Runnable对象，使线程对象停止运行。
     */
    private void listScrollUp() {
        listScrollOff();
        myHandler.postDelayed(run_scroll_up,0);
    }
    /**
     * 停止滚动
     */
    private void listScrollOff() {
        myHandler.removeCallbacks(run_scroll_down);
    }
    //smoothScrollBy在指定时间内滑动指定的像素
    Runnable run_scroll_up = new Runnable() {
        @Override
        public void run() {
            gouPlanListView.smoothScrollBy(-30,10);
            //myHandler.postDelayed(run_scroll_up,10);
        }
    };
    Runnable run_scroll_down = new Runnable() {
        @Override
        public void run() {
            gouPlanListView.smoothScrollBy(30,10);
            //myHandler.postDelayed(run_scroll_down,10);
        }
    };

    //
    /**
     * listview数据向下移动
     */
    private void listScrollDown() {
        listScrollOff();
        myHandler.postDelayed(run_scroll_down,0);
    }

    /**
     * 隐藏状态栏和导航栏
     * @param show boolean类型，true:显示  false ：隐藏
     */
    private void setSystemUIVisible(boolean show) {
        if (show) {
            int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            uiFlags |= 0x00001000;
            getWindow().getDecorView().setSystemUiVisibility(uiFlags);
        } else {
            int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN;
            uiFlags |= 0x00001000;
            getWindow().getDecorView().setSystemUiVisibility(uiFlags);
        }
    }
    //获取虚拟导航栏高度
    private void getNavigationBarHeight()
    {
        Resources resources = MainActivity.this.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height","dimen", "android");
        int height = resources.getDimensionPixelSize(resourceId);
        Log.v("dbw", "Navi height:" + height);
    }


}
