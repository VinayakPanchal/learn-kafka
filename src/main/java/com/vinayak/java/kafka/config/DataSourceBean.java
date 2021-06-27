package com.vinayak.java.kafka.config;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;

/**
 * Local database configuration.
 */
@Slf4j
@Configuration
@Profile("local")
public class DataSourceBean {
    @Value("${DATASOURCE_HOSTNAME:#{null}}")
    private String _localDataSourceUrl;

    @Value("${POSTGRES_PORT:5432}")
    private Integer _localDataSourcePort;

    private static final String LOCAL_DS_NAME = "postgres";
    private static final String LOCAL_DS_USER = "postgres";
    private static final String LOCAL_DS_PASS = "postgres";
    private static final String CONNECTION = "jdbc:postgresql://%s:%s/%s"
            + "?createDatabaseIfNotExist=true"
            + "&nullNamePatternMatchesAll=true"
            + "&useUnicode=true"
            + "&characterEncoding=UTF-8";

    /**
     * Flyway processor for managing database creation and updates.
     */
    @Bean
    public Flyway flyway()
            throws PropertyVetoException
    {
        Flyway flyway = Flyway.configure()
                .baselineOnMigrate(true)
                .dataSource(localDataSource())
                .load();

        flyway.migrate();

        return flyway;
    }

    @Bean
    public DataSource localDataSource()
            throws PropertyVetoException
    {
        ComboPooledDataSource comboPooledDataSource = new ComboPooledDataSource();
        comboPooledDataSource.setDriverClass("org.postgresql.Driver");

        String dataSourceUrl = String.format(CONNECTION, _localDataSourceUrl, _localDataSourcePort, LOCAL_DS_NAME);

        LOG.debug("Setting connection to {}", dataSourceUrl);
        comboPooledDataSource.setJdbcUrl(dataSourceUrl);
        comboPooledDataSource.setDataSourceName(LOCAL_DS_NAME);
        comboPooledDataSource.setUser(LOCAL_DS_USER);
        comboPooledDataSource.setPassword(LOCAL_DS_PASS);

        return comboPooledDataSource;
    }

}
