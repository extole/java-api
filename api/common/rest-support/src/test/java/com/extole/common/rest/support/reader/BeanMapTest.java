package com.extole.common.rest.support.reader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

public class BeanMapTest {

    @Test
    public void testBasicProperty() {
        BeanMap beanMap = new BeanMap();
        beanMap.setProperty("name", "value");
        assertThat(beanMap.getProperties().get("name")).isEqualTo("value");
    }

    @Test
    public void testSubProperty() {
        BeanMap beanMap = new BeanMap();
        beanMap.setProperty("data.name", "value");
        assertThat(beanMap.getSubBean("data").getProperties().get("name")).isEqualTo("value");
    }

    @Test
    public void testQuotedProperty() {
        BeanMap beanMap = new BeanMap();
        beanMap.setProperty("data.\"n.a.m.e\"", "value");
        assertThat(beanMap.getSubBean("data").getProperties().get("n.a.m.e")).isEqualTo("value");
    }

    @Test
    public void testQuotedMixProperty() {
        BeanMap beanMap = new BeanMap();
        beanMap.setProperty("data.\"n.a.m.e\".\"values\".first", "john");
        assertThat(beanMap
            .getSubBean("data")
            .getSubBean("n.a.m.e")
            .getSubBean("values")
            .getProperties().get("first")).isEqualTo("john");
    }

    @Test
    public void testQuoteInBeanName() {
        BeanMap beanMap = new BeanMap();
        beanMap.setProperty("data.openQuo\"te.value", "john");
        assertThat(beanMap
            .getSubBean("data")
            .getSubBean("openQuo\"te")
            .getProperties().get("value")).isEqualTo("john");
    }

    @Test
    public void testOpenQuoteBean() {
        BeanMap beanMap = new BeanMap();
        beanMap.setProperty("data.\"openQuote.value", "john");
        assertThat(beanMap
            .getSubBean("data")
            .getSubBean("\"openQuote")
            .getProperties().get("value")).isEqualTo("john");
    }

    @Test
    public void testClosedQuoteBean() {
        BeanMap beanMap = new BeanMap();
        beanMap.setProperty("data.closedQuote\".value", "john");
        assertThat(beanMap
            .getSubBean("data")
            .getSubBean("closedQuote\"")
            .getProperties().get("value")).isEqualTo("john");
    }

    @Test
    public void testEmptyQuoteBean() {
        BeanMap beanMap = new BeanMap();
        beanMap.setProperty("data.\"\".value", "john");
        assertThat(beanMap
            .getSubBean("data")
            .getSubBean("")
            .getProperties().get("value")).isEqualTo("john");
    }

    @Test
    public void testEmptyQuotePropertyName() {
        BeanMap beanMap = new BeanMap();
        beanMap.setProperty("data.param.\"\"", "john");
        assertThat(beanMap
            .getSubBean("data")
            .getSubBean("param")
            .getProperties().get("")).isEqualTo("john");
    }

    @Test
    public void testQuoteInPropertyName() {
        BeanMap beanMap = new BeanMap();
        beanMap.setProperty("data.openQuo\"te", "john");
        assertThat(beanMap
            .getSubBean("data")
            .getProperties().get("openQuo\"te")).isEqualTo("john");
    }

    @Test
    public void testQuotedQuoteInPropertyName() {
        BeanMap beanMap = new BeanMap();
        beanMap.setProperty("data.\"openQuo\"te\"", "john");
        assertThat(beanMap
            .getSubBean("data")
            .getProperties().get("openQuo\"te")).isEqualTo("john");
    }

    @Test
    public void testEmptyProperty() {
        BeanMap beanMap = new BeanMap();
        beanMap.setProperty("data..", "value");
        assertThat(beanMap.getSubBean("data")
            .getSubBean("").getProperties().get("")).isEqualTo("value");
    }

    @Test
    public void testErrorHandling() {
        BeanMap beanMap = new BeanMap();
        try {
            beanMap.setProperty(null, "test");
            fail("Expected error thrown");
        } catch (BeanMap.ParsePropertyError e) {
            assertThat(e.getMessage()).isEqualTo("Error setting property:null with value: test");
        }
    }
}
