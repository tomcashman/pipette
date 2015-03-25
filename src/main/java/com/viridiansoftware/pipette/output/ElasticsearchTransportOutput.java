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
package com.viridiansoftware.pipette.output;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.cluster.ClusterName;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.json.simple.JSONObject;

import com.viridiansoftware.pipette.filter.ElasticsearchFilter;
import com.viridiansoftware.pipette.util.JsonObjectUtils;

/**
 *
 * @author Thomas Cashman
 */
public class ElasticsearchTransportOutput extends Output {
    public static final String TYPE = "elasticsearch_transport";
    private static final Set<String> ES_FIELDS = new HashSet<String>();

    private Node node;
    private Client client;
    
    public ElasticsearchTransportOutput(String clusterName, String nodeName, String bindHost,
            int port) {
        super();
        postConstruct(clusterName, nodeName, bindHost, port);
    }

    public ElasticsearchTransportOutput(String clusterName, String nodeName, String bindHost,
            int port, int bufferSize) {
        super(bufferSize);
        postConstruct(clusterName, nodeName, bindHost, port);
    }
    
    private void postConstruct(String clusterName, String nodeName, String bindHost,
            int port) {
        node = NodeBuilder.nodeBuilder().client(true).settings(
                ImmutableSettings.builder().put(ClusterName.SETTING, clusterName)
                .put("node.name", nodeName)
                .put("processors", Runtime.getRuntime().availableProcessors())
                // limit the number of threads created
                .put("http.enabled", false)
                .put("config.ignore_system_properties", true)).build();
        node.start();
        client = node.client();
    }

    @Override
    public void preDestroy() {
        node.stop();
    }

    @Override
    public void flush(List<JSONObject> output) throws Exception {
        BulkRequestBuilder bulkRequestBuilder = client.prepareBulk();

        for (JSONObject json : output) {
            String index = (String) json.get(ElasticsearchFilter.INDEX_FIELD);
            String type = (String) json.get(ElasticsearchFilter.TYPE_FIELD);
            String id = (String) json.get(ElasticsearchFilter.ID_FIELD);

            JsonObjectUtils.removeFields(ES_FIELDS, json);

            bulkRequestBuilder.add(client.prepareIndex(index, type, id).setSource(json.toString()));
        }
        BulkResponse response = bulkRequestBuilder.get();
        if(response.hasFailures()) {
            //TODO: Log bulk request failures
        }
    }

    @Override
    public String getType() {
        return TYPE;
    }

    static {
        ES_FIELDS.add(ElasticsearchFilter.INDEX_FIELD);
        ES_FIELDS.add(ElasticsearchFilter.TYPE_FIELD);
        ES_FIELDS.add(ElasticsearchFilter.ID_FIELD);
    }
}
