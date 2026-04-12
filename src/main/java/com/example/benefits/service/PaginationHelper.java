package com.example.benefits.service;

import com.example.benefits.dto.PaginatedResponse;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class PaginationHelper {

    public <T> PaginatedResponse<T> paginate(List<T> items, int page, int size) {
        int sanitizedPage = Math.max(page, 0);
        int sanitizedSize = Math.max(size, 1);

        int fromIndex = sanitizedPage * sanitizedSize;
        if (fromIndex >= items.size()) {
            int totalPages = items.isEmpty() ? 0 : (int) Math.ceil((double) items.size() / sanitizedSize);
            return new PaginatedResponse<>(Collections.emptyList(), sanitizedPage, sanitizedSize, items.size(), totalPages);
        }

        int toIndex = Math.min(fromIndex + sanitizedSize, items.size());
        List<T> content = items.subList(fromIndex, toIndex);
        int totalPages = (int) Math.ceil((double) items.size() / sanitizedSize);
        return new PaginatedResponse<>(content, sanitizedPage, sanitizedSize, items.size(), totalPages);
    }
}
