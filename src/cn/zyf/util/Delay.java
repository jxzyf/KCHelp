package cn.zyf.util;

public class Delay {

    public static final void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }
    }

    public static final void random() {
        try {
            Thread.sleep(500 + ((Double) (Math.random() * 1000)).longValue());
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }
    }

}
