package plus.myj.nfb.builder.entity;

import java.util.Objects;

public enum DepthLevel {
    volume(1),
    chapter(2),
    section(3),
    subsection(4),
    subsubsection(5),
    ;

    private final int level;

    DepthLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public static DepthLevel getNextDepthLevel(DepthLevel depthLevel) {
        Objects.requireNonNull(depthLevel);

        return switch (depthLevel) {
            case volume -> chapter;
            case chapter -> section;
            case section -> subsection;
            case subsection -> subsubsection;
            default -> null;
        };
    }
}
