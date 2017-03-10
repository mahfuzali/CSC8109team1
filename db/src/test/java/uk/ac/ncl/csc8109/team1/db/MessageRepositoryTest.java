package uk.ac.ncl.csc8109.team1.db;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ncl.csc8109.team1.db.model.FairExchangeEntity;
import uk.ac.ncl.csc8109.team1.db.repository.MessageRepository;
import uk.ac.ncl.csc8109.team1.db.repository.impl.MessageRepositoryImpl;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Zzz on 2017/3/5.
 */
public class MessageRepositoryTest {
    @Test
    public void testAddAndGetMessage() {
        MessageRepository messageRepository = new MessageRepositoryImpl();
        FairExchangeEntity fairExchangeEntity = generateFairExchangeEntity();
        messageRepository.storeMessage(UUID.fromString(fairExchangeEntity.getUuid()), fairExchangeEntity);
        FairExchangeEntity message = messageRepository.getMessage(UUID.fromString(fairExchangeEntity.getUuid()));
        Assert.assertNotNull(message);
    }
    @Test
    public void testDeleteMessage() {
        MessageRepository messageRepository = new MessageRepositoryImpl();
        FairExchangeEntity fairExchangeEntity = generateFairExchangeEntity();
        messageRepository.storeMessage(UUID.fromString(fairExchangeEntity.getUuid()), fairExchangeEntity);
        messageRepository.deleteMessage(UUID.fromString(fairExchangeEntity.getUuid()));
        FairExchangeEntity oo = messageRepository.getMessage(UUID.fromString(fairExchangeEntity.getUuid()));
        Assert.assertNull(oo);

    }
    private FairExchangeEntity generateFairExchangeEntity(){
        UUID id = UUID.randomUUID();
        String toId = "huanhuan";
        String fromId = "huan";
        Date d = new Date();
        Long time = d.getTime();
        FairExchangeEntity fairExchangeEntity = new FairExchangeEntity();
        fairExchangeEntity.setUuid(id.toString());
        fairExchangeEntity.setToID(toId);
        fairExchangeEntity.setFromID(fromId);
        fairExchangeEntity.setTimestamp(time);
        return fairExchangeEntity;
    }

}
