package plus.myj.nfb.entity;

public enum FileFormat {
    EPUB2("epub"),
    EPUB3("epub"),
    FB2("fb2"),
    /** 该格式即经过zip压缩的fb2文件，可有效降低文件大小，但支持的阅读器有限 */
    FB2ZIP("fb2.zip"),
    TXT("txt"),
    MarkDown("md"),
    ;

    /**
     * 文件后缀名
     */
    private final String suffix;

    FileFormat(String suffix) {
        this.suffix = suffix;
    }

    public String getSuffix() {
        return suffix;
    }
}
