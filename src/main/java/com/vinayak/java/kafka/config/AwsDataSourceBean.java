package com.vinayak.java.kafka.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.vinayak.java.kafka.service.AwsSecretManagerService;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@Profile("!local")
public class AwsDataSourceBean {
    @Autowired
    private AwsSecretManagerService _secretService;
    final String SECRETS_MANAGER_ID = "learn-kafka-db-creds";

    private static final String CONNECTION = "jdbc:postgresql://%s:%s/%s"
            + "?createDatabaseIfNotExist=true"
            + "&nullNamePatternMatchesAll=true"
            + "&useUnicode=true"
            + "&characterEncoding=UTF-8";
    private static final int DATA_SOURCE_MIN_POOL_SIZE = 10;
    private static final int DATA_SOURCE_MAX_POOL_SIZE = 200;
    private static final int MAX_IDLE_TIME_IN_SECONDS  = 30;
    private static final String POSTGRES_DRIVER = "org.postgresql.Driver";
    private static final String HOST_KEY = "host";
    private static final String PORT_KEY = "port";
    private static final String DB_NAME_KEY = "dbname";
    private static final String USERNAME_KEY = "username";
    @SuppressWarnings("squid:S2068")
    private static final String PASSWORD_KEY = "password";

    /**
     * Flyway datasource for managing the database creation and updates. Datasource creation with write permissions
     * happens only when there is a valid read policy available to retrieve the master user credentials. This will be
     * the case during the deployment. After the preconfigured expiration time, if AWS spins off a new task, it won't
     * have access to the master user credentials. At that time, flyway datasource will use the app credentials.
     *
     * @return Flyway flyway bean
     */
    @Bean(name = "flyway")
    public Flyway flyway() throws Exception {
        LOG.info("Setting up Flyway DataSource with credentials...");

        return Flyway.configure()
                .baselineOnMigrate(true)
                .dataSource(createDataSource(getSecretsMap()))
                .load();
    }

    /**
     * Datasource created using the secret map
     *
     * @return DataSource Datasource for connection to aurora postgres
     */
    private DataSource createDataSource(Map<String, String> secretMap) throws Exception {
        ComboPooledDataSource comboPooledDataSource = new ComboPooledDataSource();

        try
        {
            comboPooledDataSource.setDriverClass(POSTGRES_DRIVER);
        }
        catch (PropertyVetoException e)
        {
            throw new Exception("Error setting driver class");
        }

        String connectionUrl = String.format(CONNECTION,
                secretMap.get(HOST_KEY),
                secretMap.get(PORT_KEY),
                secretMap.get(DB_NAME_KEY));

        LOG.info("Setting connection to {}", connectionUrl);
        comboPooledDataSource.setJdbcUrl(connectionUrl);
        comboPooledDataSource.setUser(secretMap.get(USERNAME_KEY));
        comboPooledDataSource.setPassword(secretMap.get(PASSWORD_KEY));
        comboPooledDataSource.setInitialPoolSize(DATA_SOURCE_MIN_POOL_SIZE);
        comboPooledDataSource.setMinPoolSize(DATA_SOURCE_MIN_POOL_SIZE);
        comboPooledDataSource.setMaxPoolSize(DATA_SOURCE_MAX_POOL_SIZE);
        comboPooledDataSource.setNumHelperThreads(DATA_SOURCE_MIN_POOL_SIZE);
        comboPooledDataSource.setMaxIdleTime(MAX_IDLE_TIME_IN_SECONDS);
        comboPooledDataSource.setAutoCommitOnClose(true);
        return comboPooledDataSource;
    }

    private Map<String, String> getSecretsMap(){
        Map<String, String> secretsMap = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode secretsJson = null;
        try {
            secretsJson = objectMapper.readTree(_secretService.getSecret(SECRETS_MANAGER_ID));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        secretsMap.put(HOST_KEY,        secretsJson.get(HOST_KEY).textValue());
        secretsMap.put(USERNAME_KEY,    secretsJson.get(USERNAME_KEY).textValue());
        secretsMap.put(PASSWORD_KEY,    secretsJson.get(PASSWORD_KEY).textValue());
        secretsMap.put(PORT_KEY,        secretsJson.get(PORT_KEY).textValue());
        secretsMap.put(DB_NAME_KEY,     secretsJson.get(DB_NAME_KEY).textValue());
        return secretsMap;
    }
}
