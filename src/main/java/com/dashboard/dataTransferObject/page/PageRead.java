package com.dashboard.dataTransferObject.page;

import lombok.Data;
import java.util.List;

@Data
public class PageRead <T> {
    private Integer totalPages;
    private Integer currentPage;
    private Integer itemsPerPage;
    private List<T> data;
}