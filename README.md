# nfb

#### 介绍
这是一个用来生成各种电子书格式的库。生成的电子书中之包含简单内容，不含复杂内容。

nfb是novel file builder的简写，就是小说文件构建器。
使用本项目生成的电子书文件都是严格符合标准的。

#### 软件架构
maven项目


#### 安装教程

1.  `cd nfb`
2.  `mvn clean instal`

#### 使用说明

1.  `plus.myj.nfb.entity.Nfb`是入口类，调用时需提供一个`plus.myj.nfb.entity.Novel`对象和`plus.myj.nfb.entity.FileFormat`对象。
2. 电子书的各种信息请自行参看`Novel`对象的各个字段。
3. 本项目生成的电子书支持多级目录，最多支持5层，超过会报错。
4. `Novel`对象包含电子书内容，`FileFormat`对象用于指定生成的电子书格式，可参考测试类`plus.myj.nfb.BuildTest`

#### 校验

- 生成的`epub`和`fb2`文件可以校验是否符合标准
- 测试类`plus.myj.nfb.BuildTest`用于校验生成`fb2`文件是否符合标准
- `epub2`和`epub3`的校验需自行下载，链接为`https://github.com/w3c/epubcheck/releases/download/v5.3.0/epubcheck-5.3.0.zip`
- `epub`文件的校验命令示例`java -jar epubcheck.jar <待校验的epub文件名>`，`epubcheck.jar`会自行检查时`epub2`还是`epub3`

#### 其他说明

- 现在生成的`fb2`文件不包含封面，且标准规定章节内容与子章节不能同时存在，生成时同时存在会报错。
- `epub`格式支持章节内容与子章节同时存在
- `txt`格式没有封面，没有目录。不过有的阅读器可以自行解读出目录

#### 参与贡献

1.  Fork 本仓库
2.  新建 Feat_xxx 分支
3.  提交代码
4.  新建 Pull Request


#### 特技

1.  使用 Readme\_XXX.md 来支持不同的语言，例如 Readme\_en.md, Readme\_zh.md
2.  Gitee 官方博客 [blog.gitee.com](https://blog.gitee.com)
3.  你可以 [https://gitee.com/explore](https://gitee.com/explore) 这个地址来了解 Gitee 上的优秀开源项目
4.  [GVP](https://gitee.com/gvp) 全称是 Gitee 最有价值开源项目，是综合评定出的优秀开源项目
5.  Gitee 官方提供的使用手册 [https://gitee.com/help](https://gitee.com/help)
6.  Gitee 封面人物是一档用来展示 Gitee 会员风采的栏目 [https://gitee.com/gitee-stars/](https://gitee.com/gitee-stars/)
