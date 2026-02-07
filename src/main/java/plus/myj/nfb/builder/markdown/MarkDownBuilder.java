package plus.myj.nfb.builder.markdown;

import plus.myj.nfb.builder.Builder;
import plus.myj.nfb.entity.Chapter;
import plus.myj.nfb.entity.Novel;
import plus.myj.nfb.util.StrUtil;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.StringJoiner;

public enum MarkDownBuilder implements Builder {
    builder;

    @Override
    public byte[] build(Novel novel) throws Exception {
        final String text = createText(novel);
        return text.getBytes(StandardCharsets.UTF_8);
    }

    private static String createText(Novel novel) {
        final StringJoiner joiner = new StringJoiner("\n");

        createMetadata(joiner, novel);
        createChapters(joiner, novel.getChapters(), 1);

        return joiner.toString();
    }

    private static void createMetadata(StringJoiner joiner, Novel novel) {
        joiner.add("# " + novel.getName());
        joiner.add("");


        joiner.add("## 简介");
        if (novel.getCoverImage() != null && novel.getCoverImageType() != null) {
            final StringBuilder cover = new StringBuilder();
            cover.append("![封面图片]");
            cover.append("(");
            cover.append("data:").append(novel.getCoverImageType().getMediaType()).append(";");
            cover.append("base64,");
            cover.append(new String(Base64.getEncoder().encode(novel.getCoverImage()), StandardCharsets.UTF_8));
            cover.append(")");
            joiner.add(cover);
            joiner.add("");
        }
        if (StrUtil.notEmpty(novel.getAuthor())) joiner.add("作者：" + novel.getAuthor() + "  ");
        if (StrUtil.notEmpty(novel.getType())) joiner.add("分类：" + novel.getType() + "  ");
        if (StrUtil.notEmpty(novel.getPublisher())) joiner.add("出版社：" + novel.getPublisher() + "  ");
        if (StrUtil.notEmpty(novel.getLanguage())) joiner.add("语言：" + novel.getLanguage() + "  ");
        if (StrUtil.notEmpty(novel.getDescription())) joiner.add("简介：" + novel.getDescription() + "  ");
        joiner.add("\n");
    }

    private static void createChapters(StringJoiner joiner, List<Chapter> chapters, int depth) {
        for (Chapter chapter : chapters) {
            joiner.add("#".repeat(depth + 1) + " " + chapter.getTitle());

            if (chapter.getContents() != null && !chapter.getContents().isEmpty()) {
                chapter.getContents().forEach(s -> {
                    joiner.add(s);
                    joiner.add("");
                });
            }
            joiner.add("\n");
            if (chapter.getSubChapters() != null && !chapter.getSubChapters().isEmpty()) {
                createChapters(joiner, chapter.getSubChapters(), depth + 1);
            }
        }
    }
}
