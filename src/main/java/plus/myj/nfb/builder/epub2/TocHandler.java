package plus.myj.nfb.builder.epub2;

import org.dom4j.*;
import plus.myj.nfb.builder.entity.EpubItem;
import plus.myj.nfb.entity.Novel;
import plus.myj.nfb.util.StrUtil;
import plus.myj.nfb.util.ZipUtil;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class TocHandler {
    public static ZipUtil.ZipItem createToc(Novel novel, List<EpubItem> epubItems, String uuid, int depth) {
        final Document doc = DocumentHelper.createDocument();

        createRoot(doc, novel, epubItems, uuid, depth);
        final String xml = StrUtil.xmlPretty(doc);

        final ZipUtil.ZipItem zipItem = new ZipUtil.ZipItem();
        zipItem.setPath("OEBPS/toc.ncx");
        zipItem.setContent(xml.getBytes(StandardCharsets.UTF_8));
        return zipItem;
    }

    private static void createRoot(Document doc, Novel novel, List<EpubItem> epubItems, String uuid, int depth) {
        final Namespace namespace = Namespace.get("", "http://www.daisy.org/z3986/2005/ncx/");
        final Element root = doc.addElement(QName.get("ncx", namespace));
        root.addAttribute("version", "2005-1");
        root.addAttribute(QName.get("lang", Namespace.get("xml", "http://www.w3.org/XML/1998/namespace")), "zh-CN");

        createHead(root, uuid, depth);
        createDocTitle(root, novel);
        createNavMap(root, epubItems);
    }

    private static void createHead(Element root, String uuid, int depth) {
        final Element head = root.addElement("head");
        head.addElement("meta").addAttribute("name", "dtb:uid").addAttribute("content", "urn:uuid:" + uuid);
        head.addElement("meta").addAttribute("name", "dtb:depth").addAttribute("content", String.valueOf(depth));
        head.addElement("meta").addAttribute("name", "dtb:totalPageCount").addAttribute("content", "0");
        head.addElement("meta").addAttribute("name", "dtb:maxPageNumber").addAttribute("content", "0");
    }

    private static void createDocTitle(Element root, Novel novel) {
        root.addElement("docTitle").addElement("text").setText(novel.getName());
    }

    private static void createNavMap(Element root, List<EpubItem> epubItems) {
        final Element navMap = root.addElement("navMap");

        if (epubItems != null && !epubItems.isEmpty()) {
            for (EpubItem epubItem : epubItems) {
                createCatalog(navMap, epubItem);
            }
        }
    }

    private static void createCatalog(Element element, EpubItem epubItem) {
        final Element navPoint = element.addElement("navPoint");

        navPoint.addAttribute("id", epubItem.getId()).addAttribute("playOrder", epubItem.getOrder());
        navPoint.addElement("navLabel").addElement("text").setText(StrUtil.nullToEmpty(epubItem.getTitle()));
        navPoint.addElement("content").addAttribute("src", epubItem.getHref());

        if (epubItem.getSubEpubItems() != null && !epubItem.getSubEpubItems().isEmpty()) {
            for (EpubItem subEpubItem : epubItem.getSubEpubItems()) {
                createCatalog(navPoint, subEpubItem);
            }
        }
    }
}
