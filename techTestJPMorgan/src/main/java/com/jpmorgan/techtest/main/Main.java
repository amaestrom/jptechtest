package com.jpmorgan.techtest.main;

import java.util.Scanner;

import com.jpmorgan.techtest.service.impl.MessageProcessorServiceImpl;

public class Main {

	public static void main(String[] args) {
		
		MessageProcessorServiceImpl msgProcessor = new MessageProcessorServiceImpl();
		boolean run = true;
		Scanner terminalInput = new Scanner(System.in);

		
		do {
			System.out.println("Please, insert new message/s, (json list format is required)");
			System.out.println();
			run = msgProcessor.processMessage(terminalInput.nextLine());
			
		} while (run);

		terminalInput.close();
	}

}
