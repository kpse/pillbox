package com.tw.container;

import org.junit.Test;

import java.net.URL;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class ContextLoaderTest {
    @Test
    public void should_load_from_file() throws Exception {
        final URL resource = getClass().getResource("test_context.yml");
        final PillContext pillContext = new ContextLoader().getContextDefinition(resource.getPath());
        assertThat(pillContext.getPill("test"), notNullValue());
    }
}
