package com.springboot.inventory;

import java.sql.SQLException;

import javax.jms.JMSException;
import javax.jms.QueueConnectionFactory;
import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import oracle.jms.AQjmsFactory;

	// @Configuration
	   
	    public class OracleAQT {
//		 private static final int load = 50000;
//		 
//		    private static final String queueName = "AQ_ADMIN.QUEUENAME";
//		    private static final String oracleAqJdbcUrl = "jdbc:oracle:thin:@grabdishi_high?TNS_ADMIN=C:\\Users\\HP\\Desktop\\wallet_grabdishi";
//		    private static final String oracleAqJdbcUser = "admin";
//		    private static final String oracleAqJdbcPassword = "MayankTayal1234";
//		    
//	        @Bean
//	        public DataSource dataSourceAq() throws SQLException {
//	            return DbHelper.createOracleDataSource(oracleAqJdbcUrl, oracleAqJdbcUser, oracleAqJdbcPassword);
//	        }
//
//	        @Bean
//	        public QueueConnectionFactory queueConnectionFactory() throws SQLException, JMSException {
//	            return AQjmsFactory.getQueueConnectionFactory(dataSourceAq());
//	        }
	    }



