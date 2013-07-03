package  com.androidlogtracker.tests.format;

import android.text.TextUtils;
import com.androidlogtracker.tests.util.TestDocument;
import com.logtracking.lib.api.MetaDataCollector;
import com.logtracking.lib.internal.LogModel;
import com.logtracking.lib.internal.format.LogFileFormatter;
import com.logtracking.lib.internal.format.NativeLogFileFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.logtracking.lib.internal.format.Tags.*;

public class NativeLogFileFormatterTest extends AbstractFormatterTest {

    static final String LINE_SEPARATOR = System.getProperty("line.separator");

    @Override
    protected LogFileFormatter setUpFormatter() {
        return new NativeLogFileFormatter();
    }

    public void testFormattedRecordShouldBeParsed() {
        //Not supported for this implementation
    }

    public void testFormattedMetaDataShouldBeParsed() {
        //Not supported for this implementation
    }

    public void testFullDocumentShouldBeParsed() {
        //Not supported for this implementation
    }

    @Override
    public void testMessageInfoShouldBeEqual() {
        TestDocument testDocument = formatDefaultDocument();
        TestDocument parsedDocument = tryParse(testDocument);

        assertEquals("Report message not equals" , testDocument.mMessage , parsedDocument.mMessage);
    }

    public void testMetaDataInfoShouldBeEqual() {
        TestDocument testDocument = formatDefaultDocument();
        TestDocument parsedDocument = tryParse(testDocument);

        for( Map.Entry<String,String> entry : parsedDocument.mMetaDataCollector.getData().entrySet() ){

            String originalValue = testDocument.mMetaDataCollector.getData().get( entry.getKey() );
            String parsedValue = entry.getValue();
            assertEquals("Meta-data not same", originalValue, parsedValue);

        }
    }

    public void testLogRecordInfoShouldBeEqual() {
        TestDocument testDocument = formatDefaultDocument();
        TestDocument parsedDocument = tryParse(testDocument);

        for(int i=0 ; i < testDocument.mRecords.size() ; i++){
            LogModel parsedRecord = parsedDocument.mRecords.get(i);
            LogModel originalRecord = testDocument.mRecords.get(i);
            assertEquals("Log records not same", parsedRecord.getFullLogRecord(), originalRecord.getFullLogRecord());
        }

    }

    public void testControlSymbolsShouldBeRemoved() {
        //Not supported for this implementation
    }

    private TestDocument tryParse(TestDocument testDocument){
        String formattedDocument = testDocument.mFormattedDocument;

        String reportMessage = tryParseReportMessage(formattedDocument);
        MetaDataCollector metaDataCollector = tryParseMetaData(formattedDocument);
        List<LogModel> logRecords = tryParseLogRecords(formattedDocument);

        return new TestDocument(reportMessage,metaDataCollector,logRecords,formattedDocument);
    }

    private String tryParseReportMessage(String formattedDocument){
        String reportMessageOpenTag = REPORT_MESSAGE_KEY + " : ";
        String reportMessageCloseTag = " : " + REPORT_MESSAGE_KEY;
        String reportMessage = getContentBetweenTags(formattedDocument, reportMessageOpenTag,
                                                                        reportMessageCloseTag);

        return  removeNonCharacterSymbols(reportMessage);
    }

    private MetaDataCollector tryParseMetaData(String formattedDocument) {

        String metaData = getContentBetweenTags(formattedDocument,  mFormatter.getMetaDataOpenTag(),
                                                                    mFormatter.getMetaDataCloseTag());


        MetaDataCollector metaDataCollector = new MetaDataCollector();
        String[] metaDataList = metaData.split(LINE_SEPARATOR);

        for (String metaDataPair : metaDataList) {

            if (TextUtils.isEmpty(metaDataPair))
                continue;

            String[] keyValuePair = metaDataPair.split("=");
            String key = removeNonCharacterSymbols(keyValuePair[0]);
            String value = removeNonCharacterSymbols(keyValuePair[1]);
            metaDataCollector.put(key, value);
        }

        return metaDataCollector;

    }

    private List<LogModel> tryParseLogRecords(String formattedDocument){
        String logRecords = getContentBetweenTags(formattedDocument,mFormatter.getLoggingOpenTag(),
                                                                    mFormatter.getLoggingCloseTag());

        List<LogModel> logRecordList = new ArrayList<LogModel>();

        for(String logRecord : logRecords.split(LINE_SEPARATOR) ){

            if (TextUtils.isEmpty(logRecord))
                continue;

            LogModel record = new LogModel();
            record.setFullLogRecord(logRecord);
            logRecordList.add(record);
        }

        return logRecordList;
    }

    private String getContentBetweenTags(String formattedDocument , String openTag, String closeTag){
        int start = formattedDocument.indexOf(openTag) + openTag.length();
        int end = formattedDocument.indexOf(closeTag);

        return start > 0 && end > 0 ? formattedDocument.substring(start,end) : null;
    }

    private String removeNonCharacterSymbols(String src){
        return  src.replaceAll("\t|\n|;", "");
    }
}
