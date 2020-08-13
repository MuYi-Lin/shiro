package org.muyi.chapter04.queue;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class DirectReceiver {
    @RabbitListener(queues = "hello-queue")
    public void handlerl(String msg){
        System.out.println("DirectReceiver:"+msg);
    }
}
