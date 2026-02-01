package plus.myj.nfb.builder.epub3;

import org.dom4j.*;
import plus.myj.nfb.builder.entity.DepthLevel;
import plus.myj.nfb.builder.entity.EpubItem;
import plus.myj.nfb.util.StrUtil;
import plus.myj.nfb.util.ZipUtil;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ContentHandler {
    public static List<ZipUtil.ZipItem> createContent(List<EpubItem> epubItems) {
        final List<ZipUtil.ZipItem> zipItems = new ArrayList<>();

        createXhtmlList(zipItems, epubItems);

        return zipItems;
    }

    private static void createXhtmlList(final List<ZipUtil.ZipItem> zipItems, List<EpubItem> epubItems) {
        for (EpubItem epubItem : epubItems) {
            final ZipUtil.ZipItem zipItem = createXhtml(epubItem);
            zipItems.add(zipItem);

            if (epubItem.getSubEpubItems() != null && !epubItem.getSubEpubItems().isEmpty()) {
                for (EpubItem subEpubItem : epubItem.getSubEpubItems()) {
                    zipItems.add(createXhtml(subEpubItem));
                }
            }
        }
    }

    private static ZipUtil.ZipItem createXhtml(EpubItem epubItem) {
        final Document doc = DocumentHelper.createDocument();

        createRoot(doc, epubItem);

        final String xml = StrUtil.xmlPretty(doc);

        final ZipUtil.ZipItem zipItem = new ZipUtil.ZipItem();
        zipItem.setPath("OEBPS/" + epubItem.getHref());
        zipItem.setContent(xml.getBytes(StandardCharsets.UTF_8));
        return zipItem;
    }

    private static void createRoot(Document doc, EpubItem epubItem) {
        final Namespace namespace = Namespace.get("", "http://www.w3.org/1999/xhtml");
        final Element root = doc.addElement(QName.get("html", namespace));
        root.addNamespace("epub", "http://www.idpf.org/2007/ops");

        createHead(root, epubItem);
        createBody(root, epubItem);
    }

    private static void createHead(Element root, EpubItem epubItem) {
        final Element head = root.addElement("head");

        head.addElement("meta").addAttribute("charset", "UTF-8");
        head.addElement("title").setText(StrUtil.nullToEmpty(epubItem.getTitle()));
    }

    private static void createBody(Element root, EpubItem epubItem) {
        final Namespace namespace = Namespace.get("epub", "http://www.idpf.org/2007/ops");

        final Element body = root.addElement("body");
        final Element section = body.addElement("section");
        section.addAttribute(QName.get("type", namespace), "chapter");
        section.addAttribute("id", epubItem.getId());
        section.addElement("h1").setText(StrUtil.nullToEmpty(epubItem.getTitle()));

        final List<String> contents = epubItem.getContents();
        if (contents != null && !contents.isEmpty()) {
            for (String content : contents) {
                section.addElement("p").setText(StrUtil.nullToEmpty(content));
            }
        }

        if (epubItem.getDepthLevel() == DepthLevel.chapter && epubItem.getSubEpubItems() != null && !epubItem.getSubEpubItems().isEmpty()) {
            createSubEpubItems(namespace, section, epubItem.getSubEpubItems());
        }
    }

    private static void createSubEpubItems(final Namespace namespace, Element element, List<EpubItem> subItems) {
        for (EpubItem subItem : subItems) {
            if (subItem.getDepthLevel() == null) {
                return;
            }

            final Element section = element.addElement("section");
            section.addAttribute(QName.get("type", namespace), subItem.getDepthLevel().name());
            section.addAttribute("id", subItem.getId());
            section.addElement("h" + (subItem.getDepthLevel().getLevel() - 1)).setText(StrUtil.nullToEmpty(subItem.getTitle()));

            final List<String> contents = subItem.getContents();
            if (contents != null && !contents.isEmpty()) {
                for (String content : contents) {
                    section.addElement("p").setText(StrUtil.nullToEmpty(content));
                }
            }

            if (subItem.getSubEpubItems() != null &&  !subItem.getSubEpubItems().isEmpty()) {
                createSubEpubItems(namespace, section, subItem.getSubEpubItems());
            }
        }
    }
}
