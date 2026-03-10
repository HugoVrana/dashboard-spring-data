package com.dashboard.context;

public class DiffContext {

    private static final ThreadLocal<String> DIFF_HOLDER = new ThreadLocal<>();

    public static void setDiff(String diff) {
        DIFF_HOLDER.set(diff);
    }

    public static String getDiff() {
        return DIFF_HOLDER.get();
    }

    public static void clear() {
        DIFF_HOLDER.remove();
    }
}
