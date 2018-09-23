package com.edu.epam.novytska;

import com.edu.epam.novytska.consumer.impl.CsvFileOutput;
import com.edu.epam.novytska.consumer.SiteProductConsumer;
import com.edu.epam.novytska.producer.impl.ParfumsUaCrawler;
import com.edu.epam.novytska.producer.SiteProductProducer;

import java.io.*;

public class Main {


    public static void main(String[] args) throws FileNotFoundException {
        SiteProductConsumer c = new CsvFileOutput("test-parfums");
        SiteProductProducer p = new ParfumsUaCrawler();
        p.start(c);
    }
}
