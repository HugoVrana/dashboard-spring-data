package com.dashboard.dataTransferObject.page;

import lombok.Data;

@Data
public class PageRequest {
    private Integer page;
    private Integer size;
    private String sort;
    private String order;
    private String search;
}
