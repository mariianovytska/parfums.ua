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
        this.outputStream = new FileOutputStream(new File(fileName+"-"+getNowDate()+".csv"), true);
    }

    @Override
    public void consume(String title, String desc, List<String> specs) {
        log.debug("Product with title: "+ title);
        try {
            outputStream.write(outputBuilder(title, desc, specs).getBytes());
            outputStream.flush();
        } catch (IOException e) {
            StringWriter writer = new StringWriter();
            PrintWriter printWriter= new PrintWriter(writer);
            e.printStackTrace(printWriter);
            log.warn("Failed to write product:\n" + e.toString());
        }
    }

    private String outputBuilder(String title, String desc, List<String> specs){
        String result = title.replace(",", "").concat(",");
        for(String spec: specs){
            if(spec != null){
                spec = spec.replace(",", "");
            } else {
                spec = "";
            }
            result = result.concat(spec+",");
        }
        String descCsv = desc.replace(",", "");
        return result.concat(descCsv+"\n");
    }

    private String getNowDate(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDateTime.now().format(dtf);
    }
}
