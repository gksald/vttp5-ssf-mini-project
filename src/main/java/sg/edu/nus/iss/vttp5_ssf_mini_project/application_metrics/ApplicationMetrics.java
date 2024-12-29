package sg.edu.nus.iss.vttp5_ssf_mini_project.application_metrics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Counter;


@Component
public class ApplicationMetrics {
    
    private final Counter visitCounter;
  private final Counter registrationCounter;
  private final Counter queryCounter;

  @Autowired
  public ApplicationMetrics(MeterRegistry registry) {

    visitCounter = Counter.builder("visit_counter")
        .description("Number of visits to the site")
        .register(registry);

    registrationCounter = Counter.builder("registration_counter")
        .description("Number of users registered on the site")
        .register(registry);

    queryCounter = Counter.builder("query_counter")
        .description("Number of recipe searches performed")
        .register(registry);
  }

  public void incrementVisits() {
    visitCounter.increment();
  }

  public void incrementRegistrations() {
    registrationCounter.increment();
  }

  public void incrementQueries() {
    queryCounter.increment();
  }

}
