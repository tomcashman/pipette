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
package com.viridiansoftware.pipette.filter;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.json.simple.JSONObject;

/**
 *
 * @author Thomas Cashman
 */
public class NDiffFilter extends Filter {
    public static final String TYPE = "ndiff";
    
    private final String inputField;
    private final String outputField;
    
    private AtomicLong previousValue;
    
    public NDiffFilter(String inputField, String outputField) {
        super();
        this.inputField = inputField;
        this.outputField = outputField;
    }
    
    public NDiffFilter(String inputField, String outputField, int bufferSize) {
        super(bufferSize);
        this.inputField = inputField;
        this.outputField = outputField;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void filter(JSONObject input, List<JSONObject> output) {
        Number number = (Number) input.get(inputField);
        long value = number.longValue();
        if(previousValue == null) {
            previousValue = new AtomicLong(value);
            return;
        }
        
        long diff = value - previousValue.get();
        previousValue.set(value);
        
        input.put(outputField, new Long(diff));
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
