package com.joaocarlos.products.service.impl;

import com.joaocarlos.core.ProductCreatedEvent;
import com.joaocarlos.products.dto.ProductDTO;
import com.joaocarlos.products.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProductServiceImpl implements ProductService {
    KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public ProductServiceImpl(KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate){
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public String create(ProductDTO product) {
        String productId = UUID.randomUUID().toString();

        //TODO: Persistir as informacoes do produto na tabela do banco de dados antes
        // de publicar o evento

        ProductCreatedEvent productCreatedEvent = new ProductCreatedEvent(
                productId,
                product.name(), product.price(), product.description(), product.quantity());

/*        //Evento Assincrona
        CompletableFuture<SendResult<String, ProductCreatedEvent>> future = kafkaTemplate.send("product-created-events-topic", productId, productCreatedEvent);

        future.whenComplete((result, excetpion) -> {
            if(excetpion != null) {
                LOGGER.error("*********** Falha no envio da mensagem: " + excetpion.getMessage());
            } else {
                LOGGER.info("*********** Mensagem envia com sucesso: " + result.getRecordMetadata());
            }
        });*/
        LOGGER.info("*********** Antes de publicar o ProductCreatedEvent");

        // Evento Sincrono
        SendResult<String, ProductCreatedEvent> result;
        try {
            result = kafkaTemplate
                    .send("product-created-events-topic", productId, productCreatedEvent)
                    .get();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new KafkaException("Erro ao enviar evento kafka");
        }

        LOGGER.info("Partição: " + result.getRecordMetadata().partition());
        LOGGER.info("Topico: " + result.getRecordMetadata().topic());
        LOGGER.info("Offset: " + result.getRecordMetadata().offset());

        LOGGER.info("*********** Retornando id produto");

        return productId;
    }
}
