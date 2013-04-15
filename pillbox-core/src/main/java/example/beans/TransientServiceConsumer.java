package example.beans;

public class TransientServiceConsumer implements ServiceConsumer {
    private Service service;

    public TransientServiceConsumer(Service service) {
        this.service = service;
    }

    public Service getService() {
        return service;
    }

    @Override
    public String service() {
        return service.service();
    }
}
