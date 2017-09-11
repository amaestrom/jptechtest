package com.jpmorgan.techtest.tests;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.jpmorgan.techtest.domain.MessageType;
import com.jpmorgan.techtest.service.impl.MessageProcessorServiceImpl;

public class MessageProcessorUtilitiesTest {
		
	static String msg = "[{\"productName\":\"peach\",\"sale\":{\"occurrences\":10,\"unitPrice\":2}},{\"adjustment\":{\"factor\":5,\"operation\":\"MULTIPLY\"},\"productName\":\"peach\",\"sale\":{\"occurrences\":2,\"unitPrice\":2}},{\"productName\":\"melon\",\"sale\":{\"unitPrice\":2}}]";
	static String badMsg = "[{\"productName\":\"\",\"sale\":{\"occurrences\":0,\"unitPrice\":-2}}]";
	
	MessageProcessorServiceImpl msgProcesor;
	
	@Before
	public void init(){
		msgProcesor = new MessageProcessorServiceImpl();
	}

	@Test
	public void deserializingMsgtest() {

		List<MessageType> deserializeMsg = msgProcesor.deserializeMsg(msg);
		
		assertEquals(deserializeMsg.size(),3);
	
		assertTrue(	deserializeMsg.get(0).getProductName().equals("peach"));
		
		assertEquals( 2 , deserializeMsg.get(1).getSale().getUnitPrice().intValue());
		
	}
	
	@Test
	public void validatingMsgtest() {

		assertTrue(	msgProcesor.validateMessage(msgProcesor.deserializeMsg(msg).get(0)));
		
		assertFalse(msgProcesor.validateMessage(msgProcesor.deserializeMsg(badMsg).get(0)));
		
	}
	
	@Test
	public void processingMsgtest() {

		assertTrue(msgProcesor.processMessage(msg));
		
	}

}
