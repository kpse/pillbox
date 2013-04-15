package example.beans;

public class PrivateService implements Service {
    public static Service getInstance() {
        return new PrivateService();
    }

    private PrivateService() {

    }

    @Override
    public String service() {
        return getClass().getCanonicalName();
    }
}
