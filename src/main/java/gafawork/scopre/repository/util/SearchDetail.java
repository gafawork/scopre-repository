/**
 *
 */
package gafawork.scopre.repository.util;


import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class SearchDetail {
    private volatile Long id = null;

    private volatile String name;

    private volatile String path;

    private volatile String branch;

    private volatile String url;

    private final AtomicInteger references =  new AtomicInteger();

    private ConcurrentMap<Long, String> lines;

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

    public void found() {
        references.incrementAndGet();
    }

    public void addLine(String linha) {
        Long key = (long) lines.size();
        lines.put(++key,linha);
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

    public ConcurrentMap<Long, String> getLines() {
        return lines;
    }

    public void setLines(ConcurrentMap<Long, String> linhas) {
        this.lines = linhas;
    }
}
