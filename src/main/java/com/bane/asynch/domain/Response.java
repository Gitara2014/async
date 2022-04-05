package com.bane.asynch.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * https://github.com/ThakurPriyanka/demoHttpClient/blob/d753df657e36c94c7087ce531fd89608010c54da/src/main/java/domain/Response.java
 */

@Builder
@ToString
@Getter
@NoArgsConstructor(force = true)
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Response {
    String data;
    String origin;
    String url;
    Header headers;
}
