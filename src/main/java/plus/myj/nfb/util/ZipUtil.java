package plus.myj.nfb.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipUtil {

    public static byte[] zip(List<ZipItem> zipItems) throws IOException {
        Objects.requireNonNull(zipItems);

        if (!zipItems.isEmpty()) {
            zipItems = new ArrayList<>(zipItems);
            zipItems.sort((o1, o2) -> {
                if ("mimetype".equals(o1.getPath())) {
                    return -1;
                } else if ("mimetype".equals(o2.getPath())) {
                    return 1;
                } else {
                    return o1.getPath().compareTo(o2.getPath());
                }
            });

            try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
                try (ZipOutputStream zip = new ZipOutputStream(output, StandardCharsets.UTF_8)) {
                    for (ZipItem zipItem : zipItems) {
                        if (zipItem != null) {
                            ZipEntry zipEntry = new ZipEntry(zipItem.getPath());

                            if ("mimetype".equals(zipItem.getPath())) {
                                zip.setLevel(0);
                            } else {
                                zip.setLevel(6);
                            }
                            zip.putNextEntry(zipEntry);
                            zip.write(zipItem.getContent());
                            zip.closeEntry();
                        }
                    }
                }

                return output.toByteArray();
            }
        }

        return new byte[0];
    }

    public static List<ZipItem> unzip(byte[] bytes, Charset charset) throws IOException {
        Objects.requireNonNull(bytes);
        Objects.requireNonNull(charset);

        List<ZipItem> zipItems = new ArrayList<>();

        try (ByteArrayInputStream input = new ByteArrayInputStream(bytes)) {
            try (ZipInputStream zip = new ZipInputStream(input, charset)) {
                ZipEntry zipEntry;

                while ((zipEntry = zip.getNextEntry()) != null) {
                    ZipItem zipItem = new ZipItem();

                    zipItem.setPath(zipEntry.getName());
                    if (!zipEntry.isDirectory()) {
                        zipItem.setContent(zip.readAllBytes());
                    }

                    zipItems.add(zipItem);
                }
            }
        }

        return zipItems;
    }

    public static final class ZipItem {
        private String path;
        private byte[] content;

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public byte[] getContent() {
            return content;
        }

        public void setContent(byte[] content) {
            this.content = content;
        }
    }
}
