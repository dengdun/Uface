package com.whzxw.uface.ether.database;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class PersonTable {

    @Id(autoincrement = true)
    Long id;

    String cardNO;

    String pseronId;

    String faceId;

    String name;


    @Generated(hash = 572319510)
    public PersonTable(Long id, String cardNO, String pseronId, String faceId,
            String name) {
        this.id = id;
        this.cardNO = cardNO;
        this.pseronId = pseronId;
        this.faceId = faceId;
        this.name = name;
    }

    @Generated(hash = 1218060513)
    public PersonTable() {
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCardNO() {
        return cardNO;
    }

    public void setCardNO(String cardNO) {
        this.cardNO = cardNO;
    }

    public String getPseronId() {
        return pseronId;
    }

    public void setPseronId(String pseronId) {
        this.pseronId = pseronId;
    }

    public String getFaceId() {
        return faceId;
    }

    public void setFaceId(String faceId) {
        this.faceId = faceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
