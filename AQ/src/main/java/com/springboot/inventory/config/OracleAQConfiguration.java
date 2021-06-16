package com.springboot.inventory.config;

import java.sql.SQLException;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jms.core.JmsTemplate;

import oracle.jms.AQjmsFactory;
import oracle.ucp.jdbc.PoolDataSource;
import oracle.ucp.jdbc.PoolDataSourceFactory;

@Configuration
public class OracleAQConfiguration {
	
	@Bean
	public DataSourceTransactionManager transactionManager(DataSource dataSource) {
		DataSourceTransactionManager manager = new DataSourceTransactionManager();
		manager.setDataSource(dataSource);
		return manager;
	}
		
	@Bean
	public ConnectionFactory connectionFactory(DataSource dataSource) throws JMSException {
		return AQjmsFactory.getQueueConnectionFactory(dataSource);
	}
		
	@Bean
	public JmsTemplate jmsTemplate(ConnectionFactory connectionFactory) {
		JmsTemplate jmsTemplate = new JmsTemplate();
		jmsTemplate.setSessionTransacted(true);
		jmsTemplate.setConnectionFactory(connectionFactory);
		return jmsTemplate;
	}

	
	@Bean
	public DataSource dataSource() throws SQLException {
		PoolDataSource dataSource = PoolDataSourceFactory.getPoolDataSource();


	    dataSource.setUser("admin");
	    dataSource.setPassword("MayankTayal1234");
	    dataSource.setMaxConnectionReuseCount(100);
	    dataSource.setInitialPoolSize(50);
	    dataSource.setMinPoolSize(50);

	    dataSource.setMaxPoolSize(100);

	    dataSource.setValidateConnectionOnBorrow(true);

	    dataSource.setConnectionFactoryClassName("oracle.jdbc.pool.OracleDataSource");

		dataSource.setURL("jdbc:oracle:thin:@grabdishi_high?TNS_ADMIN=C:\\Users\\HP\\Desktop\\wallet_grabdishi");
//		dataSource.setFastConnectionFailoverEnabled(true);
//		dataSource.setInitialPoolSize(5);
//		dataSource.setMinPoolSize(5);
//		dataSource.setMaxPoolSize(10);
		return dataSource;
	}
}
//	
////	@Bean
////	   public DataSource dataSource1() {
////	       OracleDataSource dataSource = null;
////	       try {
////	           dataSource = new OracleDataSource();
////	           Properties props = new Properties();
////	           String oracle_net_wallet_location = 
////	           System.getProperty("oracle.net.wallet_location");
////	           props.put("oracle.net.wallet_location", "(source=(method=file)(method_data=(directory="+oracle_net_wallet_location+")))");
////	           dataSource.setConnectionProperties(props);
////	           dataSource.setURL(url);
////	       } catch(Exception e) {
////	           e.printStackTrace();
////	       }
////	       return dataSource;
////	   }
//	 @Bean
//	    public DataSourceTransactionManager transactionManager(DataSource dataSource) {
//	        DataSourceTransactionManager manager = new DataSourceTransactionManager();
//	        manager.setDataSource(dataSource);
//	        return manager;
//	    }
//
//	    @Bean
//	    public ConnectionFactory connectionFactory(DataSource dataSource) throws JMSException {
//	        return AQjmsFactory.getQueueConnectionFactory(dataSource);
//	    }
//
//	    @Bean
//	    public JmsTemplate jmsTemplate(ConnectionFactory connectionFactory) {
//	        JmsTemplate jmsTemplate = new JmsTemplate();
//	        jmsTemplate.setSessionTransacted(true);
//	        jmsTemplate.setConnectionFactory(connectionFactory);
//	        return jmsTemplate;
//	    }
//}
