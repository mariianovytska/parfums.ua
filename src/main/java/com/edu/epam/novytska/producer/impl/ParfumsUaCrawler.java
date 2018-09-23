package com.edu.epam.novytska.producer.impl;

import com.edu.epam.novytska.constants.HttpConnectionConst;
import com.edu.epam.novytska.constants.HtmlParfumsConst;
import com.edu.epam.novytska.consumer.SiteProductConsumer;
import com.edu.epam.novytska.producer.SiteProductProducer;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;

@Slf4j
public class ParfumsUaCrawler implements SiteProductProducer {

    private static final String BASE_LINK = "https://parfums.ua";
    private String language;

    @Override
    public void start(SiteProductConsumer consumer) {
        Document doc;
        try {
            doc = documentFactory(BASE_LINK);
        } catch (IOException e) {
            log.warn("Can't open site: " + BASE_LINK);
            return;
        }
        Set<String> categories = getCategories(doc);
        getAllProducts(categories, consumer);
    }

    private Document documentFactory(String link) throws IOException {
        log.debug("Getting a connection to the link: "+ link);
        Document doc;
        try{
            //first try to access the link
            doc = Jsoup.connect(link).userAgent(HttpConnectionConst.DEFAULT_UA.toString())
                    .userAgent(HttpConnectionConst.USER_AGENT.toString()).timeout(5000).get();
        } catch (IOException ex){
            //second try to access the link - in the case of IOException again - will be handled in in the level up
            doc = Jsoup.connect(link).userAgent(HttpConnectionConst.DEFAULT_UA.toString())
                    .userAgent(HttpConnectionConst.USER_AGENT.toString()).timeout(5000).get();
        }
        return doc;
    }

    private Set<String> getCategories(Document doc) {
        this.language = doc.getElementsByTag(HtmlParfumsConst.HTML_TAG.toString()).attr(HtmlParfumsConst.HTML_LANG_ATTR.toString());
        Set<String> categoryLinks = new HashSet<>();
        Elements categories = doc.getElementsByClass(HtmlParfumsConst.CATEGORY_CLASS.toString());
        for (Element category : categories) {
            String cat = category.attr(HtmlParfumsConst.CATEGORY_HREF_ATTR.toString());
            if(cat.startsWith(HtmlParfumsConst.CATEGORY_LINK.toString())){
                if(categoryLinks.add(BASE_LINK.concat(cat))){
                    log.debug("Category link: " + cat);
                }
            }
        }
        return categoryLinks;
    }

    private void getAllProducts(Set<String> categoryLinks, SiteProductConsumer consumer) {
        for(String categoryLink: categoryLinks){
            Document doc;
            try{
                doc = documentFactory(categoryLink);
            } catch (IOException ex){
                log.warn(HttpConnectionConst.HTTP_ERROR + categoryLink);
                continue;
            }
            log.debug("Going to link: " + categoryLink);
            Elements pages = doc.getElementsByClass(HtmlParfumsConst.PAGINATION_CLASS.toString());
            if(!pages.isEmpty()){
                Integer lastPage = Integer.parseInt(pages.get(0).attr(HtmlParfumsConst.PAGINATION_ATTRIBUTE.toString()));
                for(int i = 1; i <= lastPage; i++){
                    if(i > 1){
                        String paginatedCategoryLink = categoryLink.concat(HtmlParfumsConst.PAGINATION_LINK.toString() + i);
                        try{
                            doc = documentFactory(paginatedCategoryLink);
                        } catch (IOException ex){
                            log.warn(HttpConnectionConst.HTTP_ERROR + paginatedCategoryLink);
                            continue;
                        }
                    }
                    Elements blocks = doc.select(HtmlParfumsConst.PRODUCT_BLOCK_SELECTOR.toString());
                    getProductSpecs(blocks, consumer);
                }
            } else {
                Elements blocks = doc.select(HtmlParfumsConst.PRODUCT_BLOCK_SELECTOR.toString());
                getProductSpecs(blocks, consumer);
            }
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
                    log.warn(HttpConnectionConst.HTTP_ERROR + productDescUrl);
                    continue;
                }
                String desctitle = doc.getElementsByClass(HtmlParfumsConst.PRODUCT_NAME_CLASS.toString()).text();
                String desc = doc.getElementById(HtmlParfumsConst.PRODUCT_DESC_ID.toString()).getElementsByTag(HtmlParfumsConst.PRODUCT_DESC_TAG.toString()).text();
                Elements specsubtitle = doc.getElementsByClass(HtmlParfumsConst.PRODUCT_SPECS_CLASS.toString());
                List<String> specs = new ArrayList<>();
                for(Element e : specsubtitle) {
                    specs.add(e.text());
                }
                consumer.consume(desctitle, desc, specs, language);
            }
        }
    }
}