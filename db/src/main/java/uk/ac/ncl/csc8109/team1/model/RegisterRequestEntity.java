package uk.ac.ncl.csc8109.team1.model;

/**
 * Created by Huan on 2017/2/28.
 */
public class RegisterRequestEntity {
    private String id;
    private String publicKey;
    private String signedId;

    public RegisterRequestEntity(String id, String publicKey, String signedId) {
        this.id = id;
        this.publicKey = publicKey;
        this.signedId = signedId;
    }

    public RegisterRequestEntity(){}

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

    public String getSignedId() {
        return signedId;
    }

    public void setSignedId(String signedId) {
        this.signedId = signedId;
    }
}
