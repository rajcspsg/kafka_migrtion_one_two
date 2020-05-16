import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import java.io.InputStream;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Main {

    private final static String TOPIC = "test1";
    private final static Random r = new Random();

    public static void main(String[] args) {
        try (InputStream input = Main.class.getClassLoader().getResourceAsStream("producer.properties")) {
            MetricRegistry registry = new MetricRegistry();
            Properties props = new Properties();
            props.load(input);
            System.out.println(props);
            KafkaProducer <String, byte[]>producer = new KafkaProducer<>(props);
            KafkaReporter reporter = new KafkaReporterBuilder(registry, producer, "topic")
                    .withMapper(new ObjectMapper())
                    .build();
            reporter.start(5, TimeUnit.SECONDS);
            while (true) {
                String key = Integer.toString(r.nextInt());
                publish(producer, key, key.getBytes());
            }
        } catch (Exception e) {

        }
    }

    public static void publish(KafkaProducer<String, byte[]> producer, String key, byte[] message) {
        ProducerRecord<String, byte[]> data = key == null ? new ProducerRecord(TOPIC, message) : new ProducerRecord(TOPIC, key, message);
        producer.send(data);
    }
}
