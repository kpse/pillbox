import com.tw.container.PillBox;
import example.Aspirin;
import example.ManualMilk;
import example.Vitamin;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.net.URL;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
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

    @Test @Ignore
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

}
