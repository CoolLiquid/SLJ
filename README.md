<!--
                1.分析结果输出路径
                2.android项目的地址
-->
<!--
                脚本处理流程：
                1.生成结果分析路径文件
                2.分析项目AndroidManifest.xml，得到需要测试的Activity列表
                3.对AndroidManifest.xml文件进行处理
                4.编译项目并运行
                5.启动adb-logcat命令
                6.启动对应的Activity，获取结果并输出到结果文件中
                7.关闭adb-logcat程序
                8.并恢复AndroidManifest.xml
-->