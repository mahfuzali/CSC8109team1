package uk.ac.ncl.csc8109.team1.db.repository;

import uk.ac.ncl.csc8109.team1.db.model.LogEntity;

/**
 * Created by Huan on 2017/3/15.
 */
public interface LogRepository {
    /**
     *  store log
     * @param entity
     */
    void storeLog( LogEntity entity);
}
