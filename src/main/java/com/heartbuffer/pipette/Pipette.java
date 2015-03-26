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
package com.heartbuffer.pipette;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.heartbuffer.pipette.config.PipetteConfig;
import com.heartbuffer.pipette.config.PipetteConfigLoader;
import com.heartbuffer.pipette.filter.Filter;
import com.heartbuffer.pipette.filter.config.FilterConfig;
import com.heartbuffer.pipette.input.Input;
import com.heartbuffer.pipette.input.config.InputConfig;
import com.heartbuffer.pipette.output.Output;
import com.heartbuffer.pipette.output.config.OutputConfig;

/**
 *
 * @author Thomas Cashman
 */
public class Pipette implements Runnable {
    private final PipetteConfig config;
    private final List<InputConfig> inputConfigs;
    private final List<FilterConfig> filterConfigs;
    private final List<OutputConfig> outputConfigs;

    private Map<String, Input> inputs;
    private Map<String, Filter> filters;
    private Map<String, Output> outputs;
    
    private List<Thread> threads;

    public Pipette(PipetteConfig config, List<InputConfig> inputConfigs,
            List<FilterConfig> filterConfigs, List<OutputConfig> outputConfigs) {
        this.config = config;
        this.inputConfigs = inputConfigs;
        this.filterConfigs = filterConfigs;
        this.outputConfigs = outputConfigs;
    }

    public void start() {
        createInputs();
        createFilters();
        createOutputs();
        wireComponents();
        startThreads();
    }
    
    private void createInputs() {
        inputs = new HashMap<String, Input>();
        for (InputConfig inputConfig : inputConfigs) {
            inputs.put(inputConfig.getId(), inputConfig.build(config));
        }
    }
    
    private void createFilters() {
        filters = new HashMap<String, Filter>();
        for (FilterConfig filterConfig : filterConfigs) {
            filters.put(filterConfig.getId(), filterConfig.build(config));
        }
    }
    
    private void createOutputs() {
        outputs = new HashMap<String, Output>();
        for (OutputConfig outputConfig : outputConfigs) {
            outputs.put(outputConfig.getId(), outputConfig.build(config));
        }
    }
    
    private void wireComponents() {
        for (InputConfig config : inputConfigs) {
            Input input = inputs.get(config.getId());
            
            for(String targetId : config.getTo()) {
                if (filters.containsKey(targetId)) {
                    Filter filter = filters.get(targetId);
                    input.outputTo(filter);
                    continue;
                }

                if (outputs.containsKey(targetId)) {
                    Output output = outputs.get(targetId);
                    input.outputTo(output);
                    continue;
                }
                
                // TODO: Print no such target error
            }
        }

        for (FilterConfig config : filterConfigs) {
            Filter filter = filters.get(config.getId());
            
            for(String targetId : config.getTo()) {
                if (!outputs.containsKey(targetId)) {
                    // TODO: Print no such target error
                    continue;
                }
                Output output = outputs.get(targetId);
                filter.outputTo(output);
            }
        }
    }
    
    private void startThreads() {
        threads = new ArrayList<Thread>();
        for (String id : inputs.keySet()) {
            threads.add(new Thread(inputs.get(id)));
        }
        for (String id : filters.keySet()) {
            threads.add(new Thread(filters.get(id)));
        }
        for (String id : outputs.keySet()) {
            threads.add(new Thread(outputs.get(id)));
        }
        
        for (Thread thread : threads) {
            thread.start();
        }
    }

    @Override
    public void run() {
        shutdown();
    }
    
    private void shutdown() {
        for (String id : inputs.keySet()) {
            inputs.get(id).shutdown(true);
        }
        for (String id : filters.keySet()) {
            filters.get(id).shutdown(true);
        }
        for (String id : outputs.keySet()) {
            outputs.get(id).shutdown(true);
        }
    }

    public void attachShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(this));
    }
    
    public void waitForExit() {
        for(Thread thread : threads) {
            try {
                thread.join();
            } catch (Exception e) {}
        }
    }

    public static void main(String[] args) throws IOException {
        // TODO: Make this change filepath based on OS
        PipetteConfigLoader configLoader = new PipetteConfigLoader("/etc/pipette/pipette.conf");
        configLoader.loadConfiguration();
        
        Pipette pipette = new Pipette(configLoader.getPipetteConfig(), 
                configLoader.getInputConfigs(), 
                configLoader.getFilterConfigs(),
                configLoader.getOutputConfigs());
        pipette.attachShutdownHook();
        pipette.start();
        pipette.waitForExit();
    }
}
