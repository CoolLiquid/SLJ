package com.xw.peng;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdbLogcatMonitor extends LogcatMonitoring {
    String objectFilePath;
    String projectPackname;
    List<String> recoder = new ArrayList<>();

    public static void main(String[] arg) {
        AdbLogcatMonitor adbLogcatMonitor = new AdbLogcatMonitor("C:\\Users\\wnp\\Desktop\\wnp.txt", "com.spec.wnp.myapplication");
        adbLogcatMonitor.monitor(new Result() {
            @Override
            public void result(boolean sucess) {
                System.out.println("adb logcat start success");
            }
        });
    }

    public AdbLogcatMonitor(String objectFilePath, String projectPackname) {
        this.objectFilePath = objectFilePath;
        this.projectPackname = projectPackname;
    }

    @Override
    public void monitor(Result result) {
        File file = new File(objectFilePath);
        String patternString = projectPackname + ".*";
        Pattern pattern = Pattern.compile(patternString);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
//            FileOutputStream fileOutputStream = new FileOutputStream(file);
            FileWriter writer = new FileWriter(file);
            executiveCommand("cmd /c" + "adb logcat", new Match() {
                boolean start = false;

                @Override
                public boolean match(boolean error, String line) throws IOException {
                    if (!start) {
                        result.result(true);
                        start = true;
                    }
                    Matcher matcher = null;
                    if (line.contains("ActivityManager")
                            && line.contains("Displayed")
                            && (matcher = pattern.matcher(line)).find()) {
                        System.out.println(matcher.group());
                        String recoderStr = matcher.group();
                        if (recoderStr.contains(":")) {
                            String[] strings = recoderStr.split(":");
                            if (strings.length > 0 && !recoder.contains(strings[0])) {
                                writer.write(matcher.group());
                                writer.append("\r\n");
                                writer.flush();
                                recoder.add(strings[0]);
                            }
                        }
                    }
                    return false;
                }

                @Override
                public void finish() throws IOException {
                    writer.close();
//                    fileOutputStream.close();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
