package com.edu.epam.novytska.consumer.impl;

import com.edu.epam.novytska.constants.TemplateParfumsConstRu;
import com.edu.epam.novytska.constants.TemplateParfumsConstUa;
import com.edu.epam.novytska.consumer.SiteProductConsumer;
import lombok.extern.slf4j.Slf4j;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class CsvFileOutput implements SiteProductConsumer {

    private OutputStream outputStream;

    public CsvFileOutput(String fileName) throws FileNotFoundException {
        this.outputStream = new FileOutputStream(new File(fileName+"-"+getNowDate()+".scv"), true);
    }

    @Override
    public void consume(String title, String desc, List<String> specs, String language) {
        log.debug("Product with title: "+ title);
        try {
            outputStream.write(outputBuilder(title, desc, specs, language).getBytes());
            outputStream.flush();
        } catch (IOException e) {
            StringWriter writer = new StringWriter();
            PrintWriter printWriter= new PrintWriter(writer);
            e.printStackTrace(printWriter);
            log.warn("Failed to write product:\n" + e.toString());
        }
    }

    private String outputBuilder(String title, String desc, List<String> specs, String language){
        Map<String, String> specsMap = new HashMap<>();
        String pattern = "(.*):(.*)";
        Pattern p = Pattern.compile(pattern);
        for(String e : specs) {
            Matcher m = p.matcher(e);
            if (m.matches()){
                specsMap.put(m.group(1).trim(), m.group(2).trim().replace(",", ""));
            }
        }
        String result;
        if(language.equals("ru")){
            result = title.replace(",", "") + ","
                    + specsMap.get(TemplateParfumsConstRu.AROMATS.toString()) + ","
                    + specsMap.get(TemplateParfumsConstRu.BASE_NOTES.toString())+ ","
                    + specsMap.get(TemplateParfumsConstRu.HEART_NOTES.toString())+ ","
                    + specsMap.get(TemplateParfumsConstRu.HIGH_NOTES.toString())+ ","
                    + specsMap.get(TemplateParfumsConstRu.BRAND.toString())+ ","
                    + specsMap.get(TemplateParfumsConstRu.COUNTRY.toString())+ ","
                    + specsMap.get(TemplateParfumsConstRu.VOLUME.toString())+ ","
                    + specsMap.get(TemplateParfumsConstRu.SEX.toString())+ ","
                    + specsMap.get(TemplateParfumsConstRu.LAUNCH_DATE.toString())+ ","
                    + desc.replace(",", "")+ ",\n";
        } else {
            result = title.replace(",", "") + ","
                    + specsMap.get(TemplateParfumsConstUa.AROMATS.toString()) + ","
                    + specsMap.get(TemplateParfumsConstUa.BASE_NOTES.toString())+ ","
                    + specsMap.get(TemplateParfumsConstUa.HEART_NOTES.toString())+ ","
                    + specsMap.get(TemplateParfumsConstUa.HIGH_NOTES.toString())+ ","
                    + specsMap.get(TemplateParfumsConstUa.BRAND.toString())+ ","
                    + specsMap.get(TemplateParfumsConstUa.COUNTRY.toString())+ ","
                    + specsMap.get(TemplateParfumsConstUa.VOLUME.toString())+ ","
                    + specsMap.get(TemplateParfumsConstUa.SEX.toString())+ ","
                    + specsMap.get(TemplateParfumsConstUa.LAUNCH_DATE.toString())+ ","
                    + desc.replace(",", "")+ ",\n";
        }
        return result.replace("null", "");
    }

    private String getNowDate(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDateTime.now().format(dtf);
    }
}
