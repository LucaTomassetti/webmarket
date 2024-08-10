/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.univaq.lucaepio.webmarket.util;

/**
 *
 * @author lucat
 */

public class PaginationUtil {
    private final int pageSize;
    private final int currentPage;
    private final long totalItems;
    private final int totalPages;

    public PaginationUtil(int pageSize, int currentPage, long totalItems) {
        this.pageSize = pageSize;
        this.totalItems = totalItems;
        this.totalPages = (int) Math.ceil((double) totalItems / pageSize);
        this.currentPage = Math.min(Math.max(1, currentPage), totalPages);
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public boolean hasNextPage() {
        return currentPage < totalPages;
    }

    public boolean hasPreviousPage() {
        return currentPage > 1;
    }

    public int getNextPage() {
        return Math.min(currentPage + 1, totalPages);
    }

    public int getPreviousPage() {
        return Math.max(currentPage - 1, 1);
    }
}
