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

/**
 * Copies the value from one field in each JSON object to another field in the
 * same object
 * 
 * @author Thomas Cashman
 */
public class CopyValueFilter extends Filter {
    public static final String TYPE = "copy_value";

    private final String sourceField;
    private final String targetField;
    private final boolean dropIfFieldMissing;

    public CopyValueFilter(String sourceField, String targetField, boolean dropIfFieldMissing) {
        this.sourceField = sourceField;
        this.targetField = targetField;
        this.dropIfFieldMissing = dropIfFieldMissing;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void filter(Map<String, Object> input, List<Map<String, Object>> output) {
        if (!input.containsKey(sourceField)) {
            if (dropIfFieldMissing) {
                return;
            }
            output.add(input);
            return;
        }

        input.put(targetField, input.get(sourceField));
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
