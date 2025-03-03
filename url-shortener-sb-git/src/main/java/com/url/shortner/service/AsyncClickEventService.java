package com.url.shortner.service;

import com.url.shortner.models.ClickEvent;
import com.url.shortner.models.UrlMapping;
import com.url.shortner.repository.ClickEventRepository;
import com.url.shortner.repository.UrlMappingRepository;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
@Service
@AllArgsConstructor
public class AsyncClickEventService {
    UrlMappingRepository urlMappingRepository;
    ClickEventRepository clickEventRepository;
    @Async
    public void addClickEvent(UrlMapping urlMapping) throws InterruptedException{
        System.out.println("INITIATE UPDATE CLICKS");
        urlMapping.setClickCount(urlMapping.getClickCount()+1);
        urlMappingRepository.save(urlMapping);


        ClickEvent clickEvent = new ClickEvent();
        clickEvent.setClickDate(LocalDateTime.now());
        clickEvent.setUrlMapping(urlMapping);
        clickEventRepository.save(clickEvent);

        //Thread.sleep(2000L);
        System.out.println("COMPLETED UPDATE CLICKS");
    }
}
