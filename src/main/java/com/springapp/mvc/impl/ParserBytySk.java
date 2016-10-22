package com.springapp.mvc.impl;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.Match;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParserBytySk {

    private static final String RegexCounterPages = "Strana \\d* z (\\d*)";

    private int counter = 0;

    public void parse() {
        String baseUrl = "http://www.byty.sk/bratislava-iv-karlova-ves/predaj";
        try {
            Document doc = getDocument(baseUrl);
            Elements listCounter = doc.select(".page-info-counter");
            String strCounterPages = listCounter.text();
            Pattern pattern = Pattern.compile(RegexCounterPages);
            Matcher matcher = pattern.matcher(strCounterPages);
            int countPages = 1;
            if (matcher.find()) {
                String strCountPages = matcher.group(1);
                countPages = Integer.parseInt(strCountPages);
            }

            // parse the first page, we already have loaded document
            parsePage(doc);

            // parse other pages if there are any
            if (countPages > 1) {
                for (int i = 2; i <= countPages; ++i) {
                    String url = baseUrl + "?p[page]=" + i;
                    parsePage(url);
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void parsePage(String url) throws IOException{
        Document doc = getDocument(url);
        parsePage(doc);
    }

    private void parsePage(Document doc) {
        Elements listInzerat = doc.select(".inzerat");
        for (Element eleInzerat : listInzerat) {
            String html = eleInzerat.html();
            String address = "";
            int area = 0;
            int price = 0;
            Element eleLink = eleInzerat.select("h2 > a").first();
            String title = eleLink.text();
            String link = eleLink.attr("href");
            Element eleLocationText = eleInzerat.select(".locationText").first();
            if (eleLocationText != null) {
                address = eleLocationText.text();
            }
            Element eleArea = eleInzerat.select(".estate-area .red").first();
            if (eleArea != null) {
                String strArea = eleArea.text();
                area = parseInt(strArea);
            }
            Element eleCena = eleInzerat.select(".cena .tlste.red").first();
            if (eleCena != null) {
                String strPrice = eleCena.text();
                price = parseInt(strPrice);
            }

            boolean isComplete = !address.isEmpty() && area > 0 && price > 0;
            System.err.println("Entry is not complete!");

            String message = String.format("# %d\n%s\nAddress: %s\nPrice: %s\nArea: %s\n",
                    counter, title, address, price, area);
            System.out.println(message);
            ++counter;
        }
    }

    private Document getDocument(String url) throws IOException{
        String html = HtmlCache.get(url);
        Document doc = Jsoup.parse(html);
        return doc;
    }

    private int parseInt(String input) {
        if (input == null) {
            System.err.println(String.format("Cannot parse string [%s] to int!", input));
            return 0;
        }

        String strNumber = "";
        for (int i = 0; i < input.length(); ++i) {
            char c = input.charAt(i);
            if (isWhitespace(c)) {
                continue;
            }
            if (!isNumber(c)) {
                break;
            }
            strNumber += c;
        }

        if (strNumber.length() == 0) {
            System.err.println(String.format("Cannot parse string [%s] to int!", input));
            return 0;
        }

        int number = Integer.parseInt(strNumber);
        return number;
    }

    private boolean isWhitespace(char c) {
        return c == ' ';
    }

    private boolean isNumber(char c) {
        return c >= '0' && c <= '9';
    }
}
