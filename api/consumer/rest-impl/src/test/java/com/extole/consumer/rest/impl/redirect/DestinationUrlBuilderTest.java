package com.extole.consumer.rest.impl.redirect;

import static com.google.common.collect.Sets.newHashSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.junit.jupiter.api.Test;

public class DestinationUrlBuilderTest {

    public void buildWithoutDestinationUrlFail() {
        assertThrows(IllegalStateException.class,
            () -> new DestinationUrlBuilder().withIncomingRequest(URI.create("google.com")).build());
    }

    public void buildWithNullDestinationUrlFail() {
        assertThrows(IllegalStateException.class,
            () -> new DestinationUrlBuilder().withDestinationUrl(null).build());
    }

    @Test
    public void buildWithoutIncomingRequest() throws Exception {
        URI uri = new DestinationUrlBuilder().withDestinationUrl(URI.create("http://google.com/path1")).build();
        assertThat(uri.toString()).isEqualTo("http://google.com/path1");
    }

    @Test
    public void buildWithoutScheme() throws Exception {
        URI uri = new DestinationUrlBuilder().withDestinationUrl(URI.create("www.google.com")).build();
        assertThat(uri.toString()).isEqualTo("http://www.google.com");
    }

    @Test
    public void buildWithIncomingScheme() throws Exception {
        URI uri = new DestinationUrlBuilder()
            .withDestinationUrl(URI.create("www.google.com"))
            .withIncomingRequest(URI.create("https://www.yahoo.com"))
            .build();
        assertThat(uri.toString()).isEqualTo("https://www.google.com");
    }

    @Test
    public void buildWithoutParams() throws Exception {
        URI uri = new DestinationUrlBuilder()
            .withDestinationUrl(URI.create("http://www.google.com#fragment1"))
            .withIncomingRequest(URI.create("http://www.yahoo.com#fragment2"))
            .build();
        assertThat(uri.toString()).isEqualTo("http://www.google.com#fragment1");
    }

    @Test
    public void buildWithIncomingFragment() throws Exception {
        URI uri = new DestinationUrlBuilder()
            .withDestinationUrl(URI.create("http://www.google.com"))
            .withIncomingRequest(URI.create("http://www.yahoo.com#fragment2"))
            .build();
        assertThat(uri.toString()).isEqualTo("http://www.google.com#fragment2");
    }

    @Test
    public void buildWithoutIncomingParameters() throws Exception {
        URI uri = new DestinationUrlBuilder()
            .withDestinationUrl(URI.create("http://www.google.com?param1=valueA&param1=valueB"))
            .withIncomingRequest(URI.create("http://www.yahoo.com"))
            .build();
        assertThat(uri.toString()).isEqualTo("http://www.google.com?param1=valueA&param1=valueB");
    }

    @Test
    public void buildWithIncomingParameters() throws Exception {
        URI uri = new DestinationUrlBuilder()
            .withDestinationUrl(URI.create("http://www.google.com"))
            .withIncomingRequest(URI.create("http://www.yahoo.com?param1=valueA&param1=valueB"))
            .build();
        assertThat(uri.toString()).isEqualTo("http://www.google.com?param1=valueA&param1=valueB");
    }

    @Test
    public void buildWithParameters() throws Exception {
        URI uri = new DestinationUrlBuilder()
            .withDestinationUrl(URI.create("http://www.google.com?param1=valueA&param1=valueB&param2=valueC"))
            .withIncomingRequest(URI.create("http://www.yahoo.com?param1=valueX&param1=valueY&param3=valueZ"))
            .build();
        List<NameValuePair> expectedParams =
            URLEncodedUtils.parse("param2=valueC&param1=valueX&param1=valueY&param3=valueZ", StandardCharsets.UTF_8);
        List<NameValuePair> actualParams = URLEncodedUtils.parse(uri.getQuery(), StandardCharsets.UTF_8);
        assertThat(actualParams.size()).isEqualTo(expectedParams.size());
        assertThat(expectedParams).containsAll(actualParams);
    }

