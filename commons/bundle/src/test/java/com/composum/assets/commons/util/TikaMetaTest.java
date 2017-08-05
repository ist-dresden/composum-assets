package com.composum.assets.commons.util;

import com.composum.sling.core.filter.StringFilter;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.junit.Test;

import java.io.InputStream;

public class TikaMetaTest {

    public static final String[] FILES = new String[]{
            "/images/fire.JPG",
            "/images/apres_midi_dun_faune.jpg",
            "/images/Composum-blue-yellow.png",
            "/images/Composum-color.svg",
            "/videos/testvideo.m4v"
    };

    public static final StringFilter IMAGE_META_DATA = new StringFilter.WhiteList(
            "^[Cc]ontent[ -]?[Tt]ype$",
            "^([Ff]ile ?)?[Ss]ize$",
            "^([Ii]mage ?)?([Ww]idth|[Hh]eight)$",
            "^[Dd]escription$",
            "^[Cc]opyright( ?[Uu]rl)?$",
            "^[Aa]uthor( ?[Uu]rl)?$",
            "^[Cc]redits( ?[Uu]rl)?$",
            "^[Ll]icense( ?[Uu]rl)?$",
            "^[Oo]rigin( ?[Uu]rl)?$",
            "^[Ss]ource( ?[Uu]rl)?$",
            "^[Ll]ocation( ?[Uu]rl)?$",
            "^([Gg]oogle)?[Ee]arth$",
            "^[Oo]rientation$",
            "^([Ll]ast[ -]?)?[Mm]odified$",
            "^[Dd]ate(/[Tt]ime)?( ?[Oo]riginal)?$",
            "^[Dd]imension( [\\w\\s]+)?$",
            "^[Tt]ransparency( [\\w\\s]+)?$",
            "^.*[Cc]olor ?[Ss]pace.*$",
            "^[Dd]ata( [\\w\\s]+)$",
            "^[Cc]ompression( [\\w\\s]+)?$",
            "^([\\w\\s]+ )?[Qq]uality$",
            "^([Uu]ser ?)?[Cc]omments?$",
            "^([XxYy][ -])?[Rr]esolution([ -][Uu]nits)?$"
    );

    @Test
    public void testMetaData() throws Exception {

        for (String filename : FILES) {
            System.out.println("\nFILE: " + filename);

            Parser parser = new AutoDetectParser();
            BodyContentHandler handler = new BodyContentHandler();
            Metadata metadata = new Metadata();
            ParseContext context = new ParseContext();

            try (InputStream inputstream = getClass().getResourceAsStream(filename)) {
                parser.parse(inputstream, handler, metadata, context);
            }

            System.out.println("----\naccepted\n----");
            for (String name : metadata.names()) {
                if (IMAGE_META_DATA.accept(name)) {
                    System.out.println(name + ": " + metadata.get(name));
                }
            }
            System.out.println("----\nnot accepted\n----");
            for (String name : metadata.names()) {
                if (!IMAGE_META_DATA.accept(name)) {
                    System.out.println(name + ": " + metadata.get(name));
                }
            }
            System.out.println("----");
        }
    }
}
