package com.edu.epam.novytska.consumer.impl;

import com.edu.epam.novytska.consumer.SiteProductConsumer;
import lombok.extern.slf4j.Slf4j;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class CsvFileOutput implements SiteProductConsumer {

    private OutputStream outputStream;

    public CsvFileOutput(String fileName) throws FileNotFoundException {
        this.outputStream = new FileOutputStream(new File(fileName+"-"+getNowDate()+".csv"), true);
    }

    @Override
    public synchronized void consume(List<String> specs) {
        try {
            String result = outputBuilder(specs);
            outputStream.write(result.getBytes());
            outputStream.flush();
        } catch (IOException e) {
            StringWriter writer = new StringWriter();
            PrintWriter printWriter= new PrintWriter(writer);
            e.printStackTrace(printWriter);
            log.warn("Failed to write product:\n" + e.toString());
        }
    }

    private String outputBuilder(List<String> specs){
        String result = "";
        for(String spec: specs){
            if(spec != null){
                spec = spec.replace(",", "");
            } else {
                spec = "";
            }
            result = result.concat(spec+",");
        }
        return result.concat("\n");
    }

    private String getNowDate(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDateTime.now().format(dtf);
    }
}
