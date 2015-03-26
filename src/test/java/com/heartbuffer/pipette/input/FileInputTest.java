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
package com.heartbuffer.pipette.input;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import oi.thekraken.grok.api.exception.GrokException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.heartbuffer.pipette.config.PipetteConfig;
import com.heartbuffer.pipette.input.config.FileInputConfig;
import com.heartbuffer.pipette.io.Inputable;

/**
 *
 * @author Thomas Cashman
 */
public class FileInputTest implements Inputable {
    private static final String SAMPLE_FILE_CONTENT = "/apache2.access.sample.log";
    private static final int TOTAL_LINES_IN_CONTENT = 1535;
    private static final String TARGET_FILEPATH = "/tmp/apache2.access.log";
    
    private FileInput fileInput;
    private Queue<Map<String, Object>> output;
    
    @Before
    public void setUp() throws IOException, GrokException {
        File targetFile = new File(TARGET_FILEPATH);
        if(targetFile.exists()) {
            targetFile.delete();
        }
        FileUtils.copyInputStreamToFile(FileInput.class.getResourceAsStream(SAMPLE_FILE_CONTENT), targetFile);
        
        FileInputConfig fileInputConfig = new FileInputConfig();
        fileInputConfig.setFilepath(TARGET_FILEPATH);
        fileInputConfig.setFromBeginning(true);
        fileInputConfig.setCheckDelayInMillis(1);
        fileInputConfig.setGrokPattern("%{COMMONAPACHELOG}");
        
        PipetteConfig config = new PipetteConfig();
        config.setGrokPatternsPath(Paths.get(new File(".").getAbsolutePath(), "patterns").toAbsolutePath().toString());
        
        fileInput = new FileInput(fileInputConfig, config);
        fileInput.outputTo(this);
        
        output = new LinkedList<Map<String, Object>>();
    }
    
    @After
    public void teardown() {
        fileInput.preDestroy();
        fileInput.shutdown(false);
    }
    
    @Test
    public void testFileInput() {
        Assert.assertEquals(0, output.size());
        new Thread(fileInput).start();
        try {
            Thread.sleep(1000);
        } catch (Exception e) {}
        Assert.assertEquals(TOTAL_LINES_IN_CONTENT, output.size());
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
}
