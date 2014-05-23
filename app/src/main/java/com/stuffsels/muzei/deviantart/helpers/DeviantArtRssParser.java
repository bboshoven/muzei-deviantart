package com.stuffsels.muzei.deviantart.helpers;

import android.net.Uri;
import android.util.Log;
import android.util.Xml;
import com.stuffsels.muzei.deviantart.classes.Deviation;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DeviantArtRssParser {

    private static final String ns = null;
    private static final String feedUrl = "http://backend.deviantart.com/rss.xml?q=%s&type=deviation";
    private String continuationUrl = null;
    private List<Deviation> deviations;
    private Boolean allowNsfw;
    private String query;
    private Integer resultCount;

    public DeviantArtRssParser(String query, Integer resultCount, Boolean allowNsfw) {
        this.resultCount = resultCount;
        this.query = query;
        this.deviations = new ArrayList<Deviation>();
        this.allowNsfw = allowNsfw;
    }

    public List<Deviation> getDeviations() throws XmlPullParserException, IOException {
        try {
            continuationUrl = String.format(feedUrl, Uri.encode(query));
            while (continuationUrl != null && this.resultCount > deviations.size()) {
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(continuationUrl);
                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                InputStream is = httpEntity.getContent();
                continuationUrl = null;
                parse(is);
            }
        } catch (UnsupportedEncodingException e) {
        } catch (ClientProtocolException e) {
        } catch (IOException e) {
        }

        return deviations;
    }

    private void parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            readFeed(parser);
        } finally {
            in.close();
        }
    }

    private void readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {

        while (parser.nextTag() != XmlPullParser.END_TAG)
        {
            String name = parser.getName();
            if (name.equals("channel")) { //Look for channel-tag
                break;
            }
        }
        parser.require(XmlPullParser.START_TAG, null, "channel");
        while (parser.next() != XmlPullParser.END_TAG && this.resultCount > deviations.size()) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("atom:link")) { //Look for next url in atom:link
                parser.require(XmlPullParser.START_TAG, null, "atom:link");
                String rel = readAttribute(parser, ns, "rel");
                if (rel != null && rel.equals("next")) {
                    continuationUrl = readAttribute(parser, ns, "href");
                }
            }
            if (name.equals("item")) { //Look for item-tag
                Deviation deviation = readItem(parser);
                if (deviation != null //deviation should not be null
                        && deviation.medium.equals("image") //deviation should be an image
                        && (allowNsfw || (!allowNsfw && deviation.rating.equals("nonadult")))) //check if adult material
                    deviations.add(deviation);
            } else {
                skip(parser);
            }
        }
    }

    private Boolean isImage(String url)
    {
        if (url == null) { return false; }
        //Documentation: Sets the artwork's image URI, which must resolve to a JPEG or PNG image, ideally under 5MB.
        if (url.endsWith("jpg") || url.endsWith("jpeg") || url.endsWith("png")) {
            return true;
        }
        return false;
    }

    private Deviation readItem(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, null, "item");
        Deviation deviation = new Deviation();
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("title")) {
                deviation.title = readTag(parser, ns, "title");
            } else if (name.equals("link")) {
                deviation.link = readTag(parser, ns, "link");
            } else if (name.equals("media:content")) {
                if (deviation.imageUrl == null) {
                    deviation.imageUrl = readAttribute(parser, ns, "url");
                }
                if (deviation.medium == null) {
                    deviation.medium = readAttribute(parser, ns, "medium");
                    if (deviation.medium == null && deviation.imageUrl != null) { //RSS feed does not seem to always return a medium
                        if (isImage(deviation.imageUrl)) {
                            deviation.medium = "image";
                        }
                    }
                    if (deviation.medium == null) {
                        deviation.medium = "unknown";
                    }
                }
                parser.nextTag();
            } else if (name.equals("guid")) {
                deviation.guid = readTag(parser, ns, "guid");
            } else if (name.equals("media:credit") && deviation.userName == null) {
                deviation.userName = readTag(parser, ns, "media:credit");
            } else if (name.equals("media:rating")) {
                deviation.rating = readTag(parser, ns, "media:rating");
            } else {
                skip(parser);
            }
        }
        if (deviation.imageUrl == null || !isImage(deviation.imageUrl)) {
            return null;
        }
        return deviation;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

    private String readTag(XmlPullParser parser, String ns, String tag) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, tag);
        String title;
        title = readText(parser);
        parser.nextTag();
        parser.require(XmlPullParser.END_TAG, ns, tag);
        return title;
    }

    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        Integer next = parser.next();
        if (next == XmlPullParser.TEXT) {
            result = parser.getText();
        }
        return result;
    }

    private String readAttribute(XmlPullParser parser, String ns, String attr) throws IOException, XmlPullParserException {
        return parser.getAttributeValue(ns, attr);
    }
}
