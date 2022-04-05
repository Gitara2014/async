package com.bane.asynch.domain;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@ToString
@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class Request {
    String url;
    String origin;
}
