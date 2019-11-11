package com.xw.peng;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AndroidManifestHandle {
    private static final String MAIN_ACTION_NAME = "android.intent.action.MAIN";
    private static final String MAIN_CATEGORY_NAME = "android.intent.category.LAUNCHER";
    List<String> manifestFiles = null;
    String manifestFile;
    String projectPath;
    String mMainActivityString;
    List<String> forbidActivitys;


    public AndroidManifestHandle(String projectPath, String mainActivity, List<String> forbidActivities) {
        this.manifestFile = projectPath;
        this.mMainActivityString = mainActivity;
        this.forbidActivitys = forbidActivities;
    }


    public static void main(String[] arg) throws IOException, SAXException, ParserConfigurationException {
        File file = new File("E:\\Project\\repair_order\\app\\src\\main\\AndroidManifest.xml");
        System.out.println(file.getName());
        System.out.println(file.exists());
        System.out.println(file.getAbsolutePath());
//        System.out.println(analysisManifestFile(file.getAbsolutePath()).activities);
//        addExportSAttrInFile(file);
//        removeExportAttrInFile(file);
    }

    ManifestDataBean analysisManifestFile() throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document document = factory.newDocumentBuilder().parse(manifestFile);
        ManifestDataBean manifestDataBean = new ManifestDataBean();
        //获取package
        Node node = document.getFirstChild();
        System.out.println(node.getAttributes().getNamedItem("package").getNodeValue());
        manifestDataBean.packageName = node.getAttributes().getNamedItem("package").getNodeValue();
        //获取Activity列表
        NodeList nodeList = document.getElementsByTagName("activity");
        Element actionElement = document.createElement("action");
        actionElement.setAttribute("android:name", MAIN_ACTION_NAME);
        Element categoryElement = document.createElement("category");
        categoryElement.setAttribute("android:name", MAIN_CATEGORY_NAME);
        if (nodeList != null && nodeList.getLength() > 0) {
            manifestDataBean.activities = new ArrayList<>();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node1 = nodeList.item(i);
                String relativePath = node1.getAttributes().getNamedItem("android:name").getNodeValue();
                Activtiy activtiy = new Activtiy();
                activtiy.path = manifestDataBean.packageName + relativePath;
                if (activtiy.path.contains(mMainActivityString)) {
                    manifestDataBean.entry = activtiy;
                    manifestDataBean.activities.add(0, activtiy);
                } else {
                    String[] strs = activtiy.path.split("\\.");
                    if (forbidActivitys.size() > 0 && !forbidActivitys.contains(strs[strs.length - 1]))
                        manifestDataBean.activities.add(activtiy);
                }
            }
        }
        if (manifestDataBean.entry == null) {
            throw new RuntimeException("并未找到入口：" + mMainActivityString);
        }
        return manifestDataBean;
    }

    public void addExportSAttrInFile() {
        File file = new File(manifestFile);
        if (file != null) {
            try {
//                FileOutputStream fileOutputStream = new FileOutputStream(file);
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                Document document = factory.newDocumentBuilder().parse(file);
                NodeList nodeList = document.getElementsByTagName("activity");
                for (int i = 0; i < nodeList.getLength(); i++) {
                    Node node = nodeList.item(i);
                    Attr attr = document.createAttribute("android:exported");
                    attr.setValue("true");
                    node.getAttributes().setNamedItem(attr);
                    System.out.println(node.getAttributes().getNamedItem("android:name").getNodeValue());
                }

                //保存到本地
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                DOMSource source = new DOMSource(document);
                StreamResult streamResult = new StreamResult(file);
                transformer.transform(source, streamResult);
            } catch (IOException | ParserConfigurationException | SAXException | TransformerException e) {
                e.printStackTrace();
            }
        }
    }

    public void removeExportAttrInFile() {
        try {
//                FileOutputStream fileOutputStream = new FileOutputStream(file);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            Document document = factory.newDocumentBuilder().parse(manifestFile);
            NodeList nodeList = document.getElementsByTagName("activity");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                node.getAttributes().removeNamedItem("android:exported");
//                System.out.println(node.getAttributes().getNamedItem("android:name").getNodeValue());
            }

            //保存到本地
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(document);
            StreamResult streamResult = new StreamResult(manifestFile);
            transformer.transform(source, streamResult);

        } catch (IOException | ParserConfigurationException | SAXException | TransformerException e) {
            e.printStackTrace();
        }
    }

    ManifestDataBean analysisManifestFileOld() {
        File project = new File(projectPath);
        if (!project.exists() || project.isFile()) {
            throw new RuntimeException("项目路径错误");
        }
        searchManifestFile(project);

        System.out.println(manifestFiles);

        return null;

    }

    protected void searchManifestFile(File direct) {
        File[] rootFiles = direct.listFiles();
        for (int i = 0; i < rootFiles.length; i++) {
            File file = rootFiles[i];
            if (file.isFile() && file.getName().equals("AndroidManifest.xml")) {
                if (manifestFiles == null) {
                    manifestFiles = new ArrayList<>();
                }
                manifestFiles.add(file.getAbsolutePath());
            }
            if (file.isDirectory()) {
                searchManifestFile(file);
            }
        }
    }
}
