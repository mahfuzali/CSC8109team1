package uk.ac.ncl.csc8109.team1.repository;

import uk.ac.ncl.csc8109.team1.model.RegisterEntity;

/**
 * Created by Huan on 2017/2/28.
 */
public interface RegisterRepository {
    /**
     * check if user already exists
     * @param id
     * @return <code>boolean</code>
     */
    boolean checkAlreadyExist(String id);

    /**
     * register user to database
     * @return <code>boolean</code>
     */
    boolean registerUser(RegisterEntity entity);

    /**
     * get public key by given id
     * @param id
     * @return <code>byte[]</code>
     */
    byte[] getPublicKeyById(String id);
}
