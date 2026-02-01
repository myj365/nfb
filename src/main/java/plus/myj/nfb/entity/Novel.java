package plus.myj.nfb.entity;

import java.util.Arrays;
import java.util.List;

public class Novel {
    /** 小说名 */
    private String name;
    /** 作者 */
    private String author;
    /**
     * 分类<br>
     * <p>fb2格式的该值为枚举值，参见{@link plus.myj.nfb.builder.fb2.Fb2GenreEnum}, 为空或非枚举值时填{@link plus.myj.nfb.builder.fb2.Fb2GenreEnum#foreign_other}，即外国其他</p>
     **/
    private String type;
    /** 电子书的内容介绍 */
    private String description;
    /** 封面图片 */
    private byte[] coverImage;
    /** 封面图片类型 */
    private ImageType coverImageType;
    /** 出版社 */
    private String publisher;
    /** 语言 */
    private String language;
    /** 章节列表 */
    private List<Chapter> chapters;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public byte[] getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(byte[] coverImage) {
        this.coverImage = coverImage;
    }

    public ImageType getCoverImageType() {
        return coverImageType;
    }

    public void setCoverImageType(ImageType coverImageType) {
        this.coverImageType = coverImageType;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public List<Chapter> getChapters() {
        return chapters;
    }

    public void setChapters(List<Chapter> chapters) {
        this.chapters = chapters;
    }

    @Override
    public String toString() {
        return "Novel{" +
                "name='" + name + '\'' +
                ", author='" + author + '\'' +
                ", type='" + type + '\'' +
                ", description='" + description + '\'' +
                ", coverImage=" + Arrays.toString(coverImage) +
                ", coverImageType=" + coverImageType +
                ", publisher='" + publisher + '\'' +
                ", language='" + language + '\'' +
                ", chapters=" + chapters +
                '}';
    }
}
