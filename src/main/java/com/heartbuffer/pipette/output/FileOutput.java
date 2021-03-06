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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;


/**
 *
 * @author Thomas Cashman
 */
public class FileOutput extends Output {
    public static final String TYPE = "file";
    
    private final Gson gson;
    private BufferedWriter writer;
    
    public FileOutput(File file) {
        this(file, DEFAULT_BUFFER_SIZE);
    }

    public FileOutput(File file, int bufferSize) {
        super(bufferSize);
        
        gson = new Gson();
        try {
            writer = new BufferedWriter(new FileWriter(file, true));
        } catch (IOException e) {
            e.printStackTrace();
            shutdown(false);
        }
    }

    @Override
    public void flush(List<Map<String, Object>> output) throws Exception {
        for(Map<String, Object> json : output) {
            String jsonString = gson.toJson(json);
            writer.append(jsonString);
            writer.newLine();
        }
        writer.flush();
    }
    
    @Override
    public void preDestroy() {
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
