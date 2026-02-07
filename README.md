

# nfb

## 介绍

nfb（Novel File Builder）是一个用于生成各种电子书格式的Java库。该项目专注于生成包含简单内容的电子书，不支持复杂内容格式。所有生成的电子书文件都严格符合相应的标准规范。

## 软件架构

本项目采用Maven构建，使用Builder设计模式实现多种电子书格式的生成：

- **核心入口**：`Nfb`类作为主入口类，负责调度不同的格式构建器
- **实体类**：`Novel`、`Chapter`、`FileFormat`、`ImageType`等核心实体
- **构建器接口**：`Builder`接口定义统一构建规范
- **格式构建器**：分别实现EPUB2、EPUB3、FB2、FB2ZIP、TXT、MarkDown六种格式
- **工具类**：`StrUtil`提供字符串处理，`ZipUtil`处理文件压缩

## 安装教程

```bash
cd nfb
mvn clean install
```

## 使用说明

### 基本用法

1. 创建`Novel`对象，设置电子书基本信息（书名、作者、类型、简介、封面等）
2. 准备`Chapter`列表，设置章节内容和子章节
3. 调用`Nfb.build(novel, format)`方法生成电子书

```java
// 创建电子书对象
Novel novel = new Novel();
novel.setName("电子书名称");
novel.setAuthor("作者名");
novel.setLanguage("zh-CN");
novel.setCoverImage(coverImageBytes);
novel.setCoverImageType(ImageType.JPEG);
novel.setPublisher("出版社");
novel.setType("小说类型");
novel.setDescription("简介");

// 创建章节
List<Chapter> chapters = new ArrayList<>();
Chapter chapter = new Chapter();
chapter.setTitle("第一章");
chapter.setContents(Arrays.asList("章节内容1", "章节内容2"));
chapters.add(chapter);
novel.setChapters(chapters);

// 生成电子书
byte[] epubBytes = Nfb.build(novel, FileFormat.EPUB2);
```

### 目录深度限制

- 支持最多5级目录结构
- 超过5级会抛出异常
- FB2格式要求章节内容与子章节不能同时存在

## 支持的文件类型

| 格式 | 说明 | 文件扩展名 | 目录支持 | 封面支持 |
|------|------|-----------|---------|---------|
| EPUB2 | EPUB 2.0标准电子书 | .epub | ✓ | ✓ |
| EPUB3 | EPUB 3.0标准电子书 | .epub | ✓ | ✓ |
| FB2 | FictionBook开源电子书格式 | .fb2 | ✓ | ✓ |
| FB2ZIP | FB2压缩包格式 | .fb2.zip | ✓ | ✓ |
| TXT | 纯文本文件 | .txt | ✗ | ✗ |
| MarkDown | 轻量级标记语言 | .md | ✓ | ✓ |

## 校验说明

生成的EPUB和FB2文件可进行标准校验：

- **FB2校验**：使用测试类`plus.myj.nfb.Fb2ValidateTest`进行XSD校验
- **EPUB校验**：下载EPUBCheck工具
  ```bash
  java -jar epubcheck.jar <待校验的epub文件名>
  ```

EPUBCheck下载地址：https://github.com/w3c/epubcheck/releases/download/v5.3.0/epubcheck-5.3.0.zip

## 其他说明

- **FB2格式**：已支持封面图片；标准规定章节内容与子章节不能同时存在
- **EPUB格式**：支持章节内容与子章节同时存在
- **TXT格式**：无封面无目录，部分阅读器可自动识别目录结构

## 项目结构

```
nfb/
├── src/main/java/plus/myj/nfb/
│   ├── Nfb.java                    # 主入口类
│   ├── builder/                    # 构建器接口和实现
│   │   ├── Builder.java
│   │   ├── entity/
│   │   │   ├── DepthLevel.java
│   │   │   └── EpubItem.java
│   │   ├── epub2/                  # EPUB2构建器
│   │   ├── epub3/                  # EPUB3构建器
│   │   ├── fb2/                    # FB2构建器
│   │   ├── fb2zip/                 # FB2ZIP构建器
│   │   ├── markdown/               # MarkDown构建器
│   │   └── txt/                    # TXT构建器
│   ├── entity/                     # 核心实体类
│   │   ├── Novel.java
│   │   ├── Chapter.java
│   │   ├── FileFormat.java
│   │   └── ImageType.java
│   └── util/                       # 工具类
│       ├── StrUtil.java
│       └── ZipUtil.java
└── src/test/                       # 测试代码和资源
```

## 参与贡献

1. Fork本仓库
2. 新建Feat_xxx分支
3. 提交代码
4. 新建Pull Request