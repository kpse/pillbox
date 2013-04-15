package example.beans;

public class SetterConsumer implements ServiceConsumer {
    private Service service;

    public void setService(Service service) {
        this.service = service;
    }

    @Override
    public String service() {
        return service.service();
    }
}
