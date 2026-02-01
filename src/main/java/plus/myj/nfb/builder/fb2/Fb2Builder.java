package plus.myj.nfb.builder.fb2;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.QName;
import plus.myj.nfb.builder.Builder;
import plus.myj.nfb.entity.Chapter;
import plus.myj.nfb.entity.Novel;
import plus.myj.nfb.util.StrUtil;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
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
        Element root = doc.addElement(QName.get("FictionBook", "", "http://www.gribuser.ru/xml/fictionbook/2.0"));

        createFb2Description(root, novel);
        createFb2Body(root, novel.getChapters());
    }

    private void createFb2Description(Element root, Novel novel) {
        Element description = root.addElement("description");

        createTitleInfo(description, novel);
        createDocumentInfo(description, novel);
    }
    private void createTitleInfo(Element element, Novel novel) {
        Element titleInfo = element.addElement("title-info");

        titleInfo.addElement("genre").setText(StrUtil.nullToEmpty(getType(novel.getType())));
        titleInfo.addElement("author").addElement("nickname").setText(StrUtil.nullToEmpty(novel.getAuthor())); // 这里之所以使用nickname，而不使用first-name和last-name，是因为这里作者只有一个字段
        titleInfo.addElement("book-title").setText(StrUtil.nullToEmpty(novel.getName()));
        titleInfo.addElement("annotation").addElement("p").setText(StrUtil.nullToEmpty(novel.getDescription()));
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

    private void createDocumentInfo(Element element, Novel novel) {
        final Element documentInfo = element.addElement("document-info");
        documentInfo.addElement("author").addElement("nickname").setText("nfb");
        documentInfo.addElement("date").setText(LocalDate.now().format(DateTimeFormatter.ISO_DATE));
        documentInfo.addElement("id").setText(UUID.randomUUID().toString());
        documentInfo.addElement("version").setText("1");
        documentInfo.addElement("publisher").addElement("nickname").setText(StrUtil.nullToEmpty(novel.getPublisher()));
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
}
