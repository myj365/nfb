package plus.myj.nfb.builder.fb2;

import org.dom4j.*;
import plus.myj.nfb.builder.Builder;
import plus.myj.nfb.entity.Chapter;
import plus.myj.nfb.entity.Novel;
import plus.myj.nfb.util.StrUtil;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public enum Fb2Builder implements Builder {
    builder;

    @Override
    public byte[] build(Novel novel) {
        Document doc = DocumentHelper.createDocument();
        createFictionBook(doc, novel);

        String xml = StrUtil.xmlPretty(doc);
        return xml.getBytes(StandardCharsets.UTF_8);
    }

    private void createFictionBook(Document doc, Novel novel) {
        final Namespace xlink = Namespace.get("xlink", "http://www.w3.org/1999/xlink");
        final Element root = doc.addElement(QName.get("FictionBook", "", "http://www.gribuser.ru/xml/fictionbook/2.0"));
        root.addNamespace(xlink.getPrefix(), xlink.getURI());

        createFb2Description(root, novel, xlink);
        createFb2Body(root, novel.getChapters());
        createFb2BinaryList(root, novel);
    }

    private void createFb2Description(Element root, Novel novel, Namespace xlink) {
        Element description = root.addElement("description");

        createTitleInfo(description, novel, xlink);
        createDocumentInfo(description);
        createPublishInfo(description, novel);
    }
    private void createTitleInfo(Element element, Novel novel, Namespace xlink) {
        Element titleInfo = element.addElement("title-info");

        titleInfo.addElement("genre").setText(StrUtil.nullToEmpty(getType(novel.getType())));
        titleInfo.addElement("author").addElement("nickname").setText(StrUtil.nullToEmpty(novel.getAuthor())); // 这里之所以使用nickname，而不使用first-name和last-name，是因为这里作者只有一个字段
        titleInfo.addElement("book-title").setText(StrUtil.nullToEmpty(novel.getName()));
        titleInfo.addElement("annotation").addElement("p").setText(StrUtil.nullToEmpty(novel.getDescription()));
        titleInfo.addElement("date").setText(LocalDate.now().format(DateTimeFormatter.ISO_DATE));
        if (novel.getCoverImage() != null && novel.getCoverImageType() != null) {
            titleInfo.addElement("coverpage").addElement("image").addAttribute(QName.get("href", xlink), "#cover");
        }
        titleInfo.addElement("lang").setText(StrUtil.nullToEmpty(novel.getLanguage()));
    }
    private static final Set<String> fb2GenreEnumSet = Arrays.stream(Fb2GenreEnum.values()).map(Enum::name).collect(Collectors.toSet());
    private String getType(String type) {
        if (fb2GenreEnumSet.contains(type)) {
            return type;
        } else {
            return Fb2GenreEnum.foreign_other.name();
        }
    }

    private void createDocumentInfo(Element element) {
        final Element documentInfo = element.addElement("document-info");
        documentInfo.addElement("author").addElement("nickname").setText("nfb");
        documentInfo.addElement("date").setText(LocalDate.now().format(DateTimeFormatter.ISO_DATE));
        documentInfo.addElement("id").setText(UUID.randomUUID().toString());
        documentInfo.addElement("version").setText("1");
    }

    private void createPublishInfo(Element element, Novel novel) {
        final Element publishInfo = element.addElement("publish-info");
        publishInfo.addElement("publisher").setText(StrUtil.nullToEmpty(novel.getPublisher()));
    }


    private void createFb2Body(Element root, List<Chapter> chapters) {
        final Element body = root.addElement("body");

        for (Chapter chapter : chapters) {
            createSection(body, chapter);
        }
    }
    private void createSection(Element element, Chapter chapter) {
        final Element section = element.addElement("section");
        section.addElement("title").addElement("p").setText(StrUtil.nullToEmpty(chapter.getTitle()));

        if (chapter.getContents() != null && chapter.getSubChapters() != null) {
            throw new RuntimeException("章节内容与子章节不能同时存在：" + chapter.getTitle());
        }

        if (chapter.getContents() != null) {
            for (String content : chapter.getContents()) {
                section.addElement("p").setText(StrUtil.nullToEmpty(content));
            }
        } else if (chapter.getSubChapters() != null && !chapter.getSubChapters().isEmpty()) {
            createSubSection(section, chapter.getSubChapters());
        }
    }
    private void createSubSection(Element element, List<Chapter> subChapters) {
        for (Chapter chapter : subChapters) {
            if (chapter.getContents() != null && chapter.getSubChapters() != null) {
                throw new RuntimeException("章节内容与子章节不能同时存在：" + chapter.getTitle());
            }

            final Element section = element.addElement("section");
            section.addElement("title").addElement("p").setText(StrUtil.nullToEmpty(chapter.getTitle()));

            if (chapter.getContents() != null) {
                for (String content : chapter.getContents()) {
                    section.addElement("p").setText(StrUtil.nullToEmpty(content));
                }
            }

            if (chapter.getSubChapters() != null && !chapter.getSubChapters().isEmpty()) {
                createSubSection(section, chapter.getSubChapters());
            }
        }
    }


    private void createFb2BinaryList(Element root, Novel novel) {
        if (novel.getCoverImage() != null && novel.getCoverImageType() != null) {
            final Base64.Encoder encoder = Base64.getEncoder();

            final String coverImageBase64 = new String(encoder.encode(novel.getCoverImage()), StandardCharsets.UTF_8);
            root.addElement("binary")
                    .addAttribute("id", "cover")
                    .addAttribute("content-type", novel.getCoverImageType().getMediaType())
                    .setText(coverImageBase64);
        }
    }
}
