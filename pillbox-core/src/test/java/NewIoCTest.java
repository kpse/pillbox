import com.tw.container.Lifecycle;
import com.tw.container.PillContainer;
import com.tw.container.exception.ComponentNotFoundException;
import com.tw.container.exception.CyclicDependencyException;
import com.tw.container.exception.MultipleConstructorsException;
import com.tw.container.exception.MultipleParametersException;
import com.tw.container.exception.MultipleSetterException;
import example.beans.BadService;
import example.beans.MultipleConstructorsService;
import example.beans.MultipleParametersServiceConsumer;
import example.beans.MultipleSetterServiceComsumer;
import example.beans.PrivateService;
import example.beans.Service;
import example.beans.ServiceConsumer;
import example.beans.ServiceConsumerImplementation;
import example.beans.ServiceImplementation;
import example.beans.SetterConsumer;
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
    public void should_be_able_to_create_instance_with_zero_constructor() throws Exception {
        pillContainer.register(Service.class, ServiceImplementation.class);

        Service service = pillContainer.get(Service.class);
        assertThat(service.service(), is(ServiceImplementation.class.getCanonicalName()));
    }

    @Test
    public void should_be_able_to_inject_service_to_constructor() throws Exception  {
        pillContainer.register(ServiceConsumer.class, ServiceConsumerImplementation.class);
        pillContainer.register(Service.class, ServiceImplementation.class);

        ServiceConsumer consumer = pillContainer.get(ServiceConsumer.class);
        assertThat(consumer.service(), is(ServiceImplementation.class.getCanonicalName()));
    }

    @Ignore
    @Test(expected = CyclicDependencyException.class)
    public void should_throw_exception_if_cyclic_dependency() throws Exception  {
        pillContainer.register(ServiceConsumer.class, ServiceConsumerImplementation.class);
        pillContainer.register(Service.class, BadService.class);

        pillContainer.get(ServiceConsumer.class);
    }

    @Test
    @Ignore
    public void should_inject_instance_to_constructor() throws Exception  {
        pillContainer.register(ServiceConsumer.class, ServiceConsumerImplementation.class);
        pillContainer.register(Service.class, PrivateService.getInstance());

        ServiceConsumer consumer = pillContainer.get(ServiceConsumer.class);
        assertThat(consumer.service(), is(PrivateService.class.getCanonicalName()));
    }

    @Test
    public void should_be_able_to_declare_service_lifecycle() throws Exception  {
        pillContainer.register(Service.class, ServiceImplementation.class, Lifecycle.Singleton);
        pillContainer.register(ServiceConsumer.class, TransientServiceConsumer.class, Lifecycle.Transient);

        TransientServiceConsumer first = (TransientServiceConsumer) pillContainer.get(ServiceConsumer.class);
        TransientServiceConsumer second = (TransientServiceConsumer) pillContainer.get(ServiceConsumer.class);

        assertThat(first, not(sameInstance(second)));
        assertThat(first.getService(), sameInstance(second.getService()));
    }

    @Test
    public void should_be_able_to_inject_service_via_setter() throws Exception  {
        pillContainer.register(Service.class, ServiceImplementation.class);
        pillContainer.register(ServiceConsumer.class, SetterConsumer.class);

        ServiceConsumer consumer = pillContainer.get(ServiceConsumer.class);
        assertThat(consumer.service(), is(ServiceImplementation.class.getCanonicalName()));
    }

    @Test
    public void should_find_service_from_parent_container() throws Exception  {
        PillContainer grandfather = PillContainer.createRawBox();
        PillContainer father = PillContainer.createFrom(grandfather);
        PillContainer son = PillContainer.createFrom(father);

        grandfather.register(Service.class, ServiceImplementation.class);
        son.register(ServiceConsumer.class, SetterConsumer.class);

        ServiceConsumer consumer = son.get(ServiceConsumer.class);
        assertThat(consumer.service(), is(ServiceImplementation.class.getCanonicalName()));
    }

    @Test(expected = ComponentNotFoundException.class)
    public void should_throw_component_not_found_exception() throws Exception  {
        pillContainer.register(ServiceConsumer.class, SetterConsumer.class);
        pillContainer.get(Service.class);
    }

    @Test(expected = MultipleConstructorsException.class)
    public void should_throw_multiple_constructors_exception_if_class_has_more_than_one_constructor() throws Exception {
        pillContainer.register(Service.class, MultipleConstructorsService.class);
        pillContainer.get(Service.class);
    }

    @Test(expected = MultipleSetterException.class)
    public void should_throw_multiple_setters_exception_if_class_has_more_than_three_setters() throws Exception {
        pillContainer.register(Service.class, ServiceImplementation.class);
        pillContainer.register(ServiceConsumer.class, MultipleSetterServiceComsumer.class);
        pillContainer.get(ServiceConsumer.class);
    }

    @Test(expected = MultipleParametersException.class)
    public void should_throw_multiple_parameters_exception_if_constructor_has_more_than_three_parameters() throws Exception{
        pillContainer.register(Service.class, ServiceImplementation.class);
        pillContainer.register(ServiceConsumer.class, MultipleParametersServiceConsumer.class);
        pillContainer.get(ServiceConsumer.class);
    }
}
