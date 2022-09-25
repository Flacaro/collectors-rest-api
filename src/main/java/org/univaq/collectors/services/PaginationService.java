package org.univaq.collectors.services;

import org.springframework.data.domain.PageRequest;

public class PaginationService {
    
    public static PageRequest getPageRequestOrDefault(Integer page, Integer size) {
        if (page == null || page < 0) {
            page = 0;
        }

        if(size == null || size < 1) {
            size = 20;
        }

        return PageRequest.of(page, size);
    }

}
