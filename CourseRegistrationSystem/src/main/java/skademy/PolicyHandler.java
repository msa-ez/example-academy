package skademy;

import skademy.config.kafka.KafkaProcessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class PolicyHandler{
    
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverPaymentCompleted_수강신청완료(@Payload PaymentCompleted paymentCompleted){

        if(paymentCompleted.isMe()){
            System.out.println("##### listener 수강신청완료 : " + paymentCompleted.toJson());
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverOpenedCanceled_수강신청취소(@Payload OpenedCanceled openedCanceled){

        if(openedCanceled.isMe()){
            System.out.println("##### listener 수강신청취소 : " + openedCanceled.toJson());
        }
    }

}
