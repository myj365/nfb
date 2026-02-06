package plus.myj.nfb;

import plus.myj.nfb.entity.Chapter;
import plus.myj.nfb.entity.FileFormat;
import plus.myj.nfb.entity.ImageType;
import plus.myj.nfb.entity.Novel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class BuildTest {
    private static final int chapterMaxDepth = 5;

    public static void main(String[] args) throws Exception {
        Novel novel = createNovel();
        final List<FileFormat> formats = List.of(FileFormat.values());

        final String name = novel.getName();
        for (FileFormat format : formats) {
            novel.setName(name + "-" + format.name());
            byte[] bytes = Nfb.build(novel, format);
            Files.write(Path.of("src/test/resources/result", "book_" + format.name() + "." + format.getSuffix()), bytes);
        }
    }

    private static Novel createNovel() throws IOException {
        Novel novel = new Novel();
        novel.setName("nfb 示例");
        novel.setAuthor("nfb");
        novel.setType("玄幻");
        novel.setDescription("nfb 示例的简介 啊欸如果报i二哥i哦啊二");
        novel.setPublisher("nfb 出版社");
        novel.setLanguage("zh");
        novel.setCoverImage(Files.readAllBytes(Path.of("C:\\Users\\myj\\Downloads", "30319.jpeg")));
        novel.setCoverImageType(ImageType.jpeg);

        final AtomicInteger atomic = new AtomicInteger();
        final List<Chapter> chapters = createChapterList(atomic, tabs, 1);
        novel.setChapters(chapters);

        System.out.println("章数 = " + atomic.get());

        return novel;
    }

    public interface Tab {
        static Content content() {
            return Content.obj;
        }
        static Sub sub(Tab... tabs) {
            return new Sub(List.of(tabs));
        }
    }
    public static final class Content implements Tab {
        private static final Content obj = new Content();
        private Content() {}
    }
    public static final class Sub implements Tab {
        private final List<Tab> tabs;
        private Sub(List<Tab> tabs) {
            this.tabs = tabs;
        }
        public List<Tab> getTabs() {
            return tabs;
        }
    }

    private static final List<String> titles = List.of("卷名", "章名", "节名", "小节名", "次小节名");

    private static List<Chapter> createChapterList(AtomicInteger atomic, List<Tab> tabs, int depth) {
        if (depth > chapterMaxDepth) {
            throw new RuntimeException("当前深度(" + depth + ")已大于指定最大深度(" + chapterMaxDepth + ")");
        }

        List<Chapter> chapters = new ArrayList<>();

        for (Tab tab : tabs) {
            Chapter chapter = new Chapter();
            chapter.setTitle("第" + atomic.incrementAndGet() + "章 " + titles.get(depth - 1));

            if (tab instanceof Content) {
                chapter.setContents(STRINGS);
            } else if (tab instanceof Sub sub) {
                final List<Chapter> subChapterList = createChapterList(atomic, sub.getTabs(), depth + 1);
                chapter.setSubChapters(subChapterList);
            }
            chapters.add(chapter);
        }

        return chapters;
    }

    private static final List<String> STRINGS = List.of(
            "清晨的山林裹着薄薄的雾纱，阳光透过枝叶洒下斑驳金斑，溪流潺潺伴着清脆鸟鸣，草木间浮动着清新的草木香气。",
            "湖畔的黄昏温柔得像一幅油画，粉白荷花亭亭玉立，晚风拂过荷叶掀起绿浪，归鸟翅膀驮着细碎霞光掠过水面。",
            "晚霞把天边染成橘红色，流云似燃烧的锦缎，远处村落升起袅袅炊烟，田野里的稻穗闪着沉甸甸的金光。",
            "晨雾漫过青石板路，路边野花缀满晶莹露珠，远山在雾气中若隐若现，几声清脆蛙鸣轻轻打破周遭的宁静。",
            "无垠麦田在风里翻涌金色波浪，蝉鸣伴着麦秆沙沙作响，湛蓝天空飘着几朵悠闲白云，空气里满是麦香的清甜。",
            "海岸落日将浪花染成琥珀色，细软沙滩留着浅浅脚印，归航渔船披着余晖驶入港湾，海风裹挟着咸湿的清新气息。"
    );

    /**
     * 用于指定章节布局<br>
     * content表示章节内容<br>
     * sub表示子章节
     */
    private static final List<Tab> tabs = List.of(
            Tab.content(),
            Tab.sub(
                    Tab.content(),
                    Tab.sub(
                            Tab.content(),
                            Tab.sub(
                                    Tab.content(),
                                    Tab.sub(
                                            Tab.content(),
                                            Tab.content(),
                                            Tab.content()
                                    ),
                                    Tab.content()
                            ),
                            Tab.content(),
                            Tab.content()
                    ),
                    Tab.content(),
                    Tab.sub(
                            Tab.content(),
                            Tab.sub(
                                    Tab.content(),
                                    Tab.content(),
                                    Tab.content()
                            ),
                            Tab.content()
                    ),
                    Tab.content()
            ),
            Tab.content(),
            Tab.sub(
                    Tab.content(),
                    Tab.content(),
                    Tab.content(),
                    Tab.content()
            ),
            Tab.content()
    );
}
