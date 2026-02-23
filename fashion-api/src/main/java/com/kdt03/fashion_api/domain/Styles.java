package com.kdt03.fashion_api.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Entity
@Table(name = "styles")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Styles {
    @Id
    @Column(name = "style_id")
    private String styleId;

    @Column(name = "style_name")
    private String styleName;
}
