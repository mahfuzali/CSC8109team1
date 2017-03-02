package uk.ac.ncl.csc8109.team1.repository;

import java.util.Map;
import java.util.UUID;
import uk.ac.ncl.csc8109.team1.model.FairExchangeEntity;
/**
 * Created by Huan on 2017/3/2.
 */

public interface MessageRepository {
    void storeMessage(UUID uuid, FairExchangeEntity entity);
    FairExchangeEntity getMessage(UUID uuid);
    Map<UUID,FairExchangeEntity> getUnfinishedMessage();
    void deleteMessage(UUID uuid);
    void deleteExpiredMessage();
}
