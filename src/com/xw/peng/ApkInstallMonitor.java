package com.xw.peng;

import java.io.IOException;

public class ApkInstallMonitor extends LogcatMonitoring {

    private final static String SUCCESS = "Success";
    String apkPath;

    public static void main(String[] arg) {
        new ApkInstallMonitor("C:\\Users\\wnp\\TgnetInformation\\app\\build\\outputs\\apk\\debug\\app-debug.apk")
                .monitor(new Result() {
                    @Override
                    public void result(boolean sucess) {
                        System.out.println(sucess);
                    }
                });

    }

    public ApkInstallMonitor(String apkPath) {
        this.apkPath = apkPath;
    }

    @Override
    public void monitor(Result result) {
        System.out.println("======================APK安装======================");
        executiveCommand("cmd /c adb install " + apkPath, new Match() {

            @Override
            public boolean match(boolean error, String line) {
                System.out.println(line);
                result.result(line.contains(SUCCESS));
                return false;
            }

            @Override
            public void finish() throws IOException {

            }
        });
    }
}
