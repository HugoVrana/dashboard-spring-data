package com.dashboard.dataTransferObject.page;

import lombok.Data;

@Data
public class PageRequest {
    public Integer page;
    public Integer size;
    public String sort;
    public String order;
    public String search;
}
