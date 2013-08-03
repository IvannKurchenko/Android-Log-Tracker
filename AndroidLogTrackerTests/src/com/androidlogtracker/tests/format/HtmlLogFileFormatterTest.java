package com.androidlogtracker.tests.format;

import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;
import com.androidlogtracker.tests.util.TestDocument;
import com.androidlogtracker.usage.TestActivity;
import com.logtracking.lib.internal.format.HtmlLogFileFormatter;
import com.logtracking.lib.internal.format.LogFileFormatter;
import junit.framework.Assert;
import org.w3c.dom.Document;
import org.w3c.tidy.Tidy;
import org.xml.sax.InputSource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;

public class HtmlLogFileFormatterTest extends ActivityInstrumentationTestCase2 implements FormatTestSuites{

    private static class OriginalHtmlFormatterTest extends XmlLogFileFormatterTest {

        private Context mContext;

        OriginalHtmlFormatterTest(Context context){
            mContext = context;
        }

        @Override
        protected LogFileFormatter setUpFormatter(){
            return new HtmlLogFileFormatter(mContext);
        }

        @Override
        public void testMessageInfoShouldBeEqual() {
            //TODO : not implemented yet!
        }

        @Override
        public void testLogRecordInfoShouldBeEqual() {
            //TODO : not implemented yet!
        }

        @Override
        public void testMetaDataInfoShouldBeEqual() {
            //TODO : not implemented yet!
        }

        @Override
        protected Document tryParse(TestDocument testDocument)  {
            InputStream is = new ByteArrayInputStream(testDocument.mFormattedDocument.getBytes());
            Tidy tidy = new Tidy();
            Document parsedDocument= tidy.parseDOM(is, null);
            Assert.assertNotNull("Parse HTML failed", parsedDocument);
            return parsedDocument;
        }
    }

    private AbstractFormatterTest mOriginalTest;

    public HtmlLogFileFormatterTest() {
        super(TestActivity.class);
    }


    @Override
    public void setUp() throws Exception {
        mOriginalTest = new OriginalHtmlFormatterTest(getActivity());
        mOriginalTest.setUp();
    }

    @Override
    public void tearDown() throws Exception {
        mOriginalTest.tearDown();
    }

    @Override
    public void testRecordFormattedStringShouldBeNotEmpty() {
        mOriginalTest.testRecordFormattedStringShouldBeNotEmpty();
    }

    @Override
    public void testMetaDataFormattedStringShouldBeNotEmpty() {
        mOriginalTest.testMetaDataFormattedStringShouldBeNotEmpty();
    }

    @Override
    public void testMessageFormattedStringShouldBeNotEmpty() {
        mOriginalTest.testMessageFormattedStringShouldBeNotEmpty();
    }

    @Override
    public void testFormattedRecordShouldBeParsed() {
        mOriginalTest.testFormattedRecordShouldBeParsed();
    }

    @Override
    public void testFormattedMetaDataShouldBeParsed() {
        mOriginalTest.testFormattedMetaDataShouldBeParsed();
    }

    @Override
    public void testFullDocumentShouldBeParsed() {
        mOriginalTest.testFullDocumentShouldBeParsed();
    }

    @Override
    public void testMessageInfoShouldBeEqual() {
        mOriginalTest.testMessageInfoShouldBeEqual();
    }

    @Override
    public void testLogRecordInfoShouldBeEqual() {
        mOriginalTest.testLogRecordInfoShouldBeEqual();
    }

    @Override
    public void testMetaDataInfoShouldBeEqual() {
        mOriginalTest.testMetaDataInfoShouldBeEqual();
    }

    @Override
    public void testControlSymbolsShouldBeRemoved() {
        mOriginalTest.testControlSymbolsShouldBeRemoved();
    }
}
