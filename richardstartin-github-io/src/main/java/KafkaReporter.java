import com.codahale.metrics.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import java.util.SortedMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class KafkaReporter extends ScheduledReporter {

    private final KafkaProducer<String, byte[]> producer;
    private final ObjectMapper mapper;
    private final String topic;

    protected KafkaReporter(MetricRegistry registry,
                            String name,
                            MetricFilter filter,
                            TimeUnit rateUnit,
                            TimeUnit durationUnit,
                            ObjectMapper mapper,
                            String topic,
                            KafkaProducer<String, byte[]> producer) {
        super(registry, name, filter, rateUnit, durationUnit);
        this.producer = producer;
        this.mapper = mapper;
        this.topic = topic;
    }

    @Override
    public void report(SortedMap<String, Gauge> gauges,
                       SortedMap<String, Counter> counters,
                       SortedMap<String, Histogram> histograms,
                       SortedMap<String, Meter> meters,
                       SortedMap<String, Timer> timers) {
        report(gauges);
        report(counters);
        report(histograms);
        report(meters);
        report(timers);
    }

    protected void report(SortedMap<String, ?> metrics) {
        metrics.entrySet()
                .stream()
                .map(kv -> toRecord(kv.getKey(), kv.getValue(), this::serialise))
                .forEach(producer::send);
    }

    private <T> ProducerRecord<String, byte[]> toRecord(String metricName, T metric, Function<T, byte[]> serialiser) {
        return new ProducerRecord<>(topic, metricName, serialiser.apply(metric));
    }

    private byte[] serialise(Object value) {
        try {
            return mapper.writeValueAsBytes(value);
        } catch(JsonProcessingException e) {
            throw new RuntimeException("Value not serialisable: " + value, e);
        }
    }
}