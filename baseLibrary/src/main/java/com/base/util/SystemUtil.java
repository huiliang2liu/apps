package com.base.util;

import android.os.Process;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class SystemUtil {
    static String processor;
    static String architecture;
    static String hardware;
    static float bootTime;
    static float freeTime;
    static String packageName;
    static float percent1;
    static float percent5;
    static float percent15;
    static int runProcess;
    static int totalProcess;
    static List<Integer> upc = new ArrayList<>();
    static int cpuNum;
    static List<CpuInfo> infos = new ArrayList<>();

    static {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/cpuinfo"));
            String line = null;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty())
                    continue;
                if (line.startsWith("Processor")) {
                    processor = line.split(":")[1].trim();
                } else if (line.startsWith("CPU architecture")) {
                    architecture = line.split(":")[1].trim();
                } else if (line.startsWith("Hardware")) {
                    hardware = line.split("Hardware")[1].trim();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        try {
            reader = new BufferedReader(new FileReader("/proc/uptime"));
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty())
                    continue;
                String sp[] = line.split(" ");
                bootTime = Float.valueOf(sp[0]) * 1000;
                freeTime = Float.valueOf(sp[1]) * 1000;
                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        try {
            reader = new BufferedReader(new FileReader(String.format("/proc/%s/cmdline", Process.myPid())));
            String line = null;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty())
                    continue;
                int index = line.indexOf(":");
                if (index >= 0) {
                    packageName = line.substring(0, index);
                } else
                    packageName = line;
                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        try {
            reader = new BufferedReader(new FileReader("/proc/loadavg"));
            String line = null;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty())
                    continue;
                String[] sp = line.split(" ");
                percent1 = Float.valueOf(sp[0].trim());
                percent5 = Float.valueOf(sp[1].trim());
                percent15 = Float.valueOf(sp[2].trim());
                sp = sp[3].trim().split("/");
                runProcess = Integer.valueOf(sp[0]);
                totalProcess = Integer.valueOf(sp[1]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        File[] files = new File("/sys/devices/system/cpu/").listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return Pattern.matches("cpu[0-9]", pathname.getName());
            }
        });
        cpuNum = files.length;

        for (File file : files) {
            CpuInfo info = new CpuInfo();
            info.curFreq = readFreq(new File(file, "cpufreq/cpuinfo_cur_freq"));
            info.maxFreq = readFreq(new File(file, "cpufreq/cpuinfo_max_freq"));
            info.minFreq = readFreq(new File(file, "cpufreq/cpuinfo_min_freq"));
            info.scalingMaxFreq = readFreq(new File(file, "cpufreq/scaling_max_freq"));
            info.scalingMinFreq = readFreq(new File(file, "cpufreq/scaling_min_freq"));
            infos.add(info);
        }
    }

    private static long readFreq(File file) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String ling = null;
            while ((ling = reader.readLine()) != null) {
                ling = ling.trim();
                if (ling.isEmpty())
                    continue;
                return Long.valueOf(ling) * 1000;
            }
        } catch (Exception e) {
        } finally {
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return 0;
    }

    //    AC powered: false ///有线充电器状态
//    USB powered: true ///USB连接状态
//    Wireless powered: false ///无线充电状态
//    Max charging current: 500000 ///最大充电电流，单位微安(uA)
//    Max charging voltage: 5000000 ///最大充电电压，单位微伏(uV)
//    Charge counter: 4149000 ///
//    status: 2 ///充电状态，UNKNOWN=1，CHARGING=2，DISCHARGING=3，NOT_CHARGING=4，FULL=5
//    health: 2 ///电池健康状态
//    present: true ///
//    level: 95 ///当前95%
//    scale: 100 ///满电100%
//    voltage: 4244 ///电压
//    temperature: 250 ///温度
//    technology: Li-ion
    public static boolean powered() {
        Map<String, String> power = power();
        if (power.containsKey("status".toLowerCase())) {
            return power.get("status".toLowerCase()).equals("2");
        }
        return false;
    }

    public static Map<String, String> power() {
        Map<String, String> power = new HashMap<>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec("adb shell dumpsys battery").getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty())
                    continue;
                String[] split = line.split(":");
                if (split.length < 2)
                    continue;
                String key = split[0].trim().toLowerCase();
                String value = split[1].trim();
                if (key.equalsIgnoreCase("level"))
                    if (value.equals("0"))
                        continue;
                if (key.equalsIgnoreCase("status"))
                    if (value.equals("1"))
                        continue;
                power.put(key, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

        return power;
    }

    public static class CpuInfo {
        long curFreq;
        long maxFreq;
        long minFreq;
        long scalingMaxFreq;
        long scalingMinFreq;
    }

}
