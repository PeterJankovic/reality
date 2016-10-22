package com.springapp.mvc.impl;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.springframework.core.io.FileSystemResource;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;

public class HtmlCache {

    private static final String CacheDirectory = "html-cache/";
    private static final String HtmlSuffix = ".html";

    public static String get(String url) throws IOException{
        String html;
        String urlEscaped = URLEncoder.encode(url);
        String cachedFileName = CacheDirectory + urlEscaped + HtmlSuffix;
        File file = new FileSystemResource(cachedFileName).getFile();
        if (!file.exists()) {
            // get file from web and cache it
            html = Jsoup.connect(url).userAgent("Mozilla").get().outerHtml();
            file.createNewFile();
            FileUtils.writeStringToFile(file, html);
        }
        else {
            // load file from cache
            html = FileUtils.readFileToString(file);
        }

        return html;
    }
}
