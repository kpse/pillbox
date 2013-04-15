package example.beans;

public class MultipleSetterServiceComsumer implements ServiceConsumer {
    private Service service1;
    private Service service2;
    private Service service3;

    @Override
    public String service() {
        return null;
    }

    public void setService1(Service service1) {
        this.service1 = service1;
    }

    public void setService2(Service service2) {
        this.service2 = service2;
    }

    public void setService3(Service service3) {
        this.service3 = service3;
    }
}
