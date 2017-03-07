package uk.ac.ncl.csc8109.team1.db.repository;

import java.util.Map;
import java.util.UUID;
import uk.ac.ncl.csc8109.team1.db.model.FairExchangeEntity;
/**
 * Created by Huan on 2017/3/2.
 */

public interface MessageRepository {
    /**
     *  store message
     * @param uuid
     * @param entity
     */
    void storeMessage(UUID uuid, FairExchangeEntity entity);

    /**
     *  get message by id
     * @param uuid
     * @return
     */
    FairExchangeEntity getMessage(UUID uuid);

    /**
     * todo
     * @return
     */
    Map<UUID,FairExchangeEntity> getUnfinishedMessage();

    /**
     * delete message by id
     * @param uuid
     */
    void deleteMessage(UUID uuid);

    /**
     * todo
     */
    void deleteExpiredMessage();
}
