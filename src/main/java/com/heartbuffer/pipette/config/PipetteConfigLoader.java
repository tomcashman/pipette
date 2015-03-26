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
package com.heartbuffer.pipette.config;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.heartbuffer.pipette.filter.config.FilterConfig;
import com.heartbuffer.pipette.input.config.InputConfig;
import com.heartbuffer.pipette.json.FilterConfigJsonAdapter;
import com.heartbuffer.pipette.json.InputConfigJsonAdapter;
import com.heartbuffer.pipette.json.OutputConfigJsonAdapter;
import com.heartbuffer.pipette.output.config.OutputConfig;

/**
 *
 * @author Thomas Cashman
 */
public class PipetteConfigLoader {
    private final String pipetteConfigFilepath;

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(InputConfig.class, new InputConfigJsonAdapter())
            .registerTypeAdapter(FilterConfig.class, new FilterConfigJsonAdapter())
            .registerTypeAdapter(OutputConfig.class, new OutputConfigJsonAdapter()).create();

    private PipetteConfig pipetteConfig;
    private List<InputConfig> inputConfigs = new ArrayList<InputConfig>();
    private List<FilterConfig> filterConfigs = new ArrayList<FilterConfig>();
    private List<OutputConfig> outputConfigs = new ArrayList<OutputConfig>();
    
    public PipetteConfigLoader(String pipetteConfigFilepath) {
        this.pipetteConfigFilepath = pipetteConfigFilepath;
    }

    public void loadConfiguration() throws IOException {
        FileReader initialConfigReader = new FileReader(new File(pipetteConfigFilepath));
        pipetteConfig = gson.fromJson(initialConfigReader, PipetteConfig.class);
        initialConfigReader.close();
        
        loadInputConfiguration();
        loadFilterConfiguration();
        loadOutputConfiguration();
    }
    
    private void loadInputConfiguration() throws IOException {
        File inputConfigDirectory = new File(pipetteConfig.getInputsDirectory());
        if(!inputConfigDirectory.exists()) {
            //TODO: Log error
            return;
        }
        
        for(File inputConfigFile : inputConfigDirectory.listFiles()) {
            FileReader configReader = new FileReader(inputConfigFile);
            inputConfigs.add(gson.fromJson(configReader, InputConfig.class));
            configReader.close();
        }
    }
    
    private void loadFilterConfiguration() throws IOException {
        File filterConfigDirectory = new File(pipetteConfig.getFiltersDirectory());
        if(!filterConfigDirectory.exists()) {
            //TODO: Log error
            return;
        }
        
        for(File filterConfigFile : filterConfigDirectory.listFiles()) {
            FileReader configReader = new FileReader(filterConfigFile);
            filterConfigs.add(gson.fromJson(configReader, FilterConfig.class));
            configReader.close();
        }
    }
    
    private void loadOutputConfiguration() throws IOException {
        File outputConfigDirectory = new File(pipetteConfig.getOutputsDirectory());
        if(!outputConfigDirectory.exists()) {
            //TODO: Log error
            return;
        }
        
        for(File outputConfigFile : outputConfigDirectory.listFiles()) {
            FileReader configReader = new FileReader(outputConfigFile);
            outputConfigs.add(gson.fromJson(configReader, OutputConfig.class));
            configReader.close();
        }
    }

    public PipetteConfig getPipetteConfig() {
        return pipetteConfig;
    }

    public List<InputConfig> getInputConfigs() {
        return inputConfigs;
    }

    public List<FilterConfig> getFilterConfigs() {
        return filterConfigs;
    }

    public List<OutputConfig> getOutputConfigs() {
        return outputConfigs;
    }
}
