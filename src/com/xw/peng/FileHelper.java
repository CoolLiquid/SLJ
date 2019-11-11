package com.xw.peng;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileHelper {

    public static void main(String[] arg) {
        String file = searchFile("C:\\Users\\wnp\\TgnetInformation", "app-debug.apk");
        System.out.println(file);
    }

    public static String searchFile(String path, String objectFileName) {
        File project = new File(path);
        if (!project.exists() || project.isFile()) {
            throw new RuntimeException("项目路径错误");
        }
        List<String> paths = new ArrayList<>();
        searchManifestFile(project, objectFileName, paths);
        System.out.println();
        return paths.size() == 0 ? null : paths.get(0);
    }

    private static void searchManifestFile(File direct, String objectFileName, List<String> paths) {
        File[] rootFiles = direct.listFiles();
        for (int i = 0; i < rootFiles.length; i++) {
            File file = rootFiles[i];
            if (file.isFile() && file.getName().equals(objectFileName)) {
                paths.add(file.getAbsolutePath());
            }
            if (file.isDirectory()) {
                searchManifestFile(file, objectFileName, paths);
            }
        }
    }
}