    @Test
    public void buildWithLabels() throws Exception {
        URI uri = new DestinationUrlBuilder()
            .withDestinationUrl(URI.create("http://www.google.com?labels=labelA,labelB&labels=labelC"
                + "&required_labels=label1,label2&required_labels=label3"))
            .withIncomingRequest(URI.create("http://www.yahoo.com?labels=labelX,labelY&labels=labelZ"
                + "&required_labels=label4,label5&required_labels=label6"))
            .build();

        Set<String> expectedLabels = newHashSet("labelA", "labelB", "labelC", "labelX", "labelY", "labelZ");
        Set<String> expectedRequiredLabels = newHashSet("label1", "label2", "label3", "label4", "label5", "label6");

        List<NameValuePair> actualParams = URLEncodedUtils.parse(uri.getQuery(), StandardCharsets.UTF_8);
        assertThat(actualParams.size()).isEqualTo(2);
        NameValuePair labelsPair =
            actualParams.stream().filter(param -> param.getName().equals("labels")).findFirst().get();
        assertThat(newHashSet(StringUtils.split(labelsPair.getValue(), ","))).isEqualTo(expectedLabels);
        NameValuePair requiredLabelsPair =
            actualParams.stream().filter(param -> param.getName().equals("required_labels")).findFirst().get();
        assertThat(newHashSet(StringUtils.split(requiredLabelsPair.getValue(), ",")))
            .isEqualTo(expectedRequiredLabels);
    }

    @Test
    public void buildWithLabelsDuplicate() throws Exception {
        URI uri = new DestinationUrlBuilder()
            .withDestinationUrl(URI.create("http://www.google.com?labels=labelA,labelB&required_labels=label1,label2"))
            .withIncomingRequest(URI.create("http://www.yahoo.com?labels=labelX,labelA&required_labels=label3,label1"))
            .build();

        Set<String> expectedLabels = newHashSet("labelA", "labelB", "labelX");
        Set<String> expectedRequiredLabels = newHashSet("label1", "label2", "label3");

        List<NameValuePair> actualParams = URLEncodedUtils.parse(uri.getQuery(), StandardCharsets.UTF_8);
        assertThat(actualParams.size()).isEqualTo(2);
        NameValuePair labelsPair =
            actualParams.stream().filter(param -> param.getName().equals("labels")).findFirst().get();
        assertThat(newHashSet(StringUtils.split(labelsPair.getValue(), ","))).isEqualTo(expectedLabels);
        NameValuePair requiredLabelsPair =
            actualParams.stream().filter(param -> param.getName().equals("required_labels")).findFirst().get();
        assertThat(newHashSet(StringUtils.split(requiredLabelsPair.getValue(), ",")))
            .isEqualTo(expectedRequiredLabels);
    }

    @Test
    public void buildWithLabelsMissingValue() throws Exception {
        URI uri = new DestinationUrlBuilder()
            .withDestinationUrl(URI.create("http://www.google.com?labels=labelA&required_labels"))
            .withIncomingRequest(URI.create("http://www.yahoo.com?labels&required_labels=label1"))
            .build();

        Set<String> expectedLabels = newHashSet("labelA");
        Set<String> expectedRequiredLabels = newHashSet("label1");

        List<NameValuePair> actualParams = URLEncodedUtils.parse(uri.getQuery(), StandardCharsets.UTF_8);
        assertThat(actualParams.size()).isEqualTo(2);
        NameValuePair labelsPair =
            actualParams.stream().filter(param -> param.getName().equals("labels")).findFirst().get();
        assertThat(newHashSet(StringUtils.split(labelsPair.getValue(), ","))).isEqualTo(expectedLabels);
        NameValuePair requiredLabelsPair =
            actualParams.stream().filter(param -> param.getName().equals("required_labels")).findFirst().get();
        assertThat(newHashSet(StringUtils.split(requiredLabelsPair.getValue(), ",")))
            .isEqualTo(expectedRequiredLabels);
    }

}
