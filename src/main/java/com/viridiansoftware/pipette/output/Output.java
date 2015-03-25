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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.json.simple.JSONObject;

import com.viridiansoftware.pipette.io.Inputable;

/**
 *
 * @author Thomas Cashman
 */
public abstract class Output implements Inputable, Runnable {
    protected static final int DEFAULT_BUFFER_SIZE = 1024;
    
    private final int bufferSize;
    private final BlockingQueue<JSONObject> input;
    private final List<JSONObject> output;
    
    private boolean running, shutdownComplete;
    
    public Output() {
        this(DEFAULT_BUFFER_SIZE);
    }
    
    public Output(int bufferSize) {
        this.bufferSize = bufferSize;
        
        running = true;
        shutdownComplete = false;
        
        input = new LinkedBlockingQueue<JSONObject>(bufferSize);
        output = new ArrayList<JSONObject>(bufferSize);
    }

    @Override
    public void run() {
        while(running) {
            while(!input.isEmpty() && output.size() < bufferSize) {
                try {
                    output.add(input.take());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            
            if(output.size() < 1) {
                continue;
            }
            
            try {
                flush(output);
                output.clear();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        preDestroy();
        shutdownComplete = true;
    }
    
    public abstract void flush(List<JSONObject> output) throws Exception;

    @Override
    public void queue(List<JSONObject> inputs) {
        for (JSONObject item : inputs) {
            try {
                input.put(item);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void queue(JSONObject input) {
        try {
            this.input.put(input);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void shutdown(boolean waitForCompletion) {
        running = false;
        if(waitForCompletion) {
            while(!shutdownComplete) {
                try {
                    Thread.sleep(100);
                } catch (Exception e) {}
            }
        }

    }
}
