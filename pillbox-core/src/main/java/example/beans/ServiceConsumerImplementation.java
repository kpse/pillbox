package example.beans;

public class ServiceConsumerImplementation implements ServiceConsumer {
    private Service service;

    public ServiceConsumerImplementation(Service service) {
        this.service = service;
    }

    @Override
    public String service() {
        return service.service();
    }
}
