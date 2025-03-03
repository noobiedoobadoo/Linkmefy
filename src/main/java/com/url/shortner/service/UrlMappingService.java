package com.url.shortner.service;

import com.url.shortner.dtos.ClickEventDto;
import com.url.shortner.dtos.UrlMappingDto;
import com.url.shortner.models.ClickEvent;
import com.url.shortner.models.UrlMapping;
import com.url.shortner.models.User;
import com.url.shortner.repository.ClickEventRepository;
import com.url.shortner.repository.UrlMappingRepository;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@Configuration
@AllArgsConstructor
public class UrlMappingService {
    private UrlMappingRepository urlMappingRepository;
    private ClickEventRepository clickEventRepository;
    private AsyncClickEventService asyncClickEventService;
    private RedisTemplate redisTemplate;

    public UrlMappingDto createShortUrl(String originalUrl, User user) {
        String shortUrl = generateShortUrl();
        UrlMapping urlMapping = new UrlMapping();
        urlMapping.setOriginalUrl(originalUrl);
        urlMapping.setShortUrl(shortUrl);
        urlMapping.setUser(user);
        urlMapping.setCreatedDate(LocalDateTime.now());
        UrlMapping saveUrlMapping = urlMappingRepository.save(urlMapping);
        return convertToDto(saveUrlMapping);
    }

    private UrlMappingDto convertToDto(UrlMapping urlMapping){

        UrlMappingDto urlMappingDto = new UrlMappingDto();
        urlMappingDto.setId(urlMapping.getId());
        urlMappingDto.setOriginalUrl(urlMapping.getOriginalUrl());
        urlMappingDto.setShortUrl(urlMapping.getShortUrl());
        urlMappingDto.setClickCount(urlMapping.getClickCount());
        urlMappingDto.setCreatedDate(urlMapping.getCreatedDate());

        return urlMappingDto;

    }

    private String generateShortUrl() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder shortUrl = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            shortUrl.append(characters.charAt(random.nextInt(characters.length())));
        }
        return shortUrl.toString();
    }

    public List<UrlMappingDto> getUrlsByUser(User user) {
        return urlMappingRepository.findByUser(user).stream()
                .map(this::convertToDto)
                .toList();
    }

    public List<ClickEventDto> getClickEventsByDate(String shortUrl, LocalDateTime start, LocalDateTime end) {
        UrlMapping urlMapping = getOriginalUrlUtil(shortUrl);
        if (urlMapping != null){
            return clickEventRepository.findByUrlMappingAndClickDateBetween(urlMapping,start,end)
                    .stream()
                    .collect(Collectors.groupingBy(click -> click.getClickDate().toLocalDate(), Collectors.counting()))
                    .entrySet().stream()
                    .map(entry -> {
                        ClickEventDto clickEventDto = new ClickEventDto();
                        clickEventDto.setClickDate(entry.getKey());
                        clickEventDto.setCount(entry.getValue());
                        return clickEventDto;
                    })
                    .collect(Collectors.toList());
        }
        return null;
    }

    public Map<LocalDate, Long> getTotalClicksByUserAndDate(User user, LocalDate start, LocalDate end) {
        List<UrlMapping> urlMappings = urlMappingRepository.findByUser(user);
        List<ClickEvent> clickEvents = clickEventRepository.findByUrlMappingInAndClickDateBetween(urlMappings,start.atStartOfDay(),end.plusDays(1).atStartOfDay());
        //List<ClickEvent> clickEvents = clickEventRepository.findByUserAndClickDateBetween(user, start.atStartOfDay(),end.plusDays(1).atStartOfDay());
        return clickEvents.stream()
                .collect(Collectors.groupingBy(click -> click.getClickDate().toLocalDate(), Collectors.counting()));
    }




    public UrlMapping getOriginalUrlUtil(String shortUrl){
        UrlMapping urlMapping = null;
        boolean redisAvail = true;
        try{
            urlMapping = (UrlMapping) redisTemplate.opsForValue().get(shortUrl);

        } catch (Exception e) {
            System.out.println("CONNECTION TO REDIS FAILED");
            redisAvail = false;
        }

        if (urlMapping == null){
            System.out.println("CACHE MISS");
            urlMapping = urlMappingRepository.findByShortUrl(shortUrl).orElse(null);
            if (redisAvail && urlMapping != null){
                redisTemplate.opsForValue().set(shortUrl,urlMapping);
            }
        }
        return urlMapping;
    }

    public UrlMapping getOriginalUrl(String shortUrl) throws InterruptedException{
        UrlMapping urlMapping = getOriginalUrlUtil(shortUrl);

        System.out.println("FETCHED URL!!");
        if (urlMapping != null){
            //update click count
            //to be done async from here

//            urlMapping.setClickCount(urlMapping.getClickCount()+1);
//            urlMappingRepository.save(urlMapping);

            //click event
            asyncClickEventService.addClickEvent(urlMapping);
        }
        return urlMapping;
    }

}
