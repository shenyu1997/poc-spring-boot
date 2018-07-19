package yu.shen.pocboot.common.pagination;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

public class PageClientImpl<T> implements PageClient<T> {
    private int totalPages;

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    @Override
    public int getTotalPages() {
        return this.totalPages;
    }

    private int totalElements;

    public void setTotalElements(int totalElements) {
        this.totalElements = totalElements;
    }

    @Override
    public long getTotalElements() {
        return this.totalElements;
    }

    private int number;

    public void setNumber(int number) {
        this.number = number;
    }

    @Override
    public int getNumber() {
        return this.number;
    }

    private int size;

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public int getSize() {
        return this.size;
    }

    private int numberOfElements;

    public void setNumberOfElements(int numberOfElements) {
        this.numberOfElements = numberOfElements;
    }

    @Override
    public int getNumberOfElements() {
        return this.numberOfElements;
    }

    private List<T> content;

    public void setContent(List<T> content) {
        this.content = content;
    }

    @Override
    public List<T> getContent() {
        return this.content;
    }

    @Override
    public boolean hasContent() {
        return this.content.size() > 0;
    }

    private Sort sort;


    public void setSort(Sort sort) {
        this.sort = sort;
    }

    @Override
    public Sort getSort() {
        return this.sort;
    }

    @Override
    public boolean isFirst() {
        return false;
    }

    @Override
    public boolean isLast() {
        return false;
    }

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public boolean hasPrevious() {
        return false;
    }

    @Override
    public Pageable nextPageable() {
        return null;
    }

    @Override
    public Pageable previousPageable() {
        return null;
    }

    @Override
    public <U> Page<U> map(Function<? super T, ? extends U> function) {
        return null;
    }

    @Override
    public Iterator<T> iterator() {
        return null;
    }
}
