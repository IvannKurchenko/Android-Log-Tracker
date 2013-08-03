package  com.androidlogtracker.tests.format;

import com.androidlogtracker.tests.util.AssertUtils;
import com.androidlogtracker.tests.util.TestDocument;
import com.logtracking.lib.internal.LogModel;
import com.logtracking.lib.internal.format.LogFileFormatter;
import com.logtracking.lib.internal.format.XmlLogFileFormatter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.util.Map;

import static com.logtracking.lib.internal.format.Tags.*;

public class XmlLogFileFormatterTest extends AbstractFormatterTest {

    @Override
    protected LogFileFormatter setUpFormatter() {
        return new XmlLogFileFormatter();
    }

    @Override
    public void testFormattedRecordShouldBeParsed() {
        tryParse( formatLogRecords(1) );
    }

    @Override
    public void testFormattedMetaDataShouldBeParsed() {
        tryParse( formatMetaData(1) );
    }

    @Override
    public void testFullDocumentShouldBeParsed() {
        tryParse( formatDefaultDocument() );
    }

    @Override
    public void testMessageInfoShouldBeEqual() {
        TestDocument testDocument = formatMessage();
        Document xmlDocument = tryParse(testDocument);
        NodeList nodeList = xmlDocument.getElementsByTagName(REPORT_MESSAGE_KEY);
        Element messageNode = (Element) nodeList.item(0);
        assertElementAttributeEquals(messageNode,MESSAGE_KEY,testDocument.mMessage);
    }

    @Override
    public void testLogRecordInfoShouldBeEqual() {
        TestDocument testDocument = formatLogRecords(1);
        Document xmlDocument = tryParse(testDocument);
        NodeList nodes = xmlDocument.getElementsByTagName(RECORD_KEY);

        for (int i = 0; i < nodes.getLength(); i++) {

            Element element = (Element) nodes.item(i);
            LogModel record = testDocument.mRecords.get(i);

            assertElementAttributeEquals(element, DATE_KEY, record.getFormattedDate());
            assertElementAttributeEquals(element, LEVEL_KEY, record.getLevelSymbol().toString());
            assertElementAttributeEquals(element, PID_KEY, Integer.toString(record.getPid()));
            assertElementAttributeEquals(element, TID_KEY, Long.toString(record.getTid()));
            assertElementAttributeEquals(element, TAG_KEY, record.getTag());
            assertElementAttributeEquals(element, MESSAGE_KEY, record.getMessage());

        }
    }

    @Override
    public void testMetaDataInfoShouldBeEqual() {
        TestDocument testDocument = formatMetaData(1);
        Document xmlDocument = tryParse(testDocument);
        Map<String,String> meta =  testDocument.mMetaData;
        NodeList nodes = xmlDocument.getElementsByTagName(META_DATA_TAG);

        for (int i = 0; i < nodes.getLength(); i++) {
            Element element = (Element) nodes.item(i);
            assertElementAttributeEquals(element,meta);
        }

    }

    @Override
    public void testControlSymbolsShouldBeRemoved() {
        TestDocument testDocument = formatDocumentWithControlSymbols("/<>");
        tryParse( testDocument );
    }


    private void assertElementAttributeEquals(Element element, String key, String expectedValue){
        assertEquals("Wrong attribute in element" , element.getAttribute(key) , expectedValue);
    }

    private void assertElementAttributeEquals(Element element, Map<String,String> keyValues){
        if(element.getAttributes().getLength() == 0)
            return;

        String key = element.getAttributes().item(0).getNodeName();
        assertElementAttributeEquals(element, key, keyValues.get(key));
    }

    protected Document tryParse(TestDocument testDocument){
        Document doc = null;

        try {

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(testDocument.mFormattedDocument));
            doc = dBuilder.parse(is);

        } catch (ParserConfigurationException e) {
            AssertUtils.throwAssertError(e);
        } catch (SAXException e) {
            AssertUtils.throwAssertError(e);
        } catch (IOException e) {
            AssertUtils.throwAssertError(e);
        }

        return doc;
    }
}
