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

import java.util.List;
import java.util.Map;

/**
 *
 * @author Thomas Cashman
 */
public class ElasticsearchHttpOutput extends Output {
    public static final String TYPE = "elasticsearch_http";
    
    private int currentNode;
    
    public ElasticsearchHttpOutput(List<String> nodeUrls) {
        this(nodeUrls, DEFAULT_BUFFER_SIZE);
    }

    public ElasticsearchHttpOutput(List<String> nodeUrls, int bufferSize) {
        super(bufferSize);
    }

    @Override
    public void flush(List<Map<String, Object>> output) {
        
    }

    @Override
    public void preDestroy() {
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
