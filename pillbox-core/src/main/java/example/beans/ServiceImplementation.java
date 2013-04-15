package example.beans;

public class ServiceImplementation implements Service {

    @Override
    public String service() {
        return this.getClass().getCanonicalName();
    }
}
