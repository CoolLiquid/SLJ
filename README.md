# SLJ
稍后会更新使用说明和说明实现原理

**这是加粗的文字**

*这是倾斜的文字*

***这是斜体加粗的文字***

~~这是加删除线的文字~~

>这是引用的内容
>>这是引用的内容
>>>这是引用的内容
>>>>>>>>>>这是引用的内容

---
----
***
*****

![blockchain](https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/
u=702257389,1274025419&fm=27&gp=0.jpg "区块链")

[简书](http://jianshu.com)
[百度](http://baidu.com)

(```)
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
(```)

```flow
st=>start: 开始
op=>operation: My Operation
cond=>condition: Yes or No?
e=>end
st->op->cond
cond(yes)->e
cond(no)->op
&```


