package plus.myj.nfb.util;

import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Objects;

public class StrUtil {
    public static boolean isEmpty(String s) {
        return s == null || s.isEmpty();
    }

    public static boolean notEmpty(String s) {
        return !isEmpty(s);
    }

    public static String xmlPretty(Document document) {
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setIndentSize(4);
        format.setEncoding(document.getXMLEncoding());
        try {
            StringWriter out = new StringWriter();
            XMLWriter writer = new XMLWriter(out, format);
            writer.write(document);
            writer.flush();
            return out.toString();
        } catch (IOException e) {
            return document.asXML();
        }
    }

    public static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }

    public static String startSubstringBySymbol(String s, String symbol) {
        if (Objects.isNull(s) || s.isEmpty() || Objects.isNull(symbol) || symbol.isEmpty()) {
            return s;
        }

        if (!s.contains(symbol)) {
            return s;
        }

        final int index = s.indexOf(symbol);
        return s.substring(0, index);
    }

    /**
     * 如果该字符串不存在指定后缀，则添加
     * @param str 该字符串
     * @param suffix 后缀
     * @return 拥有指定后缀的字符串
     */
    public static String addSuffixIfNot(String str, String suffix) {
        Objects.requireNonNull(str);
        Objects.requireNonNull(suffix);

        if (str.endsWith(suffix)) {
            return str;
        } else {
            return str + suffix;
        }
    }

    /**
     * 如果该字符串不存在指定前缀，则添加
     * @param str 该字符串
     * @param prefix 前缀
     * @return 拥有指定前缀的字符串
     */
    public static String addPrefixIfNot(String str, String prefix) {
        Objects.requireNonNull(str);
        Objects.requireNonNull(prefix);

        if (str.startsWith(prefix)) {
            return str;
        } else {
            return prefix + str;
        }
    }

    public static String getFormatNumber(int i) {
        if (i < 10) return "0000" + i;
        if (i < 100) return "000" + i;
        if (i < 1000) return "00" + i;
        if (i < 10000) return "0" + i;
        if (i < 100000) return "" + i;
        return String.valueOf(i);
    }
}
