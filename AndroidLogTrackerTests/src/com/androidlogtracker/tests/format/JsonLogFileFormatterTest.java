package  com.androidlogtracker.tests.format;

import com.androidlogtracker.tests.util.TestDocument;
import com.logtracking.lib.internal.LogModel;
import com.logtracking.lib.internal.format.JsonLogFileFormatter;
import com.logtracking.lib.internal.format.LogFileFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.androidlogtracker.tests.util.AssertUtils.*;
import static com.logtracking.lib.internal.format.Tags.*;

public class JsonLogFileFormatterTest extends AbstractFormatterTest {

    @Override
    protected LogFileFormatter setUpFormatter() {
        return new JsonLogFileFormatter();
    }

    @Override
    public void testFormattedRecordShouldBeParsed() {
        tryParse(formatLogRecords());
    }

    @Override
    public void testFormattedMetaDataShouldBeParsed() {
        tryParse(formatDefaultDocument());
    }

    @Override
    public void testFullDocumentShouldBeParsed() {
        tryParse(formatDefaultDocument());
    }

    @Override
    public void testMessageInfoShouldBeEqual() {
        TestDocument testDocument = formatDefaultDocument();
        JSONObject jsonDocument = tryParse(testDocument);
        assertEqual(jsonDocument, REPORT_MESSAGE_KEY, testDocument.mMessage);
    }

    @Override
    public void testLogRecordInfoShouldBeEqual() {
        TestDocument document = formatLogRecords(1);
        LogModel record = document.mRecords.get(0);
        JSONObject jsonDoc = tryParse(document);

        JSONObject jsonLogRecord = getJSONObject ( getObjectFromArray(jsonDoc,LOGGING_TAG,0) , RECORD_KEY);

        assertEqual(jsonLogRecord, DATE_KEY, record.getFormattedDate());
        assertEqual(jsonLogRecord, LEVEL_KEY, record.getLevelSymbol().toString());
        assertEqual(jsonLogRecord, PID_KEY, Integer.toString(record.getPid()));
        assertEqual(jsonLogRecord, TID_KEY, Long.toString(record.getTid()));
        assertEqual(jsonLogRecord, PACKAGE_NAME_KEY, record.getPackageName());
        assertEqual(jsonLogRecord, MESSAGE_KEY, record.getMessage());
    }

    @Override
    public void testMetaDataInfoShouldBeEqual() {
    }

    @Override
    public void testControlSymbolsShouldBeRemoved() {
    }

    private void assertEqual(JSONObject doc, String key , String expected){
        try {
            assertEquals("String ", doc.getString(key), expected);
        } catch (JSONException e) {
            throwAssertError(e);
        }
    }

    private JSONObject tryParse(TestDocument testDocument){
        JSONObject result = null;
        try {

            return new JSONObject(testDocument.mFormattedDocument);

        } catch (JSONException e) {
            throwAssertError(e);
        }
        return result;
    }

    private JSONObject getJSONObject(JSONObject src , String name){
        try {
            return src.getJSONObject(name);
        } catch (JSONException e) {
            throwAssertError(e);
        }
        return null;
    }

    private JSONObject getObjectFromArray(JSONObject jsonDoc, String arrayName , int index){
        try {
            JSONArray array = jsonDoc.getJSONArray(arrayName);
            return  array.getJSONObject(index);
        } catch (JSONException e) {
            throwAssertError(e);
        }
        return null;
    }
}
