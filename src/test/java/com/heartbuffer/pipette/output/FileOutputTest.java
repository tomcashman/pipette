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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;
import com.heartbuffer.pipette.json.JsonObjectTypeToken;

/**
 *
 * @author Thomas Cashman
 */
public class FileOutputTest {
    private static final String TARGET_FILEPATH = "/tmp/output.txt";
    private static final List<Map<String, Object>> CONTENT = new ArrayList<Map<String, Object>>();
    
    private Gson gson;
    private File targetFile;
    private FileOutput fileOutput;
    
    @Before
    public void setUp() {
        gson = new Gson();
        
        setUpContent();
        
        targetFile = new File(TARGET_FILEPATH);
        if(targetFile.exists()) {
            targetFile.delete();
        }
        fileOutput = new FileOutput(targetFile, CONTENT.size());
        new Thread(fileOutput).start();
    }
    
    @After
    public void teardown() {
        fileOutput.preDestroy();
        fileOutput.shutdown(false);
    }
    
    @Test
    public void testAppendToNewFile() throws IOException {
        fileOutput.queue(CONTENT);
        sleep(500);
        
        List<String> fileContent = FileUtils.readLines(targetFile);
        Assert.assertEquals(CONTENT.size(), fileContent.size());
        for(int i = 0; i < CONTENT.size(); i++) {
            String expectedContent = gson.toJson(CONTENT.get(i));
            Assert.assertEquals(expectedContent, fileContent.get(i).toString());
        }
    }
    
    @Test
    public void testAppendToExistingFile() throws IOException {
        writeExistingContentToFile();
        for(int i = CONTENT.size(); i < 100; i++) {
            fileOutput.queue(createJsonObject("line", i));
        }
        sleep(1000);
        
        List<String> fileContent = FileUtils.readLines(targetFile);
        Assert.assertEquals(100, fileContent.size());
        for(int i = 0; i < fileContent.size(); i++) {
            Map<String, Object> json = gson.fromJson(fileContent.get(i), JsonObjectTypeToken.TYPE);
            Assert.assertEquals(i, ((Number) json.get("line")).intValue());
        }
    }
    
    private void writeExistingContentToFile() throws IOException {
        for(Map<String, Object> json : CONTENT) {
            try {
                FileUtils.write(targetFile, json.toString() + "\n", true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        List<String> fileContent = FileUtils.readLines(targetFile);
        Assert.assertEquals(CONTENT.size(), fileContent.size());
    }
    
    private void setUpContent() {
        CONTENT.clear();
        for(int i = 0; i < 50; i++) {
            CONTENT.add(createJsonObject("line", i));
        }
    }
    
    @SuppressWarnings("unchecked")
    private Map<String, Object> createJsonObject(String key, int value) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put(key, new Integer(value));
        return result;
    }
    
    private void sleep(long timeout) {
        try {
            Thread.sleep(timeout);
        } catch (Exception e) {}
    }
}
