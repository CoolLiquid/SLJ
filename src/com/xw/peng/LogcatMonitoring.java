package com.xw.peng;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class LogcatMonitoring {

    private final static String PATTERN = "com.spec.wnp.myapplication/.activity.*";
    private final static String PATH = "C:/Users/wnp/Desktop/activity_launch.txt";
    private final static String MAINIFEST_PATH = "E:/Project/repair_order/app/src/main/AndroidManifest.xml";

    boolean finish = false;

    public static void main(String[] arg) {
        File file = new File("E:\\Project\\repair_order");
        PackApkMonitoring monitoring = new PackApkMonitoring(file.getAbsolutePath());
        monitoring.monitor(new Result() {
            @Override
            public void result(boolean success) {
                System.out.println(success);
            }
        });
    }

    public static void statisticsAppPagerEntry() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Process process = Runtime.getRuntime().exec("cmd /c" + "adb shell am start -W com.spec.wnp.myapplication/com.spec.wnp.myapplication.activity.LoginActivity");
                    InputStream inputStream = process.getErrorStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    String content = "";
                    while ((content = reader.readLine()) != null) {
                        System.out.println(content);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public abstract void monitor(Result result);

    public static void startLogcatMonitor() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String OSname = System.getProperty("os.name");
                Process p = null;
                try {
                    p = Runtime.getRuntime().exec("cmd /c" + "adb logcat");
                    InputStream inputStream = p.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line = "";
                    Pattern pattern = Pattern.compile(PATTERN);
                    File file = new File(PATH);
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    while ((line = reader.readLine()) != null) {// 循环读取
                        Matcher matcher = null;
                        if (line.contains("ActivityManager")
                                && line.contains("Displayed")
                                && (matcher = pattern.matcher(line)).find()) {
                            System.out.println(matcher.group());
                            fileOutputStream.write(matcher.group().getBytes());
                        }
                    }
                    reader.close();// 此处reader依赖于input，应先关闭
                    inputStream.close();
                    fileOutputStream.close();
                    p.destroy();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    public void executiveCommand(String command, Match match) {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(command);
            InputStream inputStream = process.getInputStream();
            InputStream errorStream = process.getErrorStream();
            monitorLog(inputStream, match, false);
            monitorLog(errorStream, match, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void executiveCommand(String[] cmdarray, Match match) {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(cmdarray);
            InputStream inputStream = process.getInputStream();
            InputStream errorStream = process.getErrorStream();
            monitorLog(inputStream, match, false);
            monitorLog(errorStream, match, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void executiveCommand(String command, File file, Match match) {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(command, null, file);
            InputStream inputStream = process.getInputStream();
            InputStream errorStream = process.getErrorStream();
            monitorLog(inputStream, match, false);
            monitorLog(errorStream, match, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void monitorLog(InputStream inputStream, Match match, boolean error) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (inputStream != null && match != null) {
                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                        String line = "";
                        while ((line = reader.readLine()) != null) {
                            synchronized (match) {
                                if (match.match(error, line)) {
                                    return;
                                }
                            }
                        }
                        reader.close();
                        synchronized (match) {
                            if (!finish) {
                                match.finish();
                                finish = true;
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }

    public interface Match {

        boolean match(boolean error, String line) throws IOException;//返回true，中断读取inputStream

        void finish() throws IOException;
    }

    public interface Result {
        void result(boolean sucess);
    }
}
