package com.stuffsels.muzei.deviantart.helpers;

import com.stuffsels.muzei.deviantart.classes.Deviation;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DeviantArtDDParser {

    private List<Deviation> deviations;
    private Boolean allowNsfw;

    public DeviantArtDDParser(Boolean allowNsfw) {
        this.deviations = new ArrayList<Deviation>();
        this.allowNsfw = allowNsfw;
    }

    public List<Deviation> getDeviations() throws IOException {
        Document doc = Jsoup.connect("http://www.deviantart.com/dailydeviations/").get();
        Elements thumbs = doc.select(".ddtable td");
        for (Element thumb : thumbs)
        {
            Deviation deviation = getDeviation(thumb);
            if (deviation != null //deviation should not be null
                    && deviation.medium.equals("image") //deviation should be an image
                    && (allowNsfw || (!allowNsfw && deviation.rating.equals("nonadult"))))
                this.deviations.add(deviation);
        }
        return deviations;
    }

    private Deviation getDeviation(Element thumb)
    {
        Element descriptiveTag = thumb.select("div[userid]").first();
        Element link = thumb.select("a.thumb").first();
        Element featuredBySpan = thumb.select("div.foot span.username-with-symbol").last();
        Element titleTag = thumb.select("div.ddinfo a").first();
        Deviation deviation = new Deviation();
        deviation.userName = descriptiveTag.attr("username");
        deviation.category = descriptiveTag.attr("category");
        deviation.title = titleTag.text();
        deviation.link = link.attr("href");
        deviation.guid = deviation.link;
        deviation.imageUrl = link.attr("data-super-full-img");
        deviation.rating = link.hasClass("ismature") ? "adult" : "nonadult";
        deviation.medium = isImage(deviation.imageUrl) ? "image" : "unknown";
        deviation.featuredBy = featuredBySpan.text();
        return deviation;
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

}
