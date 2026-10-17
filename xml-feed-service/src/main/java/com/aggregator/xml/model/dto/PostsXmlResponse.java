package com.aggregator.xml.model.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;

@JacksonXmlRootElement(localName = "root")
public record PostsXmlResponse(
        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "row")
        List<PostXmlDto> rows
) {
}
