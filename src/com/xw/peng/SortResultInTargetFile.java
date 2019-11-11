package com.xw.peng;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 执行完AppLaunchMain的脚本，得到输出结果之后。
 * 建议再执行此脚本，对结果进行排序
 */
public class SortResultInTargetFile {

    public static void main(String[] args) {
        String configrationFilPath = "./src/main/resources/configration.xml";
        AppLaunchMain.IGetConfigration iGetConfigration = AppLaunchMain.getConfigration(configrationFilPath);
        File file = new File(iGetConfigration.getObjectFilePath() + "/" + iGetConfigration.getObjectFileName() + ".txt");
        List<Result> results = new ArrayList<>();
        BufferedReader reader = null;
        RandomAccessFile randomAccessFile = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            randomAccessFile = new RandomAccessFile(file, "rw");
            String str;
            while ((str = reader.readLine()) != null) {
                Result result = new Result(getDataFromResultStr(str), str);
                if (results.contains(result)) {
                    break;
                }
                results.add(result);
            }
            if (results.size() > 0) {
                randomAccessFile.seek(randomAccessFile.length());//将操作文件的位置移到文件的末尾，方便在文件后追加文本
                randomAccessFile.write("\r\n".getBytes());
                randomAccessFile.write("\r\n".getBytes());
                randomAccessFile.write("此处为排列顺序之后的结果\r\n".getBytes());
            }
            Collections.sort(results);//排序
            /**
             * 0-250  优质
             * 250-400  良好
             * 400-500  一般
             * 500以上  较差
             */
            boolean you = false, yiban = false, jiaocha = false, cha = false, weizhi = false;
            for (Result result : results) {
                if (0 == result.date) {
                    if (!weizhi) {
                        randomAccessFile.write("\r\n".getBytes());
                        randomAccessFile.write("*未知的启动速度的界面".getBytes());
                        randomAccessFile.write("\r\n".getBytes());
                        weizhi = true;
                    }
                } else if (250 >= result.date) {
                    if (!you) {
                        randomAccessFile.write("\r\n".getBytes());
                        randomAccessFile.write("*优质的启动速度的界面".getBytes());
                        randomAccessFile.write("\r\n".getBytes());
                        you = true;
                    }
                } else if (400 >= result.date) {
                    if (!yiban) {
                        randomAccessFile.write("\r\n".getBytes());
                        randomAccessFile.write("*良好的启动速度的界面".getBytes());
                        randomAccessFile.write("\r\n".getBytes());
                        yiban = true;
                    }
                } else if (500 >= result.date) {
                    if (!jiaocha) {
                        randomAccessFile.write("\r\n".getBytes());
                        randomAccessFile.write("*一般的启动速度的界面".getBytes());
                        randomAccessFile.write("\r\n".getBytes());
                        jiaocha = true;
                    }
                } else {
                    if (!cha) {
                        randomAccessFile.write("\r\n".getBytes());
                        randomAccessFile.write("*较差的启动速度的界面".getBytes());
                        randomAccessFile.write("\r\n".getBytes());
                        cha = true;
                    }
                }
                randomAccessFile.write((result.str + "\r\n").getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (randomAccessFile != null) {
                try {
                    randomAccessFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static long getDataFromResultStr(String line) {
        final String regx = ": \\+(.*?ms)";
        final String regxContaintM = "(.*)s(.*)ms|(.*)ms";
        long date = 0L;
        if (line != null) {
            Pattern pattern = Pattern.compile(regx);
            Pattern timePattern = Pattern.compile(regxContaintM);
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                System.out.println(matcher.group(0));
                Matcher matcher1 = timePattern.matcher(matcher.group(1));
                if (matcher1.find()) {
                    if (matcher1.group(1) != null) {
                        date += Long.parseLong(matcher1.group(1)) * 1000;
                    }
                    if (matcher1.group(2) != null) {
                        date += Long.parseLong(matcher1.group(2));
                    }
                    if (matcher1.group(3) != null) {
                        date += Long.parseLong(matcher1.group(3));
                    }
                    System.out.println(date);
                }
            }
        }
        return date;
    }

    public static class Result implements Comparable<Result> {
        private long date;
        private String str;

        public Result(long date, String str) {
            this.date = date;
            this.str = str;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj instanceof Result) {
                return this.str.equals(((Result) obj).str);
            }
            return super.equals(obj);
        }

        @Override
        public int compareTo(Result o) {
            return Long.compare(date, o.date);
        }
    }
}
