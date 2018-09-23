package com.edu.epam.novytska.consumer;

import java.util.List;

public interface SiteProductConsumer {

    void consume(String title, String desc, List<String> specs);
}
