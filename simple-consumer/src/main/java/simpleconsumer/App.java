package simpleconsumer;

public class App
{
    public static void main( String[] args )
    {
        if (args.length != 3){
            throw new IllegalArgumentException("Please provide the following arguments: bootstrap server, consumer group ID and the topic to subscribe to.");
        }

        String bootstrapServer = args[0];
        String consumerGroupId = args[1];
        String topic = args[2];

        Consumer consumer = new Consumer(bootstrapServer, consumerGroupId, topic);
        consumer.start();
    }
}
