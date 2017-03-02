package uk.ac.ncl.csc8109.team1.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ncl.csc8109.team1.crypto.Base64Coder;
import uk.ac.ncl.csc8109.team1.crypto.KeyGenerator;
import uk.ac.ncl.csc8109.team1.crypto.SignUtil;
import uk.ac.ncl.csc8109.team1.model.RegisterEntity;
import uk.ac.ncl.csc8109.team1.model.RegisterRequestEntity;
import uk.ac.ncl.csc8109.team1.repository.RegisterRepository;
import uk.ac.ncl.csc8109.team1.service.RegisterService;

import java.security.KeyPair;

/**
 * Created by Huan on 2017/2/28.
 */
@Service
public class RegisterServiceImpl implements RegisterService{

    @Autowired
    private RegisterRepository registerRepository;

    @Override
    public RegisterEntity registerUser(String id) {
        if(null == id){
            throw new NullPointerException();
        }
        if("".equals(id)){
            throw new IllegalArgumentException("id could not be empty");
        }
        if(registerRepository.checkAlreadyExist(id)){
            throw new IllegalArgumentException("the id has already registered");
        }
        KeyPair keyPair = KeyGenerator.generateNewKeyPairs();
        RegisterEntity entity = new RegisterEntity(id, Base64Coder.encode(keyPair.getPublic().getEncoded()),Base64Coder.encode(keyPair.getPrivate().getEncoded()));
        if(registerRepository.registerUser(entity)){
            return entity;
        }
        return null;
    }

    @Override
    public boolean registerUser(RegisterRequestEntity entity) {
        if(null == entity.getId()){
            throw new NullPointerException();
        }
        if("".equals(entity.getId())){
            throw new IllegalArgumentException("id could not be empty");
        }
        if(registerRepository.checkAlreadyExist(entity.getId())){
            throw new IllegalArgumentException("the id has already registered");
        }

        if(checkAuth(entity.getId(),entity.getPublicKey(),entity.getSignedId())){
            if(registerRepository.registerUser(new RegisterEntity(entity.getId(),entity.getPublicKey()))) {
                return true;
            }
        }
        return false;


    }

    private boolean checkAuth(String id,String publicKey,String sign){
        byte[] s = SignUtil.unSign(Base64Coder.decode(publicKey), Base64Coder.decode(sign));
        if(id.equals(new String(s))){
            return true;
        }
        return false;
    }
}
