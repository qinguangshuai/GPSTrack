package com.xrd.sunmoon;

public class NativeInterface {
    public static int READ_RS485 = 0;
    public static int WRITE_RS485 = 1;
    static {
        System.loadLibrary("native-sunmoon-lib");
    }

    public native static int getSmoke_det();//烟感
    public native static int getWater_det();//水清
    public native static int openAirCondition();
    public native static int closeAirCondition();
    public native static void setRS485ttyS2(int i);
    public native static void setRS485ttyS4(int i);
    public native static void openElectricFan();
    public native static void closeElectricFan();

    public native static void openFrontDoor();
    public native static void closeFrontDoor();
    public native static void openBehindDoor();
    public native static void closeBehindDoor();

    public native static int getFrontDoorStatus();
    public native static int getBehindDoorStatus();




}
