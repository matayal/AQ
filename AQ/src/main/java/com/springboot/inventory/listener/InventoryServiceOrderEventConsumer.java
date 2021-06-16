package com.springboot.inventory.listener;

import oracle.AQ.AQException;
import oracle.jdbc.pool.OracleDataSource;
import oracle.jms.AQjmsConstants;
import oracle.jms.AQjmsConsumer;
import oracle.jms.AQjmsFactory;
import oracle.jms.AQjmsSession;
import oracle.ucp.jdbc.PoolDataSource;
import oracle.ucp.jdbc.PoolDataSourceFactory;

import javax.jms.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springboot.inventory.config.OracleAQConfiguration;
import com.springboot.inventory.controller.InventoryController;
import com.springboot.inventory.dto.InventoryTable;
import com.springboot.inventory.model.Inventory;
import com.springboot.inventory.model.Order;
import com.springboot.inventory.service.InventoryService;
import com.springboot.inventory.util.JsonUtils;
import java.lang.IllegalStateException;
import java.sql.DriverManager;
import java.sql.SQLException;

@Service
@Configuration
@Transactional
public class InventoryServiceOrderEventConsumer implements Runnable {

	@Autowired
	InventoryController inventoryController;
	
	@Autowired
	InventoryService inventoryService;

	Logger logger = LoggerFactory.getLogger(InventoryServiceOrderEventConsumer.class);

	
	public InventoryServiceOrderEventConsumer(InventoryController inventoryController) {
        this.inventoryController = inventoryController;
	}

	@Override
	public void run() {
		logger.info("Receive messages...");
		try {
			listenForOrderEvents();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void listenForOrderEvents() throws Exception {
	
		PoolDataSource poolDataSource = PoolDataSourceFactory.getPoolDataSource();


//	    dataSource.setUser("admin");
//	    dataSource.setPassword("MayankTayal1234");
//	    dataSource.setMaxConnectionReuseCount(100);
//	    dataSource.setInitialPoolSize(50);
//	    dataSource.setMinPoolSize(50);
//	    dataSource.setMaxPoolSize(100);
//	    dataSource.setValidateConnectionOnBorrow(true);
//	    dataSource.setConnectionFactoryClassName("oracle.jdbc.pool.OracleDataSource");
//		dataSource.setURL("jdbc:oracle:thin:@grabdishi_high?TNS_ADMIN=C:\\Users\\HP\\Desktop\\wallet_grabdishi");
		 poolDataSource.setConnectionFactoryClassName(OracleDataSource.class.getName());
	        poolDataSource.setConnectionPoolName("ucp");
	        poolDataSource.setUser("admin");
	        poolDataSource.setPassword("MayankTayal1234");
	        poolDataSource.setURL("jdbc:oracle:thin:@grabdishi_high?TNS_ADMIN=C:\\Users\\HP\\Desktop\\wallet_grabdishi");
	        poolDataSource.setMaxPoolSize(15);
	        poolDataSource.setMinPoolSize(0);
	        poolDataSource.setMaxConnectionReuseCount(10);
	        poolDataSource.setMaxConnectionReuseTime(5L);
	
		QueueConnectionFactory qcfact = AQjmsFactory.getQueueConnectionFactory(poolDataSource);
		QueueSession qsess = null;
		QueueConnection qconn = null;
		AQjmsConsumer consumer = null;
		qcfact.createQueueConnection("InventoryUser", "MayankTayal1234");
//logger.info(qcfact);
		try {
			if (qconn == null || qsess == null ) {
				qconn = qcfact.createQueueConnection();
				qsess = qconn.createQueueSession(true, Session.CLIENT_ACKNOWLEDGE);
				qconn.start();
				
				Queue queue = ((AQjmsSession) qsess).getQueue("INVENTORYUSER", "OrderQueue");
				consumer = (AQjmsConsumer) qsess.createConsumer(queue);
				
			}
			TextMessage orderMessage = (TextMessage) (consumer.receive(-1)); 
			String txt = orderMessage.getText();
			Order order = JsonUtils.read(txt, Order.class);
			logger.info(" orderid:" + order.getOrderid()+" "+" itemid:" + order.getItemid());

			updateDataAndSendEventOnInventory((AQjmsSession) qsess, order.getOrderid(), order.getItemid());
			qsess.commit();
			logger.info("message sent");
			
		} catch (IllegalStateException e) {
			logger.info("IllegalStateException in receiveMessages: " + e + " unrecognized message will be ignored");
			if (qsess != null)
				qsess.commit();
			
		} catch (Exception e) {
			logger.info("Error in receiveMessages: " + e);
			if (qsess != null)
				qsess.rollback();
		}
	}

	private void updateDataAndSendEventOnInventory(AQjmsSession session, String orderid, String itemid)
			throws Exception {
		String inventorylocation = evaluateInventory(session, itemid);
		Inventory inventory = new Inventory(orderid, itemid, inventorylocation, "beer"); // static suggestiveSale
		String jsonString = JsonUtils.writeValueAsString(inventory);
		
		Topic inventoryTopic = session.getTopic(inventoryController.inventoryUser, inventoryController.inventoryUser);//queue
		logger.info("send inventory status message... jsonString:" + jsonString +" "+ " inventoryTopic:" + inventoryTopic);
		
		TextMessage objmsg = session.createTextMessage();
		TopicPublisher publisher = session.createPublisher(inventoryTopic);
		objmsg.setIntProperty("Id", 1);
		objmsg.setIntProperty("Priority", 2);
		objmsg.setText(jsonString);
		objmsg.setJMSCorrelationID("" + 2);
		objmsg.setJMSPriority(2);
		
		publisher.publish(inventoryTopic, objmsg, DeliveryMode.PERSISTENT, 2, AQjmsConstants.EXPIRATION_NEVER);
	}

	
	private String evaluateInventory(AQjmsSession session, String id) throws JMSException, SQLException {
		logger.info("check inventory for inventoryid:" + id);
//		inventoryService.removeInventory(id);
//		InventoryTable getInventory=inventoryService.viewInventory(id);
		
//		if(getInventory!= null) {
//			String inventoryLocation=getInventory.getInventoryLocation();
//			logger.info("inventoryItemId: " + id+ " location:" + inventoryLocation);
//			return inventoryLocation;
//		} else {
//			logger.info("inventoryItemId: " + id+ " inventorydoesnotexist");
			return "inventorydoesnotexist";
//		}
	}

}
