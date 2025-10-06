package com.dashboard.dataTransferObject.page;

import lombok.Data;
import java.util.List;

@Data
public class PageRead <T> {
    public Integer totalPages;
    public Integer currentPage;
    public Integer itemsPerPage;
    public List<T> data;
}
