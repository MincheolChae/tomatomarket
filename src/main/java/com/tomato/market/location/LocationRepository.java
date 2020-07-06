package com.tomato.market.location;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface LocationRepository extends JpaRepository<Location, Long> {
    Location findByCityAndProvinceAndUnit(String cityName, String provinceName, String unitName);
}
