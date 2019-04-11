import com.rabbitmq.client.*;

import java.io.IOException;

public class Logger {
    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel logChannel;
        logChannel = connection.createChannel();
        String EXCHANGE_NAME = "logExchange";
        logChannel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);

        Consumer consumer = new DefaultConsumer(logChannel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println("Received: "+ message);

            }
        };
        String queueName = "adminMQ";
        logChannel.queueDeclare(queueName, true, false, false,  null);
        logChannel.queueBind(queueName, EXCHANGE_NAME, "");
        System.out.println("Successfuly connected to queue");

        logChannel.basicConsume(queueName, true, consumer);
        System.out.println("Starting to listen for log msgs");
    }
}
