/**
 *
 */
package gafawork.easyfind.util;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class SearchDetail {
    private volatile Long id = null;

    private volatile String name;

    private volatile String path;

    private volatile String branch;

    private volatile String url;

    private AtomicInteger references =  new AtomicInteger();

    private ConcurrentHashMap<Long, String> lines;

    public SearchDetail(){}

    public SearchDetail(Long id, String name, String path, String branch, String url) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.branch = branch;
        this.url = url;
        this.lines = new ConcurrentHashMap<>();
    }


    public SearchDetail cloneBean()  {
        SearchDetail cloneSearchBean = new SearchDetail();

        cloneSearchBean.setId(this.getId());
        cloneSearchBean.setBranch(this.getBranch());
        cloneSearchBean.setNome(this.getNome());
        cloneSearchBean.setPath(this.getPath());
        cloneSearchBean.setUrl(this.getUrl());
        cloneSearchBean.setLines(this.getLines());
        cloneSearchBean.setReferences(this.getReferences());

        return cloneSearchBean;


    }

    public void encontrado() {
        references.incrementAndGet();
    }

    public void addLine(String linha) {
        Long id = (long) lines.size();
        lines.put(++id,linha);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return name;
    }

    public void setNome(String nome) {
        this.name = nome;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getReferences() {
        return references.get();
    }

    public void setReferences(int value) {
        this.references.set(value);
    }

    public ConcurrentHashMap<Long, String> getLines() {
        return lines;
    }

    public void setLines(ConcurrentHashMap<Long, String> linhas) {
        this.lines = linhas;
    }
}
