package uk.ac.ncl.csc8109.team1.model;

/**
 * Created by Huan on 2017/2/28.
 */

public class RegisterEntity {
    private String id;
    private String publicKey;
    private String privateKey;
    public RegisterEntity(){}

    public RegisterEntity(String id,String publicKey, String privateKey){
        this.id = id;
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }
    public RegisterEntity(String id,String publicKey){
        this(id,publicKey,null);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }
}
