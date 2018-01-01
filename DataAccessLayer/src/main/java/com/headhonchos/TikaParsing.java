/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.headhonchos;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.detect.Detector;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;

/**
 *
 * @author richa
 */
public class TikaParsing {

    private OutputStream outputstream;
    private ParseContext context;
    private Detector detector;
    private Parser parser;
    private Metadata metadata;
    private String extractedText;

    public TikaParsing() {
        //prepare tika parser for parsing
        context = new ParseContext();
        detector = new DefaultDetector();
        parser = new AutoDetectParser(detector);
        context.set(Parser.class, parser);
        outputstream = new ByteArrayOutputStream();
        metadata = new Metadata();
    }

    public void process(String filename) throws Exception {
        URL url = null;
        File file = new File(filename);
        try {
            if (file.isFile()) {
                url = file.toURI().toURL();
            } else {
                url = new URL(filename);
            }

        InputStream input = TikaInputStream.get(url, metadata);
        ContentHandler handler = new BodyContentHandler(outputstream);
        parser.parse(input, handler, metadata, context);
        input.close();
        }catch(MalformedURLException e1){
            System.out.println("Malformed URL: " + filename);
            extractedText = "";
        }
        catch(Exception e2){
            System.out.println("TikaParsing Exception:-- " + e2.getMessage());
            extractedText = "";
        }
    }

    public String getString() {
        //Get the text into a String object
        extractedText = outputstream.toString();
        //Do whatever you want with this String object.
        //System.out.println(extractedText);
        //strip html
        extractedText = extractedText.replaceAll("\\<.*?\\>", " ");
        return extractedText;
    }

    public static void main(String args[]) throws Exception {
        for (String s : args) {
            TikaParsing textExtractor = new TikaParsing();
            textExtractor.process(s);
            String text = textExtractor.getString();
//			System.out.println(text);
        }
    }
}
