package  com.androidlogtracker.tests.format;


public interface FormatTestSuites {

    public void testRecordFormattedStringShouldBeNotEmpty();

    public void testMetaDataFormattedStringShouldBeNotEmpty();

    public void testMessageFormattedStringShouldBeNotEmpty();

    public void testFormattedRecordShouldBeParsed();

    public void testFormattedMetaDataShouldBeParsed();

    public void testFullDocumentShouldBeParsed();

    public void testMessageInfoShouldBeEqual();

    public void testLogRecordInfoShouldBeEqual();

    public void testMetaDataInfoShouldBeEqual();

    public void testControlSymbolsShouldBeRemoved();
}
