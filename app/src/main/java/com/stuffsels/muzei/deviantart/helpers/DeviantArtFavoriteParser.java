package com.stuffsels.muzei.deviantart.helpers;

import android.util.Log;

import com.stuffsels.muzei.deviantart.classes.CollectionItem;
import com.stuffsels.muzei.deviantart.classes.Deviation;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DeviantArtFavoriteParser {

    private List<Deviation> deviations;
    private Boolean allowNsfw;
    private String userName;
    private String collectionName;

    public DeviantArtFavoriteParser(Boolean allowNsfw, String userName, String collectionName) {
        this.deviations = new ArrayList<Deviation>();
        this.allowNsfw = allowNsfw;
        this.userName = userName;
        this.collectionName = collectionName;
    }

    public List<CollectionItem> getCollections() throws IOException {
        Document doc = Jsoup.connect("http://" + userName.toLowerCase() + ".deviantart.com/favourites/").get();
        Elements items = doc.select("#gmi-GZone .tv150");
        List<CollectionItem> collections = new ArrayList<CollectionItem>();
        for (Element item : items) {
            collections.add(getCollection(item));
        }
        return collections;
    }

    public List<Deviation> getDeviations() throws IOException {
        List<CollectionItem> items = getCollections();
        String url = "http://" + userName.toLowerCase() + ".deviantart.com/favourites/";
        if (!collectionName.equals("")) {
            for (CollectionItem item : items) {
                if (item.name.equalsIgnoreCase(collectionName)) {
                    url = item.url;
                }
            }
        }
        Document doc = Jsoup.connect(url).get();
        Elements thumbs = doc.select(".folderview-art .tt-a");
        for (Element thumb : thumbs) {
            Deviation deviation = getDeviation(thumb);
            if (deviation != null //deviation should not be null
                    && deviation.medium.equals("image") //deviation should be an image
                    && (allowNsfw || (!allowNsfw && deviation.rating.equals("nonadult"))))
                this.deviations.add(deviation);
        }
        return deviations;
    }

    private CollectionItem getCollection(Element parent) {
        CollectionItem collection = new CollectionItem();
        collection.url = parent.select("a.tv150-cover").attr("href");
        collection.name = parent.select(".tv150-tag").text();
        return collection;
    }

    private Deviation getDeviation(Element thumb)
    {
        Element descriptiveTag = thumb;
        Element link = thumb.select("a.thumb").first();
        Element titleTag = thumb.select(".details a").first();
        Deviation deviation = new Deviation();
        deviation.userName = descriptiveTag.attr("username");
        deviation.category = descriptiveTag.attr("category");
        deviation.title = titleTag.text();
        deviation.link = link.attr("href");
        deviation.guid = deviation.link;
        deviation.imageUrl = link.attr("data-super-img");
        if (deviation.imageUrl.equals("")) {
            deviation.imageUrl = link.select("img").first().attr("data-src");
            deviation.imageUrl = deviation.imageUrl.replace("200H/", "");
        }
        deviation.rating = link.hasClass("ismature") ? "adult" : "nonadult";
        deviation.medium = UrlHelper.isImage(deviation.imageUrl) ? "image" : "unknown";
        return deviation;
    }

}
