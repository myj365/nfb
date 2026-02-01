package plus.myj.nfb.builder.epub2;

import org.dom4j.*;
import plus.myj.nfb.builder.entity.EpubItem;
import plus.myj.nfb.entity.Novel;
import plus.myj.nfb.util.StrUtil;
import plus.myj.nfb.util.ZipUtil;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

public class OpfHandler {
    public static ZipUtil.ZipItem createOpf(Novel novel, List<EpubItem> items, String uuid) {
        final Document doc = DocumentHelper.createDocument();

        createRoot(doc, novel, items, uuid);
        final String xml = StrUtil.xmlPretty(doc);

        final ZipUtil.ZipItem zipItem = new ZipUtil.ZipItem();
        zipItem.setPath("OEBPS/package.opf");
        zipItem.setContent(xml.getBytes(StandardCharsets.UTF_8));
        return zipItem;
    }

    private static void createRoot(Document doc, Novel novel, List<EpubItem> items, String uuid) {
        final Namespace namespace = Namespace.get("", "http://www.idpf.org/2007/opf");
        final Element root = doc.addElement(QName.get("package", namespace));
        root.addAttribute("version", "2.0");
        root.addAttribute("unique-identifier", "book-id");

        createMetadata(root, novel, uuid);
        createManifest(root, novel, items);
        createSpine(root, novel, items);
    }

    private static void createMetadata(Element root, Novel novel, String uuid) {
        final Namespace namespace = Namespace.get("dc", "http://purl.org/dc/elements/1.1/");

        final Element metadata = root.addElement("metadata");
        metadata.addNamespace("dc", "http://purl.org/dc/elements/1.1/");
        metadata.addNamespace("opf", "http://www.idpf.org/2007/opf");


        metadata.addElement(QName.get("title", namespace)).setText(StrUtil.nullToEmpty(novel.getName()));
        metadata.addElement(QName.get("creator", namespace)).addAttribute(QName.get("role", Namespace.get("opf", "http://www.idpf.org/2007/opf")), "aut").setText(StrUtil.isEmpty(novel.getAuthor()) ? "佚名" : novel.getAuthor());
        metadata.addElement(QName.get("identifier", namespace)).addAttribute("id", "book-id").setText("urn:uuid:" + uuid);
        metadata.addElement(QName.get("language", namespace)).setText(StrUtil.isEmpty(novel.getLanguage()) ? "zh-CN" : novel.getLanguage());
        metadata.addElement(QName.get("date", namespace)).setText(LocalDate.now().toString());

        if (StrUtil.notEmpty(novel.getPublisher())) metadata.addElement(QName.get("publisher", namespace)).setText(novel.getPublisher());
        if (StrUtil.notEmpty(novel.getDescription())) metadata.addElement(QName.get("description", namespace)).addCDATA(novel.getDescription());
        if (StrUtil.notEmpty(novel.getType())) metadata.addElement(QName.get("type", namespace)).setText(novel.getType());
        if (StrUtil.notEmpty(novel.getType())) metadata.addElement(QName.get("subject", namespace)).setText(novel.getType());

        if (novel.getCoverImage() != null && novel.getCoverImageType() != null) {
            // 关键：标识封面（对应manifest里的cover项）
            metadata.addElement("meta").addAttribute("name", "cover").addAttribute("content", "cover-image");
        }
    }

    private static void createManifest(Element root, Novel novel, List<EpubItem> epubItems) {
        final Element manifest = root.addElement("manifest");

        if (novel.getCoverImage() != null && novel.getCoverImageType() != null) {
            // 封面图片（可选，若未加封面可删除
            manifest.addElement("item").addAttribute("id", "cover-image").addAttribute("href", "images/cover." + novel.getCoverImageType().name()).addAttribute("media-type", novel.getCoverImageType().getMediaType());
        }

        // 详情页
        manifest.addElement("item").addAttribute("id", "detail").addAttribute("href", "detail.xhtml").addAttribute("media-type", "application/xhtml+xml");
        // ncx 文件
        manifest.addElement("item").addAttribute("id", "ncx").addAttribute("href", "toc.ncx").addAttribute("media-type", "application/x-dtbncx+xml");

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
        spine.addAttribute("toc", "ncx");

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
