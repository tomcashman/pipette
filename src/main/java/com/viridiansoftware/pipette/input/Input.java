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
package com.viridiansoftware.pipette.input;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

import com.viridiansoftware.pipette.io.Inputable;
import com.viridiansoftware.pipette.io.Outputable;

/**
 *
 * @author Thomas Cashman
 */
public abstract class Input implements Outputable, Runnable {    
    private final List<JSONObject> output;
    private final List<Inputable> outputTo;
    
    private boolean running, shutdownComplete;
    
    public Input() {
        running = true;
        outputTo = new ArrayList<Inputable>();
        output = new ArrayList<JSONObject>();
    }
    
    public abstract void process(List<JSONObject> output);
    
    @Override
    public void run() {
        while(running) {
            process(output);
            flush();
        }
        preDestroy();
        shutdownComplete = true;
    }
    
    @Override
    public void flush() {
        for(Inputable inputable : outputTo) {
            inputable.queue(output);
        }
        output.clear();
    }

    @Override
    public void outputTo(Inputable inputable) {
        outputTo.add(inputable);
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
