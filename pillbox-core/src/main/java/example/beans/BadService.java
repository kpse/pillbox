package example.beans;

public class BadService implements Service {
    private ServiceConsumer consumer;

    public BadService(ServiceConsumer consumer) {
        this.consumer = consumer;
    }

    @Override
    public String service() {
        return "you will never see this";
    }
}
