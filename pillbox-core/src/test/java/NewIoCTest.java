import com.tw.container.Lifecycle;
import com.tw.container.PillContainer;
import com.tw.container.exception.CyclicDependencyException;
import example.beans.BadService;
import example.beans.PrivateService;
import example.beans.Service;
import example.beans.ServiceConsumer;
import example.beans.ServiceConsumerImplementation;
import example.beans.ServiceImplementation;
import example.beans.TransientServiceConsumer;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

public class NewIoCTest {

    private PillContainer pillContainer;

    @Before
    public void setUp() throws Exception {
        pillContainer = PillContainer.createRawBox();
    }

    @Test
    public void should_be_able_to_create_instance_with_zero_constructor() throws InstantiationException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, NoSuchFieldException {
        pillContainer.register(Service.class, ServiceImplementation.class);

        Service service = pillContainer.get(Service.class);
        assertThat(service.service(), is(ServiceImplementation.class.getCanonicalName()));
    }

    @Test
    public void should_be_able_to_inject_service_to_constructor() throws InstantiationException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, NoSuchFieldException {
        pillContainer.register(ServiceConsumer.class, ServiceConsumerImplementation.class);
        pillContainer.register(Service.class, ServiceImplementation.class);

        ServiceConsumer consumer = pillContainer.get(ServiceConsumer.class);
        assertThat(consumer.service(), is(ServiceImplementation.class.getCanonicalName()));
    }

    @Ignore
    @Test(expected = CyclicDependencyException.class)
    public void should_throw_exception_if_cyclic_dependency() throws IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchFieldException, NoSuchMethodException, ClassNotFoundException {
        pillContainer.register(ServiceConsumer.class, ServiceConsumerImplementation.class);
        pillContainer.register(Service.class, BadService.class);

        pillContainer.get(ServiceConsumer.class);
    }

    @Test
    @Ignore
    public void should_inject_instance_to_constructor() throws IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchFieldException, NoSuchMethodException, ClassNotFoundException {
        pillContainer.register(ServiceConsumer.class, ServiceConsumerImplementation.class);
        pillContainer.register(Service.class, PrivateService.getInstance());

        ServiceConsumer consumer = pillContainer.get(ServiceConsumer.class);
        assertThat(consumer.service(), is(PrivateService.class.getCanonicalName()));
    }

    @Test
    public void should_be_able_to_declare_service_lifecycle() throws IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchFieldException, NoSuchMethodException, ClassNotFoundException {
        pillContainer.register(Service.class, ServiceImplementation.class, Lifecycle.Singleton);
        pillContainer.register(ServiceConsumer.class, TransientServiceConsumer.class, Lifecycle.Transient);

        TransientServiceConsumer first = (TransientServiceConsumer) pillContainer.get(ServiceConsumer.class);
        TransientServiceConsumer second = (TransientServiceConsumer) pillContainer.get(ServiceConsumer.class);

        assertThat(first, not(sameInstance(second)));
        assertThat(first.getService(), sameInstance(second.getService()));
    }
}
