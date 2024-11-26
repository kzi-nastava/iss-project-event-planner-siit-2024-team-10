package com.ftn.iss.eventPlanner.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
public class PagedResponse<T>{
    private Collection<T> content;
    private int totalPages;
    private long totalElements;

    public PagedResponse(Collection<T> content, int totalPages, long totalElements) {
        this.content = content;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
    }
}
