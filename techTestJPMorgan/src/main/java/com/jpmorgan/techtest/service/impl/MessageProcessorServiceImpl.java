package com.jpmorgan.techtest.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.jpmorgan.techtest.domain.MessageType;
import com.jpmorgan.techtest.domain.Sales;
import com.jpmorgan.techtest.service.MessageProcessorService;

import flexjson.JSONDeserializer;

/**
 * @author amaestro
 *
 */
public class MessageProcessorServiceImpl implements MessageProcessorService {

	static int messagesProcessed = 0;
	static HashMap<String, List<Sales>> salesMap = new HashMap<String, List<Sales>>();
	static List<String> adjustmentLog = new ArrayList<String>();
	private boolean adjustmentApplied = false;
	
	

	/**
	 * @param msg json list format message received to process
	 * @return boolean indicates if the main process must stop
	 */
	public boolean processMessage(String msg) {
		
		List<MessageType> deserializedMsgs = deserializeMsg(msg);
		int salesAdjusted = 0;
		for (MessageType message: deserializedMsgs) {
			
			if(validateMessage(message)){
				
				messagesProcessed++;

				String productName = message.getProductName().trim();
				List<Sales> sales = salesMap.get(productName);

				if (salesMap.containsKey(productName)) {
					sales.add(message.getSale());
					

				} else {
					sales = new ArrayList<Sales>();
					sales.add(message.getSale());
					
				}
				
				salesMap.put(productName, sales);
				
				if(message.getAdjustment()!=null){
					
					switch (message.getAdjustment().getOperation()) {
					case ADD:
							for (Sales sale : sales) {
								sale.setUnitPrice(sale.getUnitPrice() + message.getAdjustment().getFactor());
								salesAdjusted++;
								adjustmentApplied = true;
							}
						break;
					case SUBTRACT:
						for (Sales sale : sales) {
							//UnitPrice must be positive
							if(sale.getUnitPrice() >  message.getAdjustment().getFactor() + 1){
								sale.setUnitPrice(sale.getUnitPrice() - message.getAdjustment().getFactor());
								salesAdjusted++;
								adjustmentApplied = true;
							}
							else{
								adjustmentLog.add("Adjustment operation " + message.getAdjustment().getOperation() + " at message number: " + messagesProcessed +
										" could not be applied because unit price of " +  message.getProductName() + " must be positive.");

							}
							
						}
						break;
					case MULTIPLY:
						for (Sales sale : sales) {
							sale.setUnitPrice(sale.getUnitPrice() * message.getAdjustment().getFactor());
							salesAdjusted++;
							adjustmentApplied = true;
						}
						break;

					default:
						break;
					}
					
					salesMap.put(productName, sales);
					
					if(adjustmentApplied)
						adjustmentLog.add("Adjustment applied at message number: " + messagesProcessed + " in " + salesAdjusted + " of " + sales.size() + 
								" sales of the product " + productName + ": unit price have changed by operation "+ message.getAdjustment().getOperation() + 
								" in " + message.getAdjustment().getFactor() + " units.");

				}
				
				if (messagesProcessed % 10 == 0) {
					
					System.out.println("   Have been recorded " + messagesProcessed + " messages, this is the resume of sales:  ");

					printSales();

				}
				
				if (messagesProcessed == 50) {
				
					System.out.println("   Have been recorded 50 messages, this is the resume of afjustements done: ");
					for (String adj : adjustmentLog)
						System.out.println("--- " + adj);
					
					System.out.println("This is the resume of total sales: ");
					printSales();
					
					return false;
				}
			}
			
		}

		return true;
	}
	
	private void printSales() {
		
		for (Entry<String, List<Sales>> entry : salesMap.entrySet()) {
		    String product = entry.getKey();
		    List<Sales> salesByProduct = entry.getValue();
		    int totalAmount = 0;
			int totalSales = 0;
			for (Sales sale : salesByProduct) {
				totalAmount = totalAmount + (sale.getUnitPrice() * sale.getOccurrences());
				totalSales = totalSales + sale.getOccurrences();
			}

			System.out.println("Product Name: |" + product +  "| Total sales: |" + totalSales + "| Total sale price: |" + totalAmount + "p |");
		}
		

	}

	public boolean validateMessage(MessageType message) {

		if (message != null) {

			if (message.getProductName() == null || message.getProductName().isEmpty()) {
				System.out.println("Product name can not be empty or null, this message will be ignored");
				return false;
			}

			if (message.getSale() != null) {
				if (!(message.getSale().getUnitPrice() >= 1)) {
					System.out.println("Unit price must be positive, this message will not be processed.");
					return false;

				}
				if (!(message.getSale().getOccurrences() >= 1)) {
					System.out.println("Sales occurrences must be positive, this message will not be processed.");
					return false;

				}

			} else {
				System.out.println("There is no sale info for this product, this message will not be processed.");
				return false;
			}

			if (message.getAdjustment() != null) {
				if (message.getAdjustment().getOperation() == null) {
					System.out.println("Adjustment operation can not be null, this message will not be processed.");
					return false;
				}
				if (!(message.getAdjustment().getFactor() >= 1)) {
					System.out.println("Adjustment factor must be positive, this message will not be processed.");
					return false;
				}
			}

		} else {
			return false;
		}

		return true;
	}

	
	public List<MessageType> deserializeMsg(String msg) {

		List<MessageType> listMsgs = new ArrayList<MessageType>();

		try {
			listMsgs = new JSONDeserializer<List<MessageType>>().use(null, ArrayList.class)
					.use("values", MessageType.class).deserialize(msg);

		} catch (Exception e) {
			System.out.println("Invalid Message format: Please, check the message sent.");
		}

		return listMsgs;
	}

}
