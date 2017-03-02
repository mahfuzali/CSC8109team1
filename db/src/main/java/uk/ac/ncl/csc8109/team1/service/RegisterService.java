package uk.ac.ncl.csc8109.team1.service;

import uk.ac.ncl.csc8109.team1.model.RegisterEntity;
import uk.ac.ncl.csc8109.team1.model.RegisterRequestEntity;

/**
 * Created by Huan on 2017/2/28.
 */
public interface RegisterService {
    /**
     *  register user generate key pairs and store to database
     * @param id
     * @return <code>RegisterEntity</code>
     */
    RegisterEntity registerUser(String id);

    /**
     * register user evaluate public key and store to database
     * @param entity
     * @return <code>boolean</code>
     */
    boolean registerUser(RegisterRequestEntity entity);
}
