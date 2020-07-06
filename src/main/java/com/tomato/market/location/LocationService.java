package com.tomato.market.location;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional
@Service
public class LocationService {

    private final LocationRepository locationRepository;

    @PostConstruct
    public void initLocationData() throws IOException {
        if(locationRepository.count() == 0) {
            Resource resource = new ClassPathResource("location_data.csv");
            List<Location> locationsList = Files.readAllLines(resource.getFile().toPath(), StandardCharsets.UTF_8)
                    .stream()
                    .map(line -> {
                        String[] splitLine = line.split(",");
                        return Location.builder().city(splitLine[0]).province(splitLine[1]).unit(splitLine[2]).build();
                    }).collect(Collectors.toList());
            locationRepository.saveAll(locationsList);
        }
    }

}
