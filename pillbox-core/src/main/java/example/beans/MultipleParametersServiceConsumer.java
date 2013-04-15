package example.beans;

public class MultipleParametersServiceConsumer implements ServiceConsumer {

    public MultipleParametersServiceConsumer(Service service1, Service service2,
                                             Service service3, Service service4) {
    }

    @Override
    public String service() {
        return null;
    }
}
