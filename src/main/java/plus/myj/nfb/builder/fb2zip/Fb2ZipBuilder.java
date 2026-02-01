package plus.myj.nfb.builder.fb2zip;

import plus.myj.nfb.builder.Builder;
import plus.myj.nfb.builder.fb2.Fb2Builder;
import plus.myj.nfb.entity.Novel;
import plus.myj.nfb.util.ZipUtil;

import java.util.Collections;
import java.util.List;

public enum Fb2ZipBuilder implements Builder {
    builder;

    @Override
    public byte[] build(Novel novel) throws Exception {
        final byte[] bytes = Fb2Builder.builder.build(novel);
        final List<ZipUtil.ZipItem> zipItems = createZipItems(bytes);
        return ZipUtil.zip(zipItems);
    }

    private List<ZipUtil.ZipItem> createZipItems(final byte[] bytes) {
        final ZipUtil.ZipItem zipItem = new ZipUtil.ZipItem();
        zipItem.setPath("book.fb2");
        zipItem.setContent(bytes);
        return Collections.singletonList(zipItem);
    }
}
