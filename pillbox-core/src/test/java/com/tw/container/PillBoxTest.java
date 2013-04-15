package com.tw.container;

import example.Aspirin;
import example.Vitamin;
import org.junit.Before;
import org.junit.Test;
import com.tw.container.PillBox;

import java.net.URL;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class PillBoxTest {
    private PillBox pillbox;

    @Before
    public void setUp() throws Exception {
        final URL resource = getClass().getResource("/com/tw/container/application_context.yml");
        pillbox = PillBox.loadContext(resource.getPath());
    }

    @Test
    public void should_create_pill_with_exact_type() throws Exception {
        final Aspirin aspirin = pillbox.createPill("aspirin");
        assertThat(aspirin, notNullValue());
    }

    @Test(expected = ClassCastException.class)
    public void should_report_type_error() throws Exception {
        final Vitamin vitamin = pillbox.createPill("aspirin");
    }
}
