package plus.myj.nfb.entity;

import java.util.List;

public class Chapter {
    /** 章节名 */
    private String title;
    /** 内容，多行，每行一个字符串 */
    private List<String> contents;
    /**
     * 子章节列表<br>
     **/
    private List<Chapter> subChapters;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getContents() {
        return contents;
    }

    public void setContents(List<String> contents) {
        this.contents = contents;
    }

    public List<Chapter> getSubChapters() {
        return subChapters;
    }

    public void setSubChapters(List<Chapter> subChapters) {
        this.subChapters = subChapters;
    }

    @Override
    public String toString() {
        return "Chapter{" +
                "title='" + title + '\'' +
                ", contents=" + contents +
                ", subChapters=" + subChapters +
                '}';
    }
}
