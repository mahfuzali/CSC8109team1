package uk.ac.ncl.csc8109.team1.tds;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import uk.ac.ncl.csc8109.team1.db.model.RegisterEntity;
import uk.ac.ncl.csc8109.team1.db.repository.RegisterRepository;
import uk.ac.ncl.csc8109.team1.db.repository.impl.RegisterRepositoryImpl;

public class tdsTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRegister() {
		String id = "111";	//Assume that this id is already existed
		String publickey = "publickey";
		 RegisterRepository registerRepository = new RegisterRepositoryImpl();
	     RegisterEntity entity = new RegisterEntity();
	     entity.setId(id);
	     entity.setPublicKey(publickey);
		tds.register(id, publickey);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testStep1() {
		String fromId = "existedfrom";	//Assume that this id is already existed
		String toId = "existedto";	//Assume that this id is already existed
		String message = "request";
		String queueName = "csc8109_1_tds_queue_20070306";
		String protocol = "CoffeySaidha";
		tds.step1(fromId, toId, message, queueName, protocol);
	}

}
