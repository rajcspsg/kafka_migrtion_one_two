import com.sun.jdmk.comm.HtmlAdaptorServer;
import com.yammer.metrics.Metrics;
import com.yammer.metrics.reporting.GraphiteReporter;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import javax.management.ObjectName;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class KafkaMetricsApp2 {

    private final static String TOPIC = "test1";
    private final static Random r = new Random();

    public static void main(String[] args) {
        try (InputStream input = KafkaMetricsApp2.class.getClassLoader().getResourceAsStream("producer.properties")) {
            Properties props = new Properties();
            props.load(input);
            System.out.println(props);

            HtmlAdaptorServer server = new HtmlAdaptorServer();
            ObjectName adapterName = new ObjectName("TechnoratiJMXAgent:name=htmladapter,port=" + 9999);
            server.setPort(9999);
            ManagementFactory.getPlatformMBeanServer().registerMBean(server, adapterName);

            server.start();
            System.out.println(props);

            final GraphiteReporter reporter = new GraphiteReporter(Metrics.defaultRegistry(), "host", 1234,"test1");
            reporter.start(2000L, TimeUnit.valueOf("SECONDS"));
            try {
                KafkaProducer<String, String> producer = new KafkaProducer<String, String>(props);
                while (true) {
                    String key = Integer.toString(r.nextInt());
                    publish(producer, key, key);
                }
            } catch (Exception e) {}
        } catch (Exception e) {

        }
    }

    public static void publish(KafkaProducer<String, String> producer, String key, String message) {
        ProducerRecord<String, String> data = key == null ? new ProducerRecord(TOPIC, message) : new ProducerRecord(TOPIC, key, message);
        producer.send(data);
    }
}
