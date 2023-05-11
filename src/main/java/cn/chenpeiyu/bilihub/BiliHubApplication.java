package cn.chenpeiyu.bilihub;

import cn.chenpeiyu.bilihub.service.websocket.WebSocketService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@EnableAsync
@EnableScheduling
public class BiliHubApplication {

    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(BiliHubApplication.class, args);
        WebSocketService.setApplicationContext(applicationContext);
    }

}
