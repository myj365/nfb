package plus.myj.nfb.builder.txt;

import plus.myj.nfb.builder.Builder;
import plus.myj.nfb.entity.Chapter;
import plus.myj.nfb.entity.Novel;
import plus.myj.nfb.util.StrUtil;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.StringJoiner;

public enum TxtBuilder implements Builder {
    builder;

    @Override
    public byte[] build(Novel novel) throws Exception {
        final String text = createText(novel);
        return text.getBytes(StandardCharsets.UTF_8);
    }

    private static String createText(Novel novel) {
        final StringJoiner joiner = new StringJoiner("\n");

        createMetadata(joiner, novel);
        createChapters(joiner, novel.getChapters());

        return joiner.toString();
    }

    private static void createMetadata(StringJoiner joiner, Novel novel) {
        joiner.add("【" + novel.getName() + "】");
        if (StrUtil.notEmpty(novel.getAuthor())) joiner.add("作者：" + novel.getAuthor());
        if (StrUtil.notEmpty(novel.getType())) joiner.add("分类：" + novel.getType());
        if (StrUtil.notEmpty(novel.getPublisher())) joiner.add("出版社：" + novel.getPublisher());
        if (StrUtil.notEmpty(novel.getLanguage())) joiner.add("语言：" + novel.getLanguage());
        if (StrUtil.notEmpty(novel.getDescription())) joiner.add("简介：" + novel.getDescription());
        joiner.add("\n\n\n");
    }

    private static void createChapters(StringJoiner joiner, List<Chapter> chapters) {
        for (Chapter chapter : chapters) {
            joiner.add(chapter.getTitle());

            if (chapter.getContents() != null) {
                chapter.getContents().forEach(joiner::add);
            }

            if (chapter.getSubChapters() != null) {
                createSubChapters(joiner, chapter.getSubChapters(), chapter.getTitle());
            }

            joiner.add("\n\n");
        }
    }

    private static void createSubChapters(StringJoiner joiner, List<Chapter> subChapters, String parentTitle) {
        for (Chapter subChapter : subChapters) {
            joiner.add(parentTitle + " " + subChapter.getTitle());

            if (subChapter.getContents() != null) {
                subChapter.getContents().forEach(joiner::add);
            }

            if (subChapter.getSubChapters() != null) {
                createSubChapters(joiner, subChapter.getSubChapters(), parentTitle + " " + subChapter.getTitle());
            }

            joiner.add("\n");
        }
    }

//    private static void createSubSubChapters(StringJoiner joiner, List<SubSubChapter> subSubChapters, String parentTitle) {
//        for (SubSubChapter subSubChapter : subSubChapters) {
//            joiner.add(parentTitle + " " + subSubChapter.getTitle());
//
//            if (subSubChapter.getContents() != null) {
//                subSubChapter.getContents().forEach(joiner::add);
//            }
//            joiner.add("");
//        }
//    }
}
