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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.heartbuffer.pipette.io.Inputable;

/**
 *
 * @author Thomas Cashman
 */
public class NDiffFilterTest implements Inputable {
    private static final String INPUT_FIELD = "inputField";
    private static final String OUTPUT_FIELD = "outputField";
    
    private NDiffFilter nDiff;
    private Queue<Map<String, Object>> output;
    
    @Before
    public void setUp() {
        nDiff = new NDiffFilter(INPUT_FIELD, OUTPUT_FIELD);
        nDiff.outputTo(this);
        new Thread(nDiff).start();
        
        output = new LinkedList<Map<String, Object>>();
    }
    
    @After
    public void teardown() {
        nDiff.preDestroy();
        nDiff.shutdown(false);
    }
    
    @Test
    public void testNoOutputOnFirstValue() {
        Assert.assertEquals(0, output.size());
        Map<String, Object> input = createObjectWithValue(5);
        nDiff.queue(input);
        try {
            Thread.sleep(1000);
        } catch (Exception e) {}
        Assert.assertEquals(0, output.size());
    }
    
    @Test
    public void testPositiveOutput() {
        Assert.assertEquals(0, output.size());
        Map<String, Object> firstInput = createObjectWithValue(7);
        Map<String, Object> secondInput = createObjectWithValue(10);
        nDiff.queue(firstInput);
        nDiff.queue(secondInput);
        waitForOutput(1000);
        Assert.assertEquals(1, output.size());
        checkValue(output.poll(), 3);
    }
    
    @Test
    public void testNegativeOutput() {
        Assert.assertEquals(0, output.size());
        Map<String, Object> firstInput = createObjectWithValue(5);
        Map<String, Object> secondInput = createObjectWithValue(3);
        nDiff.queue(firstInput);
        nDiff.queue(secondInput);
        waitForOutput(1000);
        Assert.assertEquals(1, output.size());
        checkValue(output.poll(), -2);
    }

    @Override
    public void queue(List<Map<String, Object>> inputs) {
        for(Map<String, Object> input : inputs) {
            output.offer(input);
        }
    }

    @Override
    public void queue(Map<String, Object> input) {
        output.offer(input);
    }

    @Override
    public void preDestroy() {
    }

    @Override
    public void shutdown(boolean waitForCompletion) {
    }

    @Override
    public String getType() {
        return getClass().getSimpleName();
    }
    
    @SuppressWarnings("unchecked")
    private Map<String, Object> createObjectWithValue(long value) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put(INPUT_FIELD, value);
        return result;
    }
    
    private void checkValue(Map<String, Object> obj, long expectedValue) {
        Assert.assertEquals(expectedValue, obj.get(OUTPUT_FIELD));
    }
    
    private void waitForOutput(long timeout) {
        long startTime = System.currentTimeMillis();
        while(output.size() == 0 && (System.currentTimeMillis() - startTime) < timeout) {
            try {
                Thread.sleep(10);
            } catch (Exception e) {}
        }
        
        if(output.size() == 0) {
            Assert.fail("Received no output");
        }
    }
}
