package com.edu.epam.novytska.producer;

import com.edu.epam.novytska.consumer.SiteProductConsumer;

public interface SiteProductProducer {

    void start(SiteProductConsumer consumer);
}
