package com.research.assistant;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Service
public class CitationService {

    public Map<String, String> extractMeta(String url) throws IOException {

        Document doc = Jsoup.connect(url).get();

        String title = doc.title();
        String author = doc.select("meta[name=author]").attr("content");
        String date = doc.select("meta[property=article:published_time]").attr("content");
        String site = doc.select("meta[property=og:site_name]").attr("content");

        if (site.isEmpty()) {
            site = new URL(url).getHost();
        }

        Map<String, String> meta = new HashMap<>();
        meta.put("title", title);
        meta.put("author", author);
        meta.put("date", date);
        meta.put("site", site);
        meta.put("url", url);

        return meta;
    }

    public String generateAPA(Map<String, String> m) {
        String author = m.get("author");
        String date = m.get("date");
        String title = m.get("title");
        String site = m.get("site");
        String url = m.get("url");

        if (author == null || author.isEmpty())
            author = "Anonymous";
        if (date == null || date.isEmpty())
            date = "n.d.";

        return author + " (" + date + "). " + title + ". " + site + ". " + url;
    }

    // MLA citation
    public String generateMLA(Map<String, String> m) {
        String author = m.get("author");
        String date = m.get("date");
        String title = m.get("title");
        String site = m.get("site");
        String url = m.get("url");

        if (author == null || author.isEmpty())
            author = "Anonymous";
        if (date == null || date.isEmpty())
            date = "n.d.";

        return author + ". \"" + title + ".\" " + site + ", " + date + ", " + url + ".";
    }

    // IEEE citation
    public String generateIEEE(Map<String, String> m) {
        String author = m.get("author");
        String date = m.get("date");
        String title = m.get("title");
        String site = m.get("site");
        String url = m.get("url");

        if (author == null || author.isEmpty())
            author = "Anonymous";
        if (date == null || date.isEmpty())
            date = "n.d.";

        return "[1] " + author + ", \"" + title + ",\" " + site + ", " + date +
                ". Available: " + url;
    }
}
