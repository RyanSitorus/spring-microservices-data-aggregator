package com.aggregator.xml.model.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public record PostXmlDto(
        @JacksonXmlProperty(localName = "userId")
        Long userId,

        @JacksonXmlProperty(localName = "id")
        Long id,

        @JacksonXmlProperty(localName = "title")
        String title,

        @JacksonXmlProperty(localName = "body")
        String body
) {
}
