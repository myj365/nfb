package plus.myj.nfb.builder.epub3;

import org.dom4j.*;
import plus.myj.nfb.builder.Builder;
import plus.myj.nfb.builder.entity.DepthLevel;
import plus.myj.nfb.builder.entity.EpubItem;
import plus.myj.nfb.entity.Chapter;
import plus.myj.nfb.entity.Novel;
import plus.myj.nfb.util.StrUtil;
import plus.myj.nfb.util.ZipUtil;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public enum Epub3Builder implements Builder {
    builder;

    @Override
    public byte[] build(Novel novel) throws IOException {
        final AtomicInteger atomic = new AtomicInteger();
        final List<EpubItem> epubItems = createEpubItems(novel.getChapters(), null, atomic, DepthLevel.volume);

        List<ZipUtil.ZipItem> list = new ArrayList<>();
        list.add(createMimetype());
        list.add(createContainerXml());
        list.add(OpfHandler.createOpf(novel, epubItems));
        list.add(TocHandler.createToc(epubItems));
        list.addAll(ContentHandler.createContent(epubItems));
        list.add(createDetailXhtml(novel));

        if (novel.getCoverImage() != null && novel.getCoverImageType() != null) {
            list.add(createCoverImage(novel));
        }

        return ZipUtil.zip(list);
    }

/*

EPUB3的文件结构如下：
minimal_epub3/
├── mimetype
├── META-INF/
│   └── container.xml
└── OEBPS/
    ├── package.opf         # 需修改：添加封面资源声明
    ├── toc.xhtml           # 无需修改
    ├── cover.xhtml         # 新增：独立封面页面
    ├── content/
    │   └── chapter1.xhtml  # 无需修改
    └── images/             # 新增：存放封面图片
        └── cover.jpg       # 封面图片（建议用JPG/PNG，分辨率≥1024×1448）
*/
    private ZipUtil.ZipItem createMimetype() {
        final String content = "application/epub+zip";

        final ZipUtil.ZipItem zipItem = new ZipUtil.ZipItem();
        zipItem.setPath("mimetype");
        zipItem.setContent(content.getBytes(StandardCharsets.UTF_8));
        return zipItem;
    }
    private ZipUtil.ZipItem createContainerXml() {
        final String content = """
                <?xml version="1.0" encoding="UTF-8"?>
                <container version="1.0" xmlns="urn:oasis:names:tc:opendocument:xmlns:container">
                    <rootfiles>
                        <rootfile full-path="OEBPS/package.opf" media-type="application/oebps-package+xml"/>
                    </rootfiles>
                </container>""";

        final ZipUtil.ZipItem zipItem = new ZipUtil.ZipItem();
        zipItem.setPath("META-INF/container.xml");
        zipItem.setContent(content.getBytes(StandardCharsets.UTF_8));
        return zipItem;
    }

    private static List<EpubItem> createEpubItems(List<Chapter> chapters, EpubItem parentItem, AtomicInteger atomic, DepthLevel depthLevel) {
        if (depthLevel == null) {
            return Collections.emptyList();
        }

        final List<EpubItem> items = new ArrayList<>();
        for (int i = 0; i < chapters.size(); i++) {
            final Chapter chapter = chapters.get(i);

            final EpubItem item = new EpubItem();
            item.setDepthLevel(depthLevel);
            item.setOrder(String.valueOf(atomic.incrementAndGet()));

            if (depthLevel == DepthLevel.volume) {
                item.setId(depthLevel.name() + i);
                item.setHref("content/" + depthLevel.name() + i + ".xhtml");
            } else if (depthLevel == DepthLevel.chapter) {
                item.setId(parentItem.getId() + "_" + depthLevel.name() + i);
                item.setHref("content/" + parentItem.getId() + "_" + depthLevel.name() + i + ".xhtml");
            } else {
                item.setId(parentItem.getId() + "_" + depthLevel.name() + i);
                item.setHref(StrUtil.startSubstringBySymbol(parentItem.getHref(), "#") + "#" + item.getId());
            }

            item.setTitle(chapter.getTitle());
            item.setContents(chapter.getContents());

            if (chapter.getSubChapters() != null && !chapter.getSubChapters().isEmpty()) {
                final List<EpubItem> subItems = createEpubItems(chapter.getSubChapters(), item, atomic, DepthLevel.getNextDepthLevel(depthLevel));
                item.setSubEpubItems(subItems);
            }

            items.add(item);
        }
        return items;
    }

    private static ZipUtil.ZipItem createDetailXhtml(Novel novel) {
        final Document doc = DocumentHelper.createDocument();

        final Namespace namespace = Namespace.get("", "http://www.w3.org/1999/xhtml");
        final Element root = doc.addElement(QName.get("html", namespace));
        root.addNamespace("epub", "http://www.idpf.org/2007/ops");


        final Element head = root.addElement("head");
        head.addElement("meta").addAttribute("charset", "UTF-8");
        head.addElement("title").setText("封面");

        final Element body = root.addElement("body");

        if (novel.getCoverImage() != null && novel.getCoverImageType() != null) {
            body.addElement("div").addAttribute(QName.get("type", Namespace.get("epub", "http://www.idpf.org/2007/ops")), "cover");

            final Element img = body.addElement("img");
            img.addAttribute("src", "images/cover." + novel.getCoverImageType().name());
            img.addAttribute("alt", "图书封面");
        }


        final Element detail = body.addElement("div");
        detail.addElement("h1").setText(StrUtil.nullToEmpty(novel.getName()));
        if (StrUtil.notEmpty(novel.getAuthor())) detail.addElement("h4").setText(novel.getAuthor());
        if (StrUtil.notEmpty(novel.getType())) detail.addElement("h4").setText(novel.getType());
        if (StrUtil.notEmpty(novel.getDescription())) {
            detail.addElement("strong").setText("简介");
            detail.addElement("p").setText(novel.getDescription());
        }


        final String xml = StrUtil.xmlPretty(doc);

        final ZipUtil.ZipItem zipItem = new ZipUtil.ZipItem();
        zipItem.setPath("OEBPS/detail.xhtml");
        zipItem.setContent(xml.getBytes(StandardCharsets.UTF_8));
        return zipItem;
    }
    private static ZipUtil.ZipItem createCoverImage(Novel novel) {
        final ZipUtil.ZipItem item = new ZipUtil.ZipItem();
        item.setPath("OEBPS/images/cover." + novel.getCoverImageType().name());
        item.setContent(novel.getCoverImage());
        return item;
    }
}
