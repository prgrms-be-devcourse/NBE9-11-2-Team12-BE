package com.rungo.api.domain.marathon.marathon.dto;

import org.springframework.data.domain.Page;

public record PageRes(
        int page,
        int size,
        long totalElements,
        int totalPages
) {
    public static PageRes from(Page<?> page) {
        return new PageRes(
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}
