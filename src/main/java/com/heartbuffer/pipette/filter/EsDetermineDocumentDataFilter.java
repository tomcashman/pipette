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
package com.heartbuffer.pipette.filter;

import java.util.List;
import java.util.Map;

import com.heartbuffer.pipette.output.ElasticsearchHttpOutput;
import com.heartbuffer.pipette.output.ElasticsearchTransportOutput;

/**
 * Applies _index, _type and _id fields to a JSON object so that it can be sent to
 * Elasticsearch by a {@link ElasticsearchHttpOutput} or {@link ElasticsearchTransportOutput}
 * 
 * The values for the fields are extracted from an existing field in the JSON object
 * 
 * @author Thomas Cashman
 */
public class EsDetermineDocumentDataFilter extends Filter {
    public static final String TYPE = "elasticsearch_determine_doc_data";
    
    public static final String INDEX_FIELD = "_index";
    public static final String TYPE_FIELD = "_type";
    public static final String ID_FIELD = "_id";
    
    private final String indexSourceField;
    private final String typeSourceField;
    private final String idSourceField;
    
    public EsDetermineDocumentDataFilter(String indexSourceField, String typeSourceField, String idSourceField) {
        super();
        this.indexSourceField = indexSourceField;
        this.typeSourceField = typeSourceField;
        this.idSourceField = idSourceField;
    }
    
    public EsDetermineDocumentDataFilter(String indexSourceField, String typeSourceField, String idSourceField, int bufferSize) {
        super(bufferSize);
        this.indexSourceField = indexSourceField;
        this.typeSourceField = typeSourceField;
        this.idSourceField = idSourceField;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void filter(Map<String, Object> input, List<Map<String, Object>> output) {
        String index = (String) input.get(indexSourceField);
        String type = (String) input.get(typeSourceField);
        String id = (String) input.get(idSourceField);
        
        input.put(INDEX_FIELD, index);
        input.put(TYPE_FIELD, type);
        input.put(ID_FIELD, id);
        output.add(input);
    }

    @Override
    public void preDestroy() {
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
