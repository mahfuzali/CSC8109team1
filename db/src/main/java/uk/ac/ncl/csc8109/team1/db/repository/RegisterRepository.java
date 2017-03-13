package uk.ac.ncl.csc8109.team1.db.repository;

import uk.ac.ncl.csc8109.team1.db.model.RegisterEntity;

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
     * @return <code>String/code>
     */
    String getPublicKeyById(String id);
    
    /**
     * get queue name by given id
     * @param id
     * @return
     */
    String getQueueById(String id);
}
