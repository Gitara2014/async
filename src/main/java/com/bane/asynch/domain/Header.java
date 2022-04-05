package com.bane.asynch.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.ToString;

@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class Header {

    @JsonProperty("Accept")
    String accept;
    @JsonProperty("Content-Length")
    String contentLength;
    @JsonProperty("Host")
    String host;
    @JsonProperty("User-Agent")
    String userAgent;
    @JsonProperty("X-Amzn-Trace-Id")
    String traceId;

}
