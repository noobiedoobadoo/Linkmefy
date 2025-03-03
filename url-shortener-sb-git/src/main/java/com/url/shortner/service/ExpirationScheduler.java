package com.url.shortner.service;

import com.url.shortner.models.UrlMapping;
import com.url.shortner.repository.UrlMappingRepository;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@AllArgsConstructor
public class ExpirationScheduler {
    private UrlMappingRepository urlMappingRepository;

    @Async
    //@Scheduled(cron = "*/5 * * * * *") // every 5 seconds
    @Scheduled(cron = "0 0 * * * *") // every 1 hour
    public void removeExpiredUrlMappings(){
        System.out.println("Removing Expired URLs");
        List<UrlMapping> urlMappings = urlMappingRepository.findAll();
        List<UrlMapping> filteredUrlMappings = urlMappings.stream()
                        .filter(x -> x.getCreatedDate().isBefore(LocalDateTime.now().minusDays(1)))
                                .toList();
        urlMappingRepository.deleteAll(filteredUrlMappings);
    }

}
