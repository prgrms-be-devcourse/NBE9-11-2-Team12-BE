package com.rungo.api.domain.marathon.marathon.dto;

import com.rungo.api.domain.marathon.marathon.entity.Marathon;
import org.springframework.data.domain.Page;

public record PageRes(
        int page,
        int size,
        long totalElements,
        int totalPages
) {
    public static PageRes from(Page<Marathon> page) {
        return new PageRes(
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}
