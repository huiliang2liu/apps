package com.image;

import android.graphics.Bitmap;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import androidx.annotation.NonNull;

public class PlistParser {
    private static SAXParser parser;

    public static PlistImage parser(InputStream is, Bitmap bitmap) {
        try {
            if (parser == null)
                parser = SAXParserFactory.newInstance().newSAXParser();
            Handler handler = new Handler();
            try {
                parser.parse(is, handler);
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            List<Image> images = handler.images;
            if (images == null || images.size() <= 0)
                return null;
            PlistImage i = new PlistImage();
            i.images = images;
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            i.pixels = new int[width * height];
            bitmap.getPixels(i.pixels, 0, width, 0, 0, width, height);
            i.bW = width;
            return i;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static class PlistImage {
        List<Image> images;
        int[] pixels;
        int bW;

        public Bitmap name2Image(String name) {
            for (Image image : images) {
                if (name.equals(image.name)) {
                    int startWidth = image.frame1.x;
                    int startHeight = image.frame1.y;
                    int width = image.frame2.x;
                    int height = image.frame2.y;
                    int[] p = new int[width * height];
                    for (int i = 0; i < height; i++) {
                        for (int j = 0; j < width; j++) {
                            p[i * width + j] = pixels[(i + startHeight) * bW + startWidth + j];
                        }
                    }
                    return Bitmap.createBitmap(p, width, height, Bitmap.Config.ARGB_8888);
                }
            }
            return null;
        }
    }

    private static class Image {
        String name;
        Point frame1;
        Point frame2;
        Point offset;
        boolean rotated;
        Point sourceColorRect1;
        Point sourceColorRect2;
        Point sourceSize;

        @NonNull
        @Override
        public String toString() {
            return String.format("<key>%s</key>\n<dict>\n" +
                    "                <key>frame</key>\n" +
                    "                <string>{%s,%s}</string>\n" +
                    "                <key>offset</key>\n" +
                    "                <string>%s</string>\n" +
                    "                <key>rotated</key>\n" +
                    "                <%s/>\n" +
                    "                <key>sourceColorRect</key>\n" +
                    "                <string>{%s,%s}</string>\n" +
                    "                <key>sourceSize</key>\n" +
                    "                <string>%s</string>\n" +
                    "            </dict>", name, frame1, frame2, offset, rotated, sourceColorRect1, sourceColorRect2, sourceSize);
        }
    }

    private static class Point {
        int x;
        int y;

        @NonNull
        @Override
        public String toString() {
            return String.format("{%d,%d}", x, y);
        }
    }

    private static class Handler extends DefaultHandler {
        private List<Image> images;
        private StringBuilder builder;
        private int index = 0;
        private Image image;
        private String keyName;

        @Override
        public void startDocument() throws SAXException {
            super.startDocument();
            images = new ArrayList<>();
            builder = new StringBuilder();
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            super.startElement(uri, localName, qName, attributes);
            if (index == 2) {
            } else {
                if ("dict".equals(qName))
                    index++;
            }

        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            super.characters(ch, start, length);
            builder.append(ch, start, length);
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            super.endElement(uri, localName, qName);
            if (index == 2) {
                if ("key".equals(qName)) {
                    if (image == null) {
                        image = new Image();
                        image.name = builder.toString().trim();
                    } else {
                        keyName = builder.toString().trim();
                    }
                } else if ("string".equals(qName)) {
                    if ("frame".equals(keyName)) {
                        String s = builder.toString().trim();
                        s = s.substring(1, s.length() - 1);
                        String[] strings = s.split(",");
                        image.frame1 = string2point(String.format("%s,%s", strings[0].trim(), strings[1].trim()));
                        image.frame2 = string2point(String.format("%s,%s", strings[2].trim(), strings[3].trim()));
                    } else if ("offset".equals(keyName)) {
                        image.offset = string2point(builder.toString().trim());
                    } else if ("sourceColorRect".equals(keyName)) {
                        String s = builder.toString().trim();
                        s = s.substring(1, s.length() - 1);
                        String[] strings = s.split(",");
                        image.sourceColorRect1 = string2point(String.format("%s,%s", strings[0].trim(), strings[1].trim()));
                        image.sourceColorRect2 = string2point(String.format("%s,%s", strings[2].trim(), strings[3].trim()));
                    } else if ("sourceSize".equals(keyName)) {
                        image.sourceSize = string2point(builder.toString().trim());
                    }
                } else if ("dict".equals(qName)) {
                    if (image != null) {
                        images.add(image);
                        image = null;
                    }
                } else if ("false".equals(qName) || "true".equals(qName))
                    image.rotated = Boolean.valueOf(qName);
            }
            builder.setLength(0);
        }

        private List<Image> images() {
            return images;
        }

        private Point string2point(String s) {
            s = s.trim();
            s = s.substring(1, s.length() - 1);
            String[] p = s.split(",");
            Point point = new Point();
            point.x = Integer.valueOf(p[0].trim());
            point.y = Integer.valueOf(p[1].trim());
            return point;
        }
    }
}
