package com.kdt03.fashion_api.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.kdt03.fashion_api.domain.Styles;

public interface StylesRepository extends JpaRepository<Styles, String> {
    Optional<Styles> findByStyleName(String styleName);
}
