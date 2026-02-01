package plus.myj.nfb.builder.epub3;

import org.dom4j.*;
import plus.myj.nfb.builder.entity.EpubItem;
import plus.myj.nfb.util.StrUtil;
import plus.myj.nfb.util.ZipUtil;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class TocHandler {
    public static ZipUtil.ZipItem createToc(List<EpubItem> epubItems) {
        final Document doc = DocumentHelper.createDocument();

        createRoot(doc, epubItems);
        final String xml = StrUtil.xmlPretty(doc);

        final ZipUtil.ZipItem zipItem = new ZipUtil.ZipItem();
        zipItem.setPath("OEBPS/toc.xhtml");
        zipItem.setContent(xml.getBytes(StandardCharsets.UTF_8));
        return zipItem;
    }

    private static void createRoot(Document doc, List<EpubItem> epubItems) {
        final Namespace namespace = Namespace.get("", "http://www.w3.org/1999/xhtml");
        final Element root = doc.addElement(QName.get("html", namespace));
        root.addNamespace("epub", "http://www.idpf.org/2007/ops");

        createHead(root);
        createBody(root, epubItems);
    }

    private static void createHead(Element root) {
        final Element head = root.addElement("head");
        head.addElement("meta").addAttribute("charset", "UTF-8");
        head.addElement("title").setText("目录");
    }

    private static void createBody(Element root, List<EpubItem> epubItems) {
        final Element body = root.addElement("body");
        final Element nav = body.addElement("nav");

        final Namespace namespace = Namespace.get("epub", "http://www.idpf.org/2007/ops");
        nav.addAttribute(QName.get("type", namespace), "toc");
        nav.addAttribute("id", "toc");

        nav.addElement("h2").setText("目录");

        if (epubItems != null && !epubItems.isEmpty()) {
            final Element ol = nav.addElement("ol");
            for (EpubItem epubItem : epubItems) {
                createCatalog(ol, epubItem);
            }
        }
    }

    private static void createCatalog(Element ol, EpubItem epubItem) {
        final Element li = ol.addElement("li");

        li.addElement("a").addAttribute("href", epubItem.getHref()).setText(StrUtil.nullToEmpty(epubItem.getTitle()));
        if (epubItem.getSubEpubItems() != null && !epubItem.getSubEpubItems().isEmpty()) {
            final Element element = li.addElement("ol");
            for (EpubItem subEpubItem : epubItem.getSubEpubItems()) {
                createCatalog(element, subEpubItem);
            }
        }
    }
}
