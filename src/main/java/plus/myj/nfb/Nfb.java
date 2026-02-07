package plus.myj.nfb;

import plus.myj.nfb.builder.Builder;
import plus.myj.nfb.builder.epub2.Epub2Builder;
import plus.myj.nfb.builder.epub3.Epub3Builder;
import plus.myj.nfb.builder.fb2.Fb2Builder;
import plus.myj.nfb.builder.fb2zip.Fb2ZipBuilder;
import plus.myj.nfb.builder.markdown.MarkDownBuilder;
import plus.myj.nfb.builder.txt.TxtBuilder;
import plus.myj.nfb.entity.Chapter;
import plus.myj.nfb.entity.FileFormat;
import plus.myj.nfb.entity.Novel;
import plus.myj.nfb.util.StrUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Nfb {
    private static final Map<FileFormat, Builder> fileFormat2BuilderMap = new HashMap<>();
    static {
        fileFormat2BuilderMap.put(FileFormat.EPUB2, Epub2Builder.builder);
        fileFormat2BuilderMap.put(FileFormat.EPUB3, Epub3Builder.builder);
        fileFormat2BuilderMap.put(FileFormat.FB2, Fb2Builder.builder);
        fileFormat2BuilderMap.put(FileFormat.FB2ZIP, Fb2ZipBuilder.builder);
        fileFormat2BuilderMap.put(FileFormat.TXT, TxtBuilder.builder);
        fileFormat2BuilderMap.put(FileFormat.MarkDown, MarkDownBuilder.builder);
    }

    public static byte[] build(Novel novel, FileFormat format) throws Exception {
        checkNovel(novel);
        checkDepth(novel.getChapters(), 1);
        if (format == null) {
            throw new RuntimeException("文件格式不得为空");
        }

        Builder builder = fileFormat2BuilderMap.get(format);
        if (builder == null) {
            throw new RuntimeException("该格式暂未支持：" + format.name());
        } else {
            return builder.build(novel);
        }
    }

    private static void checkNovel(Novel novel) {
        if (novel == null) {
            throw new RuntimeException("Novel 对象不得为空");
        }
        if (StrUtil.isEmpty(novel.getName())) {
            throw new RuntimeException("小说名称不得为空");
        }
        if (novel.getChapters() == null || novel.getChapters().isEmpty()) {
            throw new RuntimeException("小说章节列表不得为空");
        }
    }

    private static void checkDepth(List<Chapter> chapters, int depth) {
        if (chapters != null) {
            if (depth > 5) {
                throw new RuntimeException("章节深度不得超过5层，当前检测到深度" + depth);
            }

            for (Chapter chapter : chapters) {
                if (chapter == null) {
                    throw new RuntimeException("章节列表中不得存在空对象");
                }

                if (chapter.getSubChapters() == null || !chapter.getSubChapters().isEmpty()) {
                    checkDepth(chapter.getSubChapters(), depth + 1);
                }
            }
        }
    }
}
