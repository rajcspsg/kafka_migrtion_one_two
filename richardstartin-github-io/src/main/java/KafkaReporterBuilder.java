import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.KafkaProducer;
import java.util.concurrent.TimeUnit;

public class KafkaReporterBuilder {

    private final MetricRegistry registry;
    private final KafkaProducer<String, byte[]> producer;
    private final String topic;
    private String name = "KafkaReporter";
    private TimeUnit timeUnit = TimeUnit.MILLISECONDS;
    private TimeUnit rateUnit = TimeUnit.SECONDS;
    private ObjectMapper mapper;

    public KafkaReporterBuilder(MetricRegistry registry,
                                KafkaProducer<String, byte[]> producer,
                                String topic) {
        this.registry = registry;
        this.producer = producer;
        this.topic = topic;
    }

    public KafkaReporterBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public KafkaReporterBuilder withTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
        return this;
    }

    public KafkaReporterBuilder withRateUnit(TimeUnit rateUnit) {
        this.rateUnit = rateUnit;
        return this;
    }

    public KafkaReporterBuilder withMapper(ObjectMapper mapper) {
        this.mapper = mapper;
        return this;
    }

    public KafkaReporter build() {
        return new KafkaReporter(registry,
                name,
                MetricFilter.ALL,
                rateUnit,
                timeUnit,
                mapper == null ? new ObjectMapper() : mapper,
                topic,
                producer);
    }
}