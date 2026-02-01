package plus.myj.nfb.builder.epub3;

import org.dom4j.*;
import plus.myj.nfb.builder.entity.EpubItem;
import plus.myj.nfb.entity.Novel;
import plus.myj.nfb.util.StrUtil;
import plus.myj.nfb.util.ZipUtil;

import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

public class OpfHandler {
    public static ZipUtil.ZipItem createOpf(Novel novel, List<EpubItem> items) {
        final Document doc = DocumentHelper.createDocument();

        createRoot(doc, novel, items);
        final String xml = StrUtil.xmlPretty(doc);

        final ZipUtil.ZipItem zipItem = new ZipUtil.ZipItem();
        zipItem.setPath("OEBPS/package.opf");
        zipItem.setContent(xml.getBytes(StandardCharsets.UTF_8));
        return zipItem;
    }

    private static void createRoot(Document doc, Novel novel, List<EpubItem> items) {
        final Namespace namespace = Namespace.get("", "http://www.idpf.org/2007/opf");
        final Element root = doc.addElement(QName.get("package", namespace));
        root.addAttribute("version", "3.0");
        root.addAttribute("unique-identifier", "pub-id");

        createMetadata(root, novel);
        createManifest(root, novel, items);
        createSpine(root, novel, items);
    }



    private static void createMetadata(Element root, Novel novel) {
        final Namespace namespace = Namespace.get("dc", "http://purl.org/dc/elements/1.1/");

        final Element metadata = root.addElement("metadata");
        metadata.addNamespace("dcterms", "http://purl.org/dc/terms/");
        metadata.addNamespace("dc", "http://purl.org/dc/elements/1.1/");


        metadata.addElement(QName.get("title", namespace)).setText(StrUtil.nullToEmpty(novel.getName()));
        metadata.addElement(QName.get("creator", namespace)).setText(StrUtil.isEmpty(novel.getAuthor()) ? "佚名" : novel.getAuthor());
        metadata.addElement(QName.get("identifier", namespace)).addAttribute("id", "pub-id").setText("urn:uuid:" + UUID.randomUUID());
        metadata.addElement(QName.get("language", namespace)).setText(StrUtil.isEmpty(novel.getLanguage()) ? "zh" : novel.getLanguage());
        metadata.addElement("meta").addAttribute("property", "dcterms:modified").setText(ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")));

        if (StrUtil.notEmpty(novel.getPublisher())) metadata.addElement(QName.get("publisher", namespace)).setText(StrUtil.nullToEmpty(novel.getPublisher()));
        if (StrUtil.notEmpty(novel.getDescription())) metadata.addElement(QName.get("description", namespace)).addCDATA(StrUtil.nullToEmpty(novel.getDescription()));
        if (StrUtil.notEmpty(novel.getType())) metadata.addElement(QName.get("type", namespace)).setText(StrUtil.nullToEmpty(novel.getType()));
        if (StrUtil.notEmpty(novel.getType())) metadata.addElement(QName.get("subject", namespace)).setText(StrUtil.nullToEmpty(novel.getType()));
    }



    private static void createManifest(Element root, Novel novel, List<EpubItem> epubItems) {
        final Element manifest = root.addElement("manifest");

        if (novel.getCoverImage() != null && novel.getCoverImageType() != null) {
            // 封面图片（可选，若未加封面可删除
            manifest.addElement("item").addAttribute("id", "cover-img").addAttribute("href", "images/cover." + novel.getCoverImageType().name()).addAttribute("media-type", novel.getCoverImageType().getMediaType()).addAttribute("properties", "cover-image");

        }

        // 详情页
        manifest.addElement("item").addAttribute("id", "detail").addAttribute("href", "detail.xhtml").addAttribute("media-type", "application/xhtml+xml");
        // 导航文档：仅需properties="nav"标识，无需toc属性
        manifest.addElement("item").addAttribute("id", "toc").addAttribute("href", "toc.xhtml").addAttribute("media-type", "application/xhtml+xml").addAttribute("properties", "nav");

        // 接下来是具体的章节内容的列表
        if (epubItems != null) {
            for (EpubItem epubItem : epubItems) {
                createItem(manifest, epubItem);
            }
        }
    }
    private static void createItem(Element manifest, EpubItem epubItem) {
        manifest.addElement("item")
                .addAttribute("id", epubItem.getId())
                .addAttribute("href", epubItem.getHref())
                .addAttribute("media-type", "application/xhtml+xml");

        if (epubItem.getSubEpubItems() != null && !epubItem.getSubEpubItems().isEmpty()) {
            for (EpubItem subEpubItem : epubItem.getSubEpubItems()) {
                createSubItem(manifest, subEpubItem);
            }
        }
    }
    private static void createSubItem(Element manifest, EpubItem epubItem) {
        manifest.addElement("item")
                .addAttribute("id", epubItem.getId())
                .addAttribute("href", epubItem.getHref())
                .addAttribute("media-type", "application/xhtml+xml");
    }



    private static void createSpine(Element root, Novel novel, List<EpubItem> epubItems) {
        final Element spine = root.addElement("spine");

        if (novel.getCoverImage() != null && novel.getCoverImageType() != null) {
            spine.addElement("itemref").addAttribute("idref", "detail");
        }

        for (EpubItem epubItem : epubItems) {
            createItemref(spine, epubItem);
        }
    }
    private static void createItemref(Element spine, EpubItem epubItem) {
        spine.addElement("itemref").addAttribute("idref", epubItem.getId());

        if (epubItem.getSubEpubItems() != null && !epubItem.getSubEpubItems().isEmpty()) {
            for (EpubItem subEpubItem : epubItem.getSubEpubItems()) {
                createSubItemref(spine, subEpubItem);
            }
        }
    }
    private static void createSubItemref(Element spine, EpubItem epubItem) {
        spine.addElement("itemref").addAttribute("idref", epubItem.getId());
    }
}
