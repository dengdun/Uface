package com.whzxw.uface.ether.http;

/**
 * 柜子的细节
 */
public class CabinetBean {
    private String id;
    private String sarkCode;
    private String no;
    private Integer used;
    private String usable;
    private String sno;
    private String owner;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSarkCode() {
        return sarkCode;
    }

    public void setSarkCode(String sarkCode) {
        this.sarkCode = sarkCode;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public Integer getUsed() {
        return used;
    }

    public void setUsed(Integer used) {
        this.used = used;
    }

    public String getUsable() {
        return usable;
    }

    public void setUsable(String usable) {
        this.usable = usable;
    }

    public String getSno() {
        return sno;
    }

    public void setSno(String sno) {
        this.sno = sno;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
