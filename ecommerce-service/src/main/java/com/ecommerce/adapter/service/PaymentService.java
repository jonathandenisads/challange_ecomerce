package com.ecommerce.adapter.service;

import com.ecommerce.adapter.enums.PaymentMethod;
import com.ecommerce.adapter.enums.PaymentStatus;
import com.ecommerce.adapter.model.Order;
import com.ecommerce.adapter.model.Payment;
import com.ecommerce.adapter.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    public Payment processPayment(Order order, PaymentMethod method){

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(order.getTotalAmount());
        payment.setMethod(method);
        payment.setStatus(PaymentStatus.PROCESSING);

        try{
            Thread.sleep(2000);
            payment.completePayment();

            kafkaTemplate.send("order.paid" , order.getId().toString());
        }catch (InterruptedException e){
            payment.setStatus(PaymentStatus.FAILED);
            Thread.currentThread().interrupt();
        }

        return paymentRepository.save(payment);

    }


}
