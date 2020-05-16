import com.sun.jdmk.comm.HtmlAdaptorServer;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import javax.management.ObjectName;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.util.Properties;
import java.util.Random;

public class KafkaMetricsApp {

    private final static String TOPIC = "test1";
    private final static Random r = new Random();
    public static void main(String[] args) {
        try (InputStream input = KafkaMetricsApp.class.getClassLoader().getResourceAsStream("producer.properties")) {
            Properties props = new Properties();
            props.load(input);
            System.out.println(props);

            HtmlAdaptorServer server =  new HtmlAdaptorServer();
            ObjectName adapterName = new ObjectName("TechnoratiJMXAgent:name=htmladapter,port="+9999);
            server.setPort(9999);
            ManagementFactory.getPlatformMBeanServer().registerMBean(server, adapterName);
            server.start();
            ProducerConfig config = new ProducerConfig(props);
            final Producer<String, byte[]> producer = new Producer<String, byte[]>(config);

            while (true) {
                String key = Integer.toString(r.nextInt());
                byte[] value = key.getBytes();
                publish(producer, key, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void publish(Producer<String, byte[]> producer, String key, byte[] message)  {
        KeyedMessage<String, byte[]> data;
        if (key == null){
            data = new KeyedMessage<String, byte[]>(TOPIC, message);
        } else {
            data = new KeyedMessage<String, byte[]>(TOPIC, key, message);
        }
        producer.send(data);
    }
}
