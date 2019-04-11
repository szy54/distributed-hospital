import com.rabbitmq.client.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Doctor {

    public static void main(String[] argv) throws Exception {

        // get doctor name
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter Doctor name");
        String doctorID = br.readLine().toLowerCase();


        // connection & channel for analysis purpose
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // exchange for analysis purpose
        String EXCHANGE_NAME = "testTechExchange1";
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

        // queue & bind to receive results
        String queueName = channel.queueDeclare().getQueue();
        String LSTN_TOPIC = "doctor."+doctorID;
        channel.queueBind(queueName, EXCHANGE_NAME, LSTN_TOPIC);
        System.out.println("created queue: " + queueName);

        //channel for admin logging
        Channel logChanel = connection.createChannel();
        logChanel.exchangeDeclare("logExchange", BuiltinExchangeType.FANOUT);

        // consumer (message handling)
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println("Received: " + message);
            }
        };

        // start listening
        System.out.println("Waiting for messages...");
        channel.basicConsume(queueName, true, consumer);

        while (true) {

            // read msg
//            br = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Enter message: ");
            String type = br.readLine();
            String name = br.readLine();
            String message = type+" "+name;

            // break condition
            if ("exit".equals(type)) {
                break;
            }

            // publish
            channel.basicPublish(
                    EXCHANGE_NAME,
                    "technician."+type,
                    new AMQP.BasicProperties.Builder().replyTo(LSTN_TOPIC).build(),
                    message.getBytes("UTF-8")
            );
            logChanel.basicPublish("logExchange", "", null, message.getBytes("UTF-8"));
            System.out.println("Sent: " + message);
        }

        channel.close();
        connection.close();

    }
}
