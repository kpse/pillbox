import com.tw.container.Lifecycle;
import com.tw.container.PillBox;
import com.tw.container.PillContainer;
import com.tw.container.exception.CyclicDependencyException;
import example.Aspirin;
import example.ManualMilk;
import example.Vitamin;

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
import java.net.URL;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

public class IoCTest {

    private PillBox pillbox;

    @Before
    public void setUp() throws Exception {
        final URL resource = this.getClass().getResource("/com/tw/container/application_context.yml");
        pillbox = PillBox.loadContext(resource.getPath());
    }

    @Test
    public void should_create_pill() throws Exception {
        final Aspirin aspirin = pillbox.createPill("aspirin");
        assertThat(aspirin, notNullValue());
    }

    @Test
    public void should_create_by_pill_name() throws Exception {
        final Vitamin pill = pillbox.createPill("vitamin");
        assertThat(pill, notNullValue());
    }

    @Test
    public void should_create_pill_with_constructor_injection() throws Exception {
        final URL resource = getClass().getResource("/com/tw/container/test_constructor_context.yml");
        pillbox = PillBox.loadContext(resource.getFile());
        final Object pill = pillbox.createPill("milk");
        assertThat(pill, notNullValue());
    }

    @Test
    public void should_create_pill_with_setter_injection() throws Exception {
        final URL resource = getClass().getResource("/com/tw/container/test_setter_context.yml");
        pillbox = PillBox.loadContext(resource.getFile());
        final ManualMilk milk = pillbox.createPill("milk");
        assertThat(milk.getProtein(), notNullValue());
        assertThat(milk.getFat(), notNullValue());
        assertThat(milk.getLinoleicAcid(), notNullValue());
    }

    @Test
    public void should_create_pill_with_singleton_scope() throws Exception {
        final URL resource = getClass().getResource("/com/tw/container/test_scope_context.yml");
        pillbox = PillBox.loadContext(resource.getFile());
        final ManualMilk milk = pillbox.createPill("milk");
        final ManualMilk milk2 = pillbox.createPill("milk");
        assertThat(milk, not(milk2));
        assertThat(milk.getProtein(), is(milk2.getProtein()));
        assertThat(milk.getFat(), not(milk2.getFat()));
        assertThat(milk.getLinoleicAcid(), not(milk2.getLinoleicAcid()));
    }

    @Test
    public void should_be_able_to_create_instance_with_zero_constructor() throws InstantiationException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, NoSuchFieldException {
        PillContainer pillContainer = PillContainer.createRawBox();
        pillContainer.register(Service.class, ServiceImplementation.class);

        Service service = pillContainer.get(Service.class);
        assertThat(service.service(), is(ServiceImplementation.class.getCanonicalName()));
    }

    @Test
    public void should_be_able_to_inject_service_to_constructor() throws InstantiationException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, NoSuchFieldException {
        PillContainer pillContainer = PillContainer.createRawBox();
        pillContainer.register(ServiceConsumer.class, ServiceConsumerImplementation.class);
        pillContainer.register(Service.class, ServiceImplementation.class);

        ServiceConsumer consumer = pillContainer.get(ServiceConsumer.class);
        assertThat(consumer.service(), is(ServiceImplementation.class.getCanonicalName()));
    }

    @Ignore
    @Test(expected = CyclicDependencyException.class)
    public void should_throw_exception_if_cyclic_dependency() throws IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchFieldException, NoSuchMethodException, ClassNotFoundException {
        PillContainer pillContainer = PillContainer.createRawBox();
        pillContainer.register(ServiceConsumer.class, ServiceConsumerImplementation.class);
        pillContainer.register(Service.class, BadService.class);

        pillContainer.get(ServiceConsumer.class);
    }

    @Test @Ignore
    public void should_inject_instance_to_constructor() throws IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchFieldException, NoSuchMethodException, ClassNotFoundException {
        PillContainer pillContainer = PillContainer.createRawBox();
        pillContainer.register(ServiceConsumer.class, ServiceConsumerImplementation.class);
        pillContainer.register(Service.class, PrivateService.getInstance());

        ServiceConsumer consumer = pillContainer.get(ServiceConsumer.class);
        assertThat(consumer.service(), is(PrivateService.class.getCanonicalName()));
    }

    @Test
    public void should_be_able_to_declare_service_lifecycle() throws IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchFieldException, NoSuchMethodException, ClassNotFoundException {
        PillContainer pillContainer = PillContainer.createRawBox();
        pillContainer.register(Service.class, ServiceImplementation.class, Lifecycle.Singleton);
        pillContainer.register(ServiceConsumer.class, TransientServiceConsumer.class, Lifecycle.Transient);

        TransientServiceConsumer first = (TransientServiceConsumer) pillContainer.get(ServiceConsumer.class);
        TransientServiceConsumer second = (TransientServiceConsumer) pillContainer.get(ServiceConsumer.class);

        assertThat(first, not(sameInstance(second)));
        assertThat(first.getService(), sameInstance(second.getService()));
    }

}
