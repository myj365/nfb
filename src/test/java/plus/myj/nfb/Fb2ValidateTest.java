package plus.myj.nfb;

import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;

public class Fb2ValidateTest {
    public static void main(String[] args) {
        final String xsdPath = "src/test/resources/fb2xsd/FictionBook.xsd";
        final String xmlPath = "src/test/resources/result/book_FB2.fb2";

        System.out.println(validateXmlByXsd(xsdPath, xmlPath));
    }

    /**
     * 验证 xml 文件是否符合XSD文件的约束
     * @param xmlPath XML 文件路径
     * @param xsdPath XSD 文件路径
     * @return true=验证通过，false=验证失败
     */
    public static boolean validateXmlByXsd(String xsdPath, String xmlPath) {
        // 1. 创建SchemaFactory（指定XSD解析器）
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

        // 2. 加载XSD文件，生成Schema对象
        Schema schema;
        try {
            schema = schemaFactory.newSchema(new File(xsdPath));
        } catch (SAXException e) {
            System.out.println("加载XSD文件失败：" + e.getMessage());
            return false;
        }

        // 3. 创建Validator（验证器）
        Validator validator = schema.newValidator();

        try {
            // 读取xml文件为 JAXP的Source
            Source xmlSource = new StreamSource(new File(xmlPath));

            // 5. 执行验证（无异常则验证通过）
            validator.validate(xmlSource);
            System.out.println("XML文件验证通过！");
            return true;
        } catch (SAXException e) {
            System.out.println("XML文件违反XSD约束：" + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO异常：" + e.getMessage());
        }
        return false;
    }
}
