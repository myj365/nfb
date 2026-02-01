package plus.myj.nfb.builder.entity;

import java.util.List;

public class EpubItem {
    private DepthLevel depthLevel;
    private String order;
    private String id;
    private String href;
    private String title;
    private List<String> contents;
    private List<EpubItem> subEpubItems;

    public DepthLevel getDepthLevel() {
        return depthLevel;
    }

    public void setDepthLevel(DepthLevel depthLevel) {
        this.depthLevel = depthLevel;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

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

    public List<EpubItem> getSubEpubItems() {
        return subEpubItems;
    }

    public void setSubEpubItems(List<EpubItem> subEpubItems) {
        this.subEpubItems = subEpubItems;
    }

    @Override
    public String toString() {
        return "EpubItem{" +
                "depthLevel=" + depthLevel +
                ", order='" + order + '\'' +
                ", id='" + id + '\'' +
                ", href='" + href + '\'' +
                ", title='" + title + '\'' +
                ", contents=" + contents +
                ", subEpubItems=" + subEpubItems +
                '}';
    }
}