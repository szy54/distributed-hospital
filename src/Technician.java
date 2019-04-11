import com.rabbitmq.client.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Technician {

    public static void main(String[] argv) throws Exception {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter two types of analysis(knee, hip, elbow):");
        String[] analysis = new String[2];
        analysis[0] = br.readLine();
        analysis[1] = br.readLine();
        boolean elbow=false, hip=false, knee=false;
        for(String type : analysis) {
            switch (type.toLowerCase()) {
                case "elbow":
                    elbow = true;
                    break;
                case "hip":
                    hip = true;
                    break;
                case "knee":
                    knee = true;
                    break;
            }
        }

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel techChannel;
        techChannel = connection.createChannel();
        String EXCHANGE_NAME = "testTechExchange1";
        techChannel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

        Channel logChannel = connection.createChannel();
        logChannel.exchangeDeclare("logExchange", BuiltinExchangeType.FANOUT);

        Consumer consumer = new DefaultConsumer(techChannel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException{
                String message = new String(body, "UTF-8");
                System.out.println(properties.getReplyTo()+" sent: "+ message);
                message+=" done";
                techChannel.basicPublish(
                        EXCHANGE_NAME,
                        properties.getReplyTo(),
                        null,
                        message.getBytes("UTF-8")
                );
                logChannel.basicPublish("logExchange", "", null, message.getBytes("UTF-8"));
                System.out.println("Responded: " + message);
            }
        };


        //elbow
        //Channel elbowChannel;
        if(elbow){
//            elbowChannel = connection.createChannel();
//            String EXCHANGE_NAME = "testelbow1";
//            elbowChannel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
//            String elbowQueueName = techChannel.queueDeclare().getQueue();
            String elbowQueueName = "elbowTechMQ";
            techChannel.queueDeclare(elbowQueueName, true, false, false,  null);
            techChannel.queueBind(elbowQueueName, EXCHANGE_NAME, "technician.elbow");
            System.out.println("Successfuly connected to elbow queue");

            techChannel.basicConsume(elbowQueueName, true, consumer);
            System.out.println("Starting to listen for elbow analysis requests");
        }

//        Channel hipChannel;
        if(hip){
//            hipChannel = connection.createChannel();
//            String EXCHANGE_NAME = "testhip1";
//            hipChannel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
//            String hipQueueName = hipChannel.queueDeclare().getQueue();
            String hipQueueName = "hipTechMQ";
            techChannel.queueDeclare(hipQueueName, true, false, false,  null);
            techChannel.queueBind(hipQueueName, EXCHANGE_NAME, "technician.hip");
            System.out.println("Successfuly connected to hip queue");
//            Consumer hipConsumer = new DefaultConsumer(hipChannel){
//                @Override
//                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException{
//                    String message = new String(body, "UTF-8");
//                    System.out.println("Received: "+ message);
//
//                    //take care of response
//                }
//            };
            techChannel.basicConsume(hipQueueName, true, consumer);
            System.out.println("Starting to listen for hip analysis requests");
        }

//        Channel kneeChannel;
        if(knee){
//            kneeChannel = connection.createChannel();
//            String EXCHANGE_NAME = "testknee1";
//            kneeChannel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

            String kneeQueueName = "kneeTechMQ";
            techChannel.queueDeclare(kneeQueueName, true, false, false,  null);
            techChannel.queueBind(kneeQueueName, EXCHANGE_NAME, "technician.knee");
            System.out.println("Successfuly connected to knee queue");
//            Consumer kneeConsumer = new DefaultConsumer(kneeChannel){
//                @Override
//                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException{
//                    String message = new String(body, "UTF-8");
//                    System.out.println("Received: "+ message);
//
//                    //take care of response
//                }
//            };
            techChannel.basicConsume(kneeQueueName, true, consumer);
            System.out.println("Starting to listen for knee analysis requests");
        }



/*        Channel channel = connection.createChannel();

        // exchange
        String EXCHANGE_NAME = "testexchange1";
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

        // queue & bind
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, EXCHANGE_NAME, "all.technician."+(elbow ? "elbow." : "no.")+(hip ? "hip." : "no.")+(knee ? "knee" : "no"));
        System.out.println("created queue: " + queueName);*/

/*        // consumer (message handling)
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println("Received: " + message);


                //odeslij zwrotke
            }
        };

        // start listening
        System.out.println("Waiting for messages...");
        channel.basicConsume(queueName, true, consumer);
        System.out.println("is it blocking or not?");*/
    }
}
