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
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.heartbuffer.pipette.input.FileInput;
import com.heartbuffer.pipette.input.config.FileInputConfig;
import com.heartbuffer.pipette.input.config.InputConfig;

/**
 *
 * @author Thomas Cashman
 */
public class PipetteConfigLoaderTest {
    private static final String CONFIG_PATH = "/tmp/pipette/pipette.conf";
    
    @Before
    public void setUp() throws IOException {
        setupTmpConfigDirectory();
    }
    
    @Test
    public void testLoadConfiguration() throws IOException {
        PipetteConfigLoader configLoader = new PipetteConfigLoader(CONFIG_PATH);
        configLoader.loadConfiguration();
        checkPipetteConfig(configLoader);
        checkInputConfigs(configLoader);
        checkFilterConfigs(configLoader);
        checkOutputConfigs(configLoader);
    }
    
    private void checkPipetteConfig(PipetteConfigLoader configLoader) {
        Assert.assertEquals("/tmp/pipette/inputs.d/", 
                configLoader.getPipetteConfig().getInputsDirectory());
        Assert.assertEquals("/tmp/pipette/filters.d/", 
                configLoader.getPipetteConfig().getFiltersDirectory());
        Assert.assertEquals("/tmp/pipette/outputs.d/", 
                configLoader.getPipetteConfig().getOutputsDirectory());
        Assert.assertEquals("/tmp/pipette/patterns/", 
                configLoader.getPipetteConfig().getGrokPatternsDirectory());
    }
    
    private void checkInputConfigs(PipetteConfigLoader configLoader) {
        Assert.assertEquals(1, configLoader.getInputConfigs().size());
        
        InputConfig config = configLoader.getInputConfigs().get(0);
        Assert.assertEquals(true, config instanceof FileInputConfig);
        Assert.assertEquals("apache2", config.getId());
        Assert.assertEquals(1, config.getTo().size());
    }
    
    private void checkFilterConfigs(PipetteConfigLoader configLoader) {
        
    }
    
    private void checkOutputConfigs(PipetteConfigLoader configLoader) {
        
    }
    
    private void setupTmpConfigDirectory() throws IOException {
        File targetFile = new File(CONFIG_PATH);
        FileUtils.copyInputStreamToFile(FileInput.class.getResourceAsStream("/pipette.sample.conf"), targetFile);
    
        targetFile = new File("/tmp/pipette/inputs.d/apache2.conf");
        FileUtils.copyInputStreamToFile(FileInput.class.getResourceAsStream("/fileinput.sample.conf"), targetFile);
    }
}
