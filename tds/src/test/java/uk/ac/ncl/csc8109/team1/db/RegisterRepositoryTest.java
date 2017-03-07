package uk.ac.ncl.csc8109.team1.db;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ncl.csc8109.team1.db.model.FileEntity;
import uk.ac.ncl.csc8109.team1.db.model.RegisterEntity;
import uk.ac.ncl.csc8109.team1.db.repository.FileRepository;
import uk.ac.ncl.csc8109.team1.db.repository.MessageRepository;
import uk.ac.ncl.csc8109.team1.db.repository.RegisterRepository;
import uk.ac.ncl.csc8109.team1.db.repository.impl.FileRepositoryImpl;
import uk.ac.ncl.csc8109.team1.db.repository.impl.RegisterRepositoryImpl;

import java.io.*;
import java.util.UUID;

/**
 * Created by Huan on 2017/3/5.
 */
public class RegisterRepositoryTest {



    @Test
    public void testCheckAlreadyExist(){
        RegisterRepository registerRepository = new RegisterRepositoryImpl();
        RegisterEntity entity = new RegisterEntity();
        entity.setId(UUID.randomUUID().toString());
        entity.setPublicKey("examplepublic");
        registerRepository.registerUser(entity);
        Boolean result =  registerRepository.checkAlreadyExist(entity.getId());
        Assert.assertTrue(result);
    }

    @Test
    public void testResigterAndGetPublicKey(){
        RegisterRepository registerRepository = new RegisterRepositoryImpl();
        RegisterEntity entity = new RegisterEntity();
        entity.setId(UUID.randomUUID().toString());
        entity.setPublicKey("examplepublic");
        registerRepository.registerUser(entity);
        String result =  registerRepository.getPublicKeyById(entity.getId());
        Assert.assertNotNull(result);
    }
}
