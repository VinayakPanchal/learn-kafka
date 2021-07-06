package com.vinayak.java.kafka.service;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile("!local")
@Service
public class AwsSecretManagerService {
    final AWSSecretsManager client;

    public AwsSecretManagerService(){
        client = AWSSecretsManagerClientBuilder
                .standard()
                .withRegion("us-east-2")
                .build();
    }

    public String getSecret(String secretName){
        GetSecretValueRequest getSecretValueRequest = new GetSecretValueRequest()
                .withSecretId(secretName);

        GetSecretValueResult getSecretValueResult = client.getSecretValue(getSecretValueRequest);
        return getSecretValueResult.getSecretString();
    }
}
