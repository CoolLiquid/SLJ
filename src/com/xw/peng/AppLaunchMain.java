package com.xw.peng;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AppLaunchMain {
    //脚本程序入口
    public static void main(String[] arg) {


        String configrationFilPath = "./src/resources/configration.xml";
        IGetConfigration iGetConfigration = getConfigration(configrationFilPath);
        if (iGetConfigration != null) {
            File file = new File(iGetConfigration.getObjectFilePath(), iGetConfigration.getObjectFileName() + ".txt");
            try {
                if (!file.exists()) {
                    file.createNewFile();
                }
                //从Manifest文件中获取相关信息并处理manifest文件
                AndroidManifestHandle androidManifestHandle = new AndroidManifestHandle(iGetConfigration.getManifestFilePath()
                        , iGetConfigration.getMainActivity(), iGetConfigration.getForbidActivities());
                ManifestDataBean manifestDataBean = androidManifestHandle.analysisManifestFile();
                androidManifestHandle.addExportSAttrInFile();

               /* startLogcatMonitor(
                        file.getAbsolutePath()
                        , manifestDataBean.packageName);
                startLaunchEngineMonitor(manifestDataBean);*/

                //为工程项目打包
                LogcatMonitoring logcatMonitoring = new PackApkMonitoring(iGetConfigration.getProjectPath());
                logcatMonitoring.monitor(new LogcatMonitoring.Result() {
                    @Override
                    public void result(boolean sucess) {
                        if (sucess) {
                            //去除manifest中的额外添加的属性
                            androidManifestHandle.removeExportAttrInFile();
                            //安装apk到手机上
                            String apkPath = FileHelper.searchFile(iGetConfigration.getProjectPath(), "app-debug.apk");
                            if (apkPath == null) {
                                throw new RuntimeException("未找到刚刚打出来的apk包");
                            }

                            ApkInstallMonitor apkInstallMonitor = new ApkInstallMonitor(apkPath);
                            apkInstallMonitor.monitor(new LogcatMonitoring.Result() {
                                @Override
                                public void result(boolean sucess) {
                                    if (sucess) {
                                        startLogcatMonitor(
                                                file.getAbsolutePath()
                                                , manifestDataBean.packageName);
                                        startLaunchEngineMonitor(manifestDataBean);

                                    } else {
                                        System.out.println("应用安装失败，请确保AS和手机是连上的同时要安装的应用需要先卸载掉");
                                    }
                                }
                            });
                        }
                    }
                });
            } catch (IOException | ParserConfigurationException | SAXException e) {
                e.printStackTrace();
            }
        }
    }


    public static void startLogcatMonitor(String objectFile, String packName) {
        //启动logcat 命令
        AdbLogcatMonitor adbLogcatMonitor = new AdbLogcatMonitor(objectFile, packName);
        adbLogcatMonitor.monitor(new LogcatMonitoring.Result() {
            @Override
            public void result(boolean sucess) {
                System.out.println("adb logcat start success");
            }
        });
    }

    public static void startLaunchEngineMonitor(ManifestDataBean manifestDataBean) {
        LaunchEngineMonitor launchEngineMonitor = new LaunchEngineMonitor(manifestDataBean);
        launchEngineMonitor.monitor(new LogcatMonitoring.Result() {
            @Override
            public void result(boolean sucess) {
                String str = sucess ? "应用运行成功" : "应用运行失败";
                System.out.println(str);
            }
        });
    }


    public static IGetConfigration getConfigration(String configrationFilPath) {
        String objectPath = null;
        String projectPath = null;
        String objectFileName = null;
        String mainfestFilePath = null;
        String mainActivity = null;
        List<String> forbidActivities = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            Document document = factory.newDocumentBuilder().parse(configrationFilPath);
            NodeList documentNodeLists = document.getDocumentElement().getChildNodes();
            if (documentNodeLists != null) {
                for (int i = 0; i < documentNodeLists.getLength(); i++) {
                    Node node = documentNodeLists.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        System.out.println(node.getNodeName());
                        for (Node childNode = node.getFirstChild(); childNode != null; childNode = childNode
                                .getNextSibling()) {
                            if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                                if (childNode.getNodeName().equals("path")) {
                                    if (childNode.getParentNode().getNodeName().equals("result")) {
                                        System.out.println(childNode.getNodeName());
                                        System.out.println(childNode.getFirstChild().getNodeValue());
                                        objectPath = childNode.getFirstChild().getNodeValue();
                                    }
                                    if (childNode.getParentNode().getNodeName().equals("project")) {
                                        System.out.println(childNode.getNodeName());
                                        System.out.println(childNode.getFirstChild().getNodeValue());
                                        projectPath = childNode.getFirstChild().getNodeValue();
                                    }
                                }
                                if (childNode.getNodeName().equals("fileName")) {
                                    if (childNode.getParentNode().getNodeName().equals("result")) {
                                        objectFileName = childNode.getFirstChild().getNodeValue();
                                        System.out.println(objectFileName);
                                    }
                                }

                                if (childNode.getNodeName().equals("manifest")) {
                                    if (childNode.getParentNode().getNodeName().equals("project")) {
                                        mainfestFilePath = childNode.getFirstChild().getNodeValue();
                                        System.out.println(mainfestFilePath);
                                    }
                                }

                                if (childNode.getNodeName().equals("main-activity")) {
                                    if (childNode.getParentNode().getNodeName().equals("project")) {
                                        mainActivity = childNode.getFirstChild().getNodeValue();
                                        System.out.println(mainActivity);
                                    }
                                }

                                if (childNode.getNodeName().equals("filter-activity")) {
                                    if (childNode.getParentNode().getNodeName().equals("project")) {
                                        String activity = childNode.getFirstChild().getNodeValue();
                                        forbidActivities.add(activity);
                                        System.out.println(activity);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        String finalObjectPath = objectPath;
        String finalProjectPath = projectPath;
        String finalObjectFileName = objectFileName;
        String finalMainfestFilePath = mainfestFilePath;
        String finalMainActivity = mainActivity;
        return new IGetConfigration() {
            @Override
            public String getObjectFilePath() {
                return finalObjectPath;
            }

            @Override
            public String getProjectPath() {
                return finalProjectPath;
            }

            @Override
            public String getObjectFileName() {
                return finalObjectFileName;
            }

            @Override
            public String getManifestFilePath() {
                return finalMainfestFilePath;
            }

            @Override
            public String getMainActivity() {
                return finalMainActivity;
            }

            @Override
            public List<String> getForbidActivities() {
                return forbidActivities;
            }
        };
    }

    public interface IGetConfigration {
        String getObjectFilePath();

        String getProjectPath();

        String getObjectFileName();

        String getManifestFilePath();

        String getMainActivity();

        List<String> getForbidActivities();
    }
}
