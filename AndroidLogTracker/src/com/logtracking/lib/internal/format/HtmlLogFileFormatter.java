package com.logtracking.lib.internal.format;

import android.content.Context;
import com.logtracking.lib.internal.LogModel;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import static com.androidlogtracker.R.string.*;
import static java.lang.String.*;

public class HtmlLogFileFormatter implements LogFileFormatter {

    private Context mContext;
    private TableFormatter mTableFormatter;
    private SpannableTableFormatter mSpannableTableFormatter;

    private Map<Character,String> mLevelSpanMap;

    public HtmlLogFileFormatter(Context appContext){
        mContext = appContext;
        mTableFormatter = new TableFormatter();
        mSpannableTableFormatter = new SpannableTableFormatter();

        mLevelSpanMap = new HashMap<Character,String>();
        mLevelSpanMap.put('E',getString(alt_html_error_log));
        mLevelSpanMap.put('W',getString(alt_html_warm_log));
        mLevelSpanMap.put('I',getString(alt_html_info_log));
        mLevelSpanMap.put('D',getString(alt_html_debug_log));
        mLevelSpanMap.put('V',getString(alt_html_verbose_log));
    }

    @Override
    public String getFileExtension() {
        return ".html";
    }

    @Override
    public String getDocumentOpenTag() {
        return getString(alt_html_document_open);
    }

    @Override
    public String formatMessage(String message) {
        return format(getString(alt_html_report_message), message);
    }

    @Override
    public String getMetaDataOpenTag() {
        return "";
    }

    @Override
    public String getMetaDataCloseTag() {
        return "";
    }

    @Override
    public String formatMetaData(Map<String,String> metaData) {
        StringBuilder builder = new StringBuilder();
        builder.append(getString(alt_html_meta_data_header_text));
        builder.append(mTableFormatter.getTableOpenTag());
        builder.append(mTableFormatter.formatTableHeader(alt_html_meta_table_key, alt_html_meta_table_value));
        for(Entry<String,String> meta : metaData.entrySet()){
            builder.append(mTableFormatter.formatTableRow(meta.getKey(), meta.getValue()));
        }
        builder.append(mTableFormatter.getTableCloseTag());
        return builder.toString();
    }

    @Override
    public String getLoggingOpenTag() {
        StringBuilder builder = new StringBuilder();
        builder.append(getString(alt_html_logging_header_text));
        builder.append(mSpannableTableFormatter.getTableOpenTag());
        builder.append(mSpannableTableFormatter.formatTableHeader(alt_html_logging_table_date,
                                                                  alt_html_logging_table_level,
                                                                  alt_html_logging_table_pid,
                                                                  alt_html_logging_table_tid,
                                                                  alt_html_logging_table_package,
                                                                  alt_html_logging_table_tag,
                                                                  alt_html_logging_table_message));
        return builder.toString();
    }

    @Override
    public String getLoggingCloseTag() {
        return mSpannableTableFormatter.getTableCloseTag();
    }

    @Override
    public String formatLogRecord(LogModel model) {
        return mSpannableTableFormatter.formatSpanRow(  mLevelSpanMap.get(model.getLevelSymbol()),
                                                        model.getFormattedDate(),
                                                        Character.toString(model.getLevelSymbol()),
                                                        Integer.toString(model.getPid()),
                                                        Long.toString(model.getTid()),
                                                        model.getPackageName(),
                                                        model.getTag(),
                                                        model.getMessage());
    }

    @Override
    public String getDocumentCloseTag() {
        return getString(alt_html_document_close);
    }

    private String getString(int resourceId){
        return mContext.getResources().getString(resourceId);
    }

    private String[] getStrings(int... resourceIds){
        String[] strings = new String[resourceIds.length];
        for(int i=0;i<resourceIds.length;i++){
            strings[i] = getString(resourceIds[i]);
        }
        return strings;
    }

    private class TableFormatter {

        String mTableOpenTag;
        String mTableCloseTag;

        String mTableRowOpenTag;
        String mTableRowCloseTag;

        String mTableHeaderCellOpenTag;
        String mTableHeaderCellCloseTag;

        String mTableCellOpenTag;
        String mTableCellCloseTag;

        TableFormatter(){
            mTableOpenTag = format(getString(alt_html_table_open_tag),getString(alt_html_table_class_name));
            mTableCloseTag = getString(alt_html_table_close_tag);

            mTableRowOpenTag = getString(alt_html_table_row_open_tag);
            mTableRowCloseTag = getString(alt_html_table_row_close_tag);

            mTableHeaderCellOpenTag = getString(alt_html_table_header_cell_open_tag);
            mTableHeaderCellCloseTag = getString(alt_html_table_header_cell_close_tag);

            mTableCellOpenTag = getString(alt_html_table_cell_open_tag);
            mTableCellCloseTag = getString(alt_html_table_cell_close_tag);
        }

        String getTableOpenTag(){
            return mTableOpenTag;
        }

        String formatTableHeader(int... columns){
            return formatRow(mTableHeaderCellOpenTag, mTableHeaderCellCloseTag, getStrings(columns));
        }

        String formatTableRow(String... columns){
            return formatRow(mTableCellOpenTag, mTableCellCloseTag, columns);
        }

        String formatRow(String cellOpenTag, String cellCloseTag, String... columns){
            StringBuilder builder = new StringBuilder();
            builder.append(mTableRowOpenTag);
            for(String column : columns){
                builder.append(cellOpenTag);
                builder.append(column);
                builder.append(cellCloseTag);
            }
            builder.append(mTableRowCloseTag);
            return builder.toString();
        }

        String getTableCloseTag(){
            return mTableCloseTag;
        }
    }

    private class SpannableTableFormatter extends TableFormatter{

        String mSpanOpenTag;
        String mSpanCloseTag;


        String mSpanCellOpenTag;
        String mSpanCellCloseTag;

        SpannableTableFormatter() {
            super();
            mSpanOpenTag = getString(alt_html_span_open_tag);
            mSpanCloseTag = getString(alt_html_span_close_tag);
            mSpanCellOpenTag = mTableCellOpenTag + mSpanOpenTag;
            mSpanCellCloseTag = mSpanCloseTag + mTableCellCloseTag;
        }

        String formatSpanRow(String style, String... columns){
            return formatRow(format(mSpanCellOpenTag,style), mSpanCellCloseTag, columns);
        }
    }
}
