package com.joaocarlos.emailNotification.handler;

import com.joaocarlos.core.ProductCreatedEvent;
import com.joaocarlos.emailNotification.entities.ProcessedEvent;
import com.joaocarlos.emailNotification.exceptions.NotRetryableException;
import com.joaocarlos.emailNotification.exceptions.RetryableException;
import com.joaocarlos.emailNotification.repository.ProcessedEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Component
@KafkaListener(topics = "product-created-events-topic")
public class ProductCreatedEventHandler {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private final RestTemplate restTemplate;
    private final ProcessedEventRepository processedEventRepository;

    public ProductCreatedEventHandler(RestTemplate restTemplate,
                                      ProcessedEventRepository processedEventRepository) {
        this.restTemplate = restTemplate;
        this.processedEventRepository = processedEventRepository;
    }

    @KafkaHandler
    public void handle(@Payload ProductCreatedEvent productCreatedEvent,
                       @Header("messageId") String messageId,
                       @Header(KafkaHeaders.RECEIVED_KEY) String messageKey) {
        LOGGER.info("Novo evento recebido: " + productCreatedEvent.getName());

        //Verificar se a menssagem já foi processada antes
        ProcessedEvent existingRecord = processedEventRepository.findByMessageId(messageId);
        if(existingRecord != null) {
            LOGGER.info("Encontrado id message duplicado: {}", existingRecord.getMessageId());
            return;
        }

        String requestUrl = "http://localhost:8082/200";

        try {
            ResponseEntity<String> response = restTemplate.exchange(requestUrl, HttpMethod.GET, null, String.class);

            if(response.getStatusCode().value() == HttpStatus.OK.value()){
                LOGGER.info("Resposta recebida do servico remoto: " + response.getBody());
            }
        } catch (ResourceAccessException e) {
            LOGGER.error(e.getMessage());
            throw new RetryableException(e);
        } catch (HttpServerErrorException e) {
            LOGGER.error(e.getMessage());
            throw new NotRetryableException(e);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw new NotRetryableException(e);
        }

        // Salva id message unica na tabela do banco de dados
        try {
            processedEventRepository.save(new ProcessedEvent(messageId, messageKey));
        } catch (DataIntegrityViolationException e) {
            throw new NotRetryableException(e);
        }

    }

}
