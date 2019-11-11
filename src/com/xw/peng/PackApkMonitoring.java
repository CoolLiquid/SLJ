package com.xw.peng;


import java.io.File;
import java.io.IOException;

public class PackApkMonitoring extends LogcatMonitoring {

    String projectPath;
    private final static String COMMAND = "gradlew.bat assembleDebug";
    private final static String BUILT_FAIL = "BUILD FAIL";
    private final static String BUILT_SUCC = "BUILD SUCCESSFUL";

    public PackApkMonitoring(String projectPath) {
        this.projectPath = projectPath;
    }

    @Override
    public void monitor(Result result) {
        System.out.println("============正在为项目打包===================");
        if (!projectPath.contains(":")) {
            throw new RuntimeException("请确保项目路径为绝对路径");
        }
        Match match = new Match() {

            @Override
            public boolean match(boolean error, String line) throws IOException {
                System.out.println(line);
                if (line.contains(BUILT_SUCC)) {
                    result.result(true);
                    return true;
                }
                if (line.contains(BUILT_FAIL)) {
                    result.result(false);
                    return true;
                }
                return error;
            }

            @Override
            public void finish() throws IOException {
            }
        };
        executiveCommand("cmd /c " + COMMAND, new File(projectPath), match);
    }
}
