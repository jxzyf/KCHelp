package cn.zyf.entry;

import cn.zyf.config.Config;
import cn.zyf.operation.Data;
import cn.zyf.operation.Port;
import cn.zyf.resource.Resource;
import cn.zyf.util.Delay;

import javax.annotation.PostConstruct;
import java.util.Map;

public class Main {

    class MT implements Runnable {

        void f() {
            throw new RuntimeException();
        }

        @Override
        public void run() {
            int i = 0;
            while (!Thread.currentThread().isInterrupted()) {
                Delay.sleep(1300);
                System.out.println(Thread.currentThread().isInterrupted());
            }
        }
    }

    public static void main(String[] args) {
        Config.t2 = new Thread(new Main().new MT(), "666");
        Config.t2.setDaemon(true);
        int i = 0;
        while (i < 1000) {
            try {
                System.out.println(Config.t2.getState() + "===" + Config.t2.isAlive());
                Thread.sleep(1000);
                if (i == 0) {
                    Config.t2.start();
                }
                if (i == 7) {
                    Config.t2.interrupt();
                }
                i += 1;
            } catch (Exception e) {}
        }
    }

    @PostConstruct
    private void run() throws Exception {
        Resource.setProperty("program_start_time", System.currentTimeMillis());
        // 先收集所有必要信息，保存至Resource中
        Data.get();
        Port.port();
        Config.t2.start();
        Config.t3.start();
        Config.t4.start();
    }

}
