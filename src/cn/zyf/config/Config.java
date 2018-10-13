package cn.zyf.config;

import cn.zyf.entry.MissionThread;

public class Config {

    public static boolean stop = false;

    public static Thread t2 = new Thread(new MissionThread(2), "Mission-2");
    public static Thread t3 = new Thread(new MissionThread(3), "Mission-3");
    public static Thread t4 = new Thread(new MissionThread(4), "Mission-4");

}
