/**
 * The MIT License (MIT)
 * 
 * Copyright (c) 2015 Thomas Cashman
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.heartbuffer.pipette.output;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.count.CountResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Thomas Cashman
 */
public class ElasticsearchTransportOutputTest {
    private static final String INDEX = "test-index";
    private static final String TYPE = "test-type";
    
    private final String clusterName;
    
    private Node node;

    private ElasticsearchTransportOutput esOutput;
    
    public ElasticsearchTransportOutputTest() {
        clusterName = "test-cluster-" + new Random().nextInt();
    }
    
    @Before
    public void setUp() throws Exception {
        node = NodeBuilder.nodeBuilder().data(true).local(false)
                .settings(ImmutableSettings.settingsBuilder()
                        .put("path.data",  System.getProperty("java.io.tmpdir")))
                        .clusterName(clusterName).build();
        node.start();
        node.client().admin().indices().create(new CreateIndexRequest(INDEX)).get();
        
        esOutput = new ElasticsearchTransportOutput(clusterName, clusterName + "-test", "127.0.0.1", 270001);
        new Thread(esOutput).start();
    }
    
    @After
    public void teardown() throws Exception {
        esOutput.preDestroy();
        esOutput.shutdown(false);
        
        node.client().admin().indices().delete(new DeleteIndexRequest(INDEX)).get();
        node.stop();
    }

    @Test
    public void testOutputWithValidData() {
        int dataSize = 100;
        List<Map<String, Object>> data = createSampleData(dataSize);
        esOutput.queue(data);
        checkOutput(dataSize, 2000);
    }

    @Test
    public void testOutputWithMissingIndexInfomation() {
        int dataSize = 100;
        List<Map<String, Object>> data = createSampleData(dataSize, false);
        esOutput.queue(data);
        checkOutput(0, 2000);
    }

    @Test
    public void testOutputWithMissingTypeInfomation() {
        int dataSize = 100;
        List<Map<String, Object>> data = createSampleData(dataSize, true, false);
        esOutput.queue(data);
        waitFor(1000);
        checkOutput(0, 1000);
    }

    @Test
    public void testOutputWithMissingIdInfomation() {
        int dataSize = 100;
        List<Map<String, Object>> data = createSampleData(dataSize, true, true, false);
        esOutput.queue(data);
        waitFor(1000);
        checkOutput(0, 1000);
    }

    private void checkOutput(int expectedSize, long timeout) {
        Client client = node.client();

        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < timeout) {
            CountResponse response = client.prepareCount(INDEX).get();
            if (response.getCount() == expectedSize) {
                return;
            }
        }
        Assert.fail("Expected " + expectedSize + " data to be output");
    }

    private void waitFor(long duration) {
        try {
            Thread.sleep(duration);
        } catch (Exception e) {
        }
    }

    private List<Map<String, Object>> createSampleData(int dataSize) {
        return createSampleData(dataSize, true, true, true);
    }

    private List<Map<String, Object>> createSampleData(int dataSize, boolean withIndex) {
        return createSampleData(dataSize, withIndex, true, true);
    }

    private List<Map<String, Object>> createSampleData(int dataSize, boolean withIndex, boolean withType) {
        return createSampleData(dataSize, withIndex, withType, true);
    }

    private List<Map<String, Object>> createSampleData(int dataSize, boolean withIndex, boolean withType,
            boolean withId) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < dataSize; i++) {
            Map<String, Object> json = new HashMap<String, Object>();
            if (withIndex) {
                json.put("_index", INDEX);
            }
            if (withType) {
                json.put("_type", TYPE);
            }
            if (withId) {
                json.put("_id", String.valueOf(i));
            }
            json.put("value", new Integer(i));
            result.add(json);
        }
        return result;
    }
}
