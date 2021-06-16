package com.springboot.inventory.controller;

import oracle.jdbc.pool.OracleDataSource;
import oracle.ucp.jdbc.PoolDataSource;
import oracle.ucp.jdbc.PoolDataSourceFactory;

import javax.jms.QueueConnectionFactory;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Properties;

public class DbHelper {


//    public static QueueConnectionFactory activeMqConnectionFactory(String activeMqUrl) {
//        ActiveMQConnectionFactory amcf = new ActiveMQConnectionFactory(activeMqUrl);
//        PooledConnectionFactory pcf = new PooledConnectionFactory();
//        pcf.setConnectionFactory(amcf);
//        pcf.setMaxConnections(25);
//        return pcf;
//    }

    public static DataSource createOracleUcpDataSource(String url, String username, String password) throws SQLException {
        PoolDataSource poolDataSource = PoolDataSourceFactory.getPoolDataSource();
        poolDataSource.setConnectionFactoryClassName(OracleDataSource.class.getName());
        poolDataSource.setConnectionPoolName("ucp");
        poolDataSource.setUser(username);
        poolDataSource.setPassword(password);
        poolDataSource.setURL(url);
        poolDataSource.setMaxPoolSize(15);
        poolDataSource.setMinPoolSize(0);
        poolDataSource.setMaxConnectionReuseCount(10);
        poolDataSource.setMaxConnectionReuseTime(5L);
        return poolDataSource;
    }

    public static DataSource createOracleDataSource(String url, String username, String password) throws SQLException {
        final OracleDataSource oracleDataSource = new OracleDataSource();
        oracleDataSource.setURL(url);
        oracleDataSource.setUser(username);
        oracleDataSource.setPassword(password);
        oracleDataSource.setLoginTimeout(10);
        oracleDataSource.setExplicitCachingEnabled(true);
        oracleDataSource.setConnectionCacheProperties(new Properties() {{
            put("MaxLimit", 100);
            put("InitialLimit", 10);
        }});
        return oracleDataSource;
    }
}