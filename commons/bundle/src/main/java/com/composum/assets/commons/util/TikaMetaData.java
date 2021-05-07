package com.composum.assets.commons.util;

import com.composum.sling.core.filter.StringFilter;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TikaMetaData extends HashMap<String, Object> {

    private static final Logger LOG = LoggerFactory.getLogger(TikaMetaData.class);

    public static final Pattern INT_PROPERTY = Pattern.compile("^[^0-9]*([+-]?[0-9]+)[^0-9]*$");

    public static final String[] DATE_FORMATS = new String[]{
            "yyyy-MM-dd'T'HH:mm:ssX",
            "yyyy-MM-dd'T'HH:mm:ss",
            "yyyy:MM:dd' 'HH:mm:ss",
            "yyyy-MM-dd",
    };

    public static TikaMetaData parseMetadata(InputStream content, StringFilter nameFilter) {
        TikaMetaData result = new TikaMetaData();
        try {
            Parser parser = new AutoDetectParser();
            BodyContentHandler handler = new BodyContentHandler();
            Metadata metadata = new Metadata();
            ParseContext context = new ParseContext();
            parser.parse(content, handler, metadata, context);
            for (String name : metadata.names()) {
                if (nameFilter.accept(name)) {
                    result.put(name, metadata.get(name));
                } else if (LOG.isDebugEnabled()) {
                    LOG.debug("Rejected metadata name {} : {}", name, metadata.get(name));
                }
            }
        } catch (TikaException | SAXException | IOException | NoClassDefFoundError ex) {
            LOG.error(ex.getMessage(), ex);
        }
        return result;
    }

    public interface Converter<T> {

        T valueOf(String stringValue);

        Pattern valuePattern();
    }

    public <T> T getValue(Converter<T> converter, String... keysToTry) {
        T result = null;
        for (String key : keysToTry) {
            Object value = get(key);
            if (value instanceof String) {
                String string = (String) value;
                Pattern pattern = converter.valuePattern();
                if (pattern != null) {
                    Matcher matcher = pattern.matcher(string);
                    if (matcher.matches()) {
                        result = converter.valueOf(matcher.group(1));
                        break;
                    }
                } else {
                    result = converter.valueOf(string);
                    break;
                }
            }
        }
        return result;
    }

    public Integer getInt(String... keysToTry) {
        return getValue(new Converter<Integer>() {

            @Override
            public Integer valueOf(String stringValue) {
                return Integer.valueOf(stringValue);
            }

            @Override
            public Pattern valuePattern() {
                return INT_PROPERTY;
            }

        }, keysToTry);
    }

    public Long getLong(String... keysToTry) {
        return getValue(new Converter<Long>() {

            @Override
            public Long valueOf(String stringValue) {
                return Long.valueOf(stringValue);
            }

            @Override
            public Pattern valuePattern() {
                return INT_PROPERTY;
            }

        }, keysToTry);
    }

    public Date getDate(String... keysToTry) {
        return getValue(new Converter<Date>() {

            @Override
            public Date valueOf(String stringValue) {
                for (String format : DATE_FORMATS) {
                    try {
                        return new SimpleDateFormat(format).parse(stringValue);
                    } catch (ParseException ignore) {
                    }
                }
                return null;
            }

            @Override
            public Pattern valuePattern() {
                return null;
            }

        }, keysToTry);
    }
}
