package com.springboot.inventory.controller;

import oracle.jms.AQjmsFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.PostConstruct;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.sql.DataSource;
import java.sql.SQLException;

@SpringBootApplication
@EnableTransactionManagement
@EnableJms
public class LoadGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(LoadGenerator.class);

    private static final int load = 50000;
    private static final String queueName = "AQ_ADMIN.QUEUENAME";
    private static final String oracleAqJdbcUrl = "jdbc:oracle:thin:@grabdishi_high?TNS_ADMIN=C:\\Users\\HP\\Desktop\\wallet_grabdishi";
    private static final String oracleAqJdbcUser = "admin";
    private static final String oracleAqJdbcPassword = "MayankTayal1234";
    private static final String activeProfile = Profiles.OracleAQ;

    @Autowired
    private QueueConnectionFactory queueConnectionFactory;

    /**
     * Adds a message to the queue so the demo app can process it.
     *
     * @throws SQLException
     * @throws JMSException
     */
    @PostConstruct
    public void addMessageToQueue() throws SQLException, JMSException, InterruptedException {
        QueueConnection queueConnection = queueConnectionFactory.createQueueConnection();
        QueueSession queueSession = queueConnectionFactory.createQueueConnection().createQueueSession(true, QueueSession.AUTO_ACKNOWLEDGE);
        Queue queue = queueSession.createQueue(queueName);
        QueueSender queueSender = queueSession.createSender(queue);

        LOG.info("Start sending {} messages", load);

        for (int i = 0; i < load; i++) {
            queueSender.send(queueSession.createTextMessage("message"));

            if (i % 1000 == 0) {
                LOG.info("progress... {}/{}...", i, load);
                queueSession.commit();
            }
        }

        LOG.info("progress... {}/{}...", load, load);
        queueSession.commit();

        LOG.info("Done.");

        queueSender.close();
        queueSession.close();
        queueConnection.close();

    }

    public class Profiles {
        public static final String OracleAQ = "OracleAQ";
        public static final String ActiveMQ = "ActiveMQ";
    }

    @Configuration
    static class OracleAQ {

        @Bean
        public DataSource dataSourceAq() throws SQLException {
            return DbHelper.createOracleDataSource(oracleAqJdbcUrl, oracleAqJdbcUser, oracleAqJdbcPassword);
        }

        @Bean
        public QueueConnectionFactory queueConnectionFactory() throws SQLException, JMSException {
            return AQjmsFactory.getQueueConnectionFactory(dataSourceAq());
        }
    }


}
