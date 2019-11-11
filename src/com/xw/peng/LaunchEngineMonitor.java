package com.xw.peng;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LaunchEngineMonitor extends LogcatMonitoring {
    ManifestDataBean manifestDataBean;
    List<String> alreadyLaunActivities = new ArrayList<>();

    private static final String errorFilePath = "C:\\Users\\wnp\\Desktop\\errorLaunch.txt";

    public static void main(String[] arg) {
        LaunchEngineMonitor launchEngine = new LaunchEngineMonitor(null);
        launchEngine.monitor(new Result() {
            @Override
            public void result(boolean sucess) {
                System.out.println("运行成功，运行失败");
            }
        });
    }


    public LaunchEngineMonitor(ManifestDataBean manifestDataBean) {
        this.manifestDataBean = manifestDataBean;
    }

    @Override
    public void monitor(Result result) {
        result.result(launchActivity());
    }

    public boolean launchActivity() {
        if (manifestDataBean != null
                && manifestDataBean.activities != null
                && manifestDataBean.activities.size() > 0) {
            File file = new File(errorFilePath);
            try {
                final FileWriter writer = new FileWriter(file);
                for (int i = 0; i < manifestDataBean.activities.size(); i++) {

                    executiveCommand("adb shell am start -n " + manifestDataBean.packageName + "/" + manifestDataBean.activities.get(i),
                            new Match() {
                                @Override
                                public boolean match(boolean error, String line) throws IOException {
                                    if (!error) {
                                        alreadyLaunActivities.add(line);
                                    }
                                    if (error) {
                                        if (alreadyLaunActivities.size() > 0) {
                                            synchronized (writer) {
                                                writer.write(alreadyLaunActivities.get(alreadyLaunActivities.size() - 1));
                                                writer.append("\r\n");
                                                writer.flush();
                                            }
                                        }
                                    }
                                    System.out.println(line);
                                    return false;
                                }

                                @Override
                                public void finish() throws IOException {

                                }
                            });
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (i == 0) {
                        acceptStartChartToStartProgram();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    public void acceptStartChartToStartProgram() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("请先登录应用。。。并输入start字符开始启动测试");
        String startChart = "start";
        String first = scanner.findInLine(startChart);
        if (startChart.equals(first)) {
            return;
        } else {
            acceptStartChartToStartProgram();
        }
    }
}
