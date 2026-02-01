package plus.myj.nfb.entity;

public enum ImageType {
    jpeg("image/jpeg"),
    jpg("image/jpeg"),
    png("image/png"),
    ;

    private final String mediaType;

    ImageType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getMediaType() {
        return mediaType;
    }
}
