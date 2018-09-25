package com.edu.epam.novytska.producer.impl;

import com.edu.epam.novytska.constants.HttpConnectionConst;
import com.edu.epam.novytska.constants.HtmlParfumsConst;
import com.edu.epam.novytska.constants.TemplateParfumsConstRu;
import com.edu.epam.novytska.constants.TemplateParfumsConstUa;
import com.edu.epam.novytska.consumer.SiteProductConsumer;
import com.edu.epam.novytska.producer.SiteProductProducer;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class ParfumsUaCrawler implements SiteProductProducer {

    private static final String BASE_PARFUM_CATEGORY_LINK = "https://parfums.ua/category/parfums/none/1/60";
    private String language;

    @Override
    public void start(SiteProductConsumer consumer) {
        log.debug("Start crawling");
        Document doc;
        try {
            doc = documentFactory(BASE_PARFUM_CATEGORY_LINK);
        } catch (IOException e) {
            log.warn("Can't open site: " + BASE_PARFUM_CATEGORY_LINK);
            return;
        }
        getAllProducts(consumer, doc);
    }

    private Document documentFactory(String link) throws IOException {
        Document doc;
        try{
            //first try to access the link
            doc = Jsoup.connect(link).userAgent(HttpConnectionConst.DEFAULT_UA.toString())
                    .userAgent(HttpConnectionConst.USER_AGENT.toString()).timeout(10000).get();
        } catch (IOException ex){
            //second try to access the link - in the case of IOException again - will be handled in in the level up
            doc = Jsoup.connect(link).userAgent(HttpConnectionConst.DEFAULT_UA.toString())
                    .userAgent(HttpConnectionConst.USER_AGENT.toString()).timeout(10000).get();
        }
        return doc;
    }

    private void getAllProducts(SiteProductConsumer consumer, Document doc) {
        this.language = doc.getElementsByTag(HtmlParfumsConst.HTML_TAG.toString()).attr(HtmlParfumsConst.HTML_LANG_ATTR.toString());
        Elements pages = doc.getElementsByClass(HtmlParfumsConst.PAGINATION_CLASS.toString());
        Elements blocks1 = doc.select(HtmlParfumsConst.PRODUCT_BLOCK_SELECTOR.toString());
        getProductSpecs(blocks1, consumer);
        if(!pages.isEmpty()){
            Integer lastPage = Integer.parseInt(pages.get(0).attr(HtmlParfumsConst.PAGINATION_ATTRIBUTE.toString()));
            ExecutorService taskExecutor = Executors.newFixedThreadPool(32);
            for(int i = 2; i <= lastPage; i++) {
                final int finalI = i;
                taskExecutor.execute( () -> {
                    String paginatedCategoryLink = BASE_PARFUM_CATEGORY_LINK
                            .substring(0, 41).concat(finalI + HtmlParfumsConst.PRODUCTS_ON_PAGE.toString());
                    Document pageDoc;
                    try {
                        pageDoc = documentFactory(paginatedCategoryLink);
                    } catch (IOException ex) {
                        log.warn(HttpConnectionConst.HTTP_ERROR + paginatedCategoryLink + " " + ex);
                        return;
                    }
                    Elements blocks = pageDoc.select(HtmlParfumsConst.PRODUCT_BLOCK_SELECTOR.toString());
                    getProductSpecs(blocks, consumer);
                    log.debug("Page " + finalI + ", link " + paginatedCategoryLink);
                });
            }
            taskExecutor.shutdown();
            try {
                taskExecutor.awaitTermination(1, TimeUnit.DAYS);
            } catch (InterruptedException e) {
                log.warn("Process interrupted - not all products may be gathered: " + e);
            }
        } else {
            Elements blocks = doc.select(HtmlParfumsConst.PRODUCT_BLOCK_SELECTOR.toString());
            getProductSpecs(blocks, consumer);
        }
    }

    private void getProductSpecs(Elements names, SiteProductConsumer consumer) {
        for (Element name : names) {
            String productDescUrl = name.attr(HtmlParfumsConst.PRODUCT_BLOCK_URL_ATTR.toString());
            if(productDescUrl != null){
                Document doc;
                try{
                    doc = documentFactory(productDescUrl);
                } catch (IOException ex){
                    log.warn(HttpConnectionConst.HTTP_ERROR + productDescUrl+ " "+ ex);
                    continue;
                }
                String desctitle = doc.getElementsByClass(HtmlParfumsConst.PRODUCT_NAME_CLASS.toString()).text();
                String desc = doc.getElementById(HtmlParfumsConst.PRODUCT_DESC_ID.toString()).getElementsByTag(HtmlParfumsConst.PRODUCT_DESC_TAG.toString()).text();
                Elements specsubtitle = doc.getElementsByClass(HtmlParfumsConst.PRODUCT_SPECS_CLASS.toString());
                consumer.consume(buildProductSpecs(desctitle, desc, specsubtitle));
            }
        }
    }

    private List<String> buildProductSpecs(String desctitle, String desc, Elements specsubtitle){
        Map<String, String> specsMap = new HashMap<>();
        String pattern = "(.*):(.*)";
        Pattern p = Pattern.compile(pattern);
        for(Element e : specsubtitle) {
            Matcher m = p.matcher(e.text());
            if (m.matches()){
                specsMap.put(m.group(1).trim(), m.group(2).trim());
            }
        }
        List<String> specs = new ArrayList<>();
        specs.add(desctitle.trim());
        if(language.equals("ru")){
            specs.add(specsMap.get(TemplateParfumsConstRu.AROMATS.toString()));
            specs.add(specsMap.get(TemplateParfumsConstRu.BASE_NOTES.toString()));
            specs.add(specsMap.get(TemplateParfumsConstRu.HEART_NOTES.toString()));
            specs.add(specsMap.get(TemplateParfumsConstRu.HIGH_NOTES.toString()));
            specs.add(specsMap.get(TemplateParfumsConstRu.BRAND.toString()));
            specs.add(specsMap.get(TemplateParfumsConstRu.COUNTRY.toString()));
            specs.add(specsMap.get(TemplateParfumsConstRu.VOLUME.toString()));
            specs.add(specsMap.get(TemplateParfumsConstRu.SEX.toString()));
            specs.add(specsMap.get(TemplateParfumsConstRu.LAUNCH_DATE.toString()));
        } else {
            specs.add(specsMap.get(TemplateParfumsConstUa.AROMATS.toString()));
            specs.add(specsMap.get(TemplateParfumsConstUa.BASE_NOTES.toString()));
            specs.add(specsMap.get(TemplateParfumsConstUa.HEART_NOTES.toString()));
            specs.add(specsMap.get(TemplateParfumsConstUa.HIGH_NOTES.toString()));
            specs.add(specsMap.get(TemplateParfumsConstUa.BRAND.toString()));
            specs.add(specsMap.get(TemplateParfumsConstUa.COUNTRY.toString()));
            specs.add(specsMap.get(TemplateParfumsConstUa.VOLUME.toString()));
            specs.add(specsMap.get(TemplateParfumsConstUa.SEX.toString()));
            specs.add(specsMap.get(TemplateParfumsConstUa.LAUNCH_DATE.toString()));
        }
        specs.add(desc.trim());
        return specs;
    }
}
