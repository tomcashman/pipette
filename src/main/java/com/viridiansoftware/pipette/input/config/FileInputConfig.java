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
package com.viridiansoftware.pipette.input.config;

import java.io.IOException;

import oi.thekraken.grok.api.exception.GrokException;

import com.viridiansoftware.pipette.config.PipetteConfig;
import com.viridiansoftware.pipette.input.FileInput;
import com.viridiansoftware.pipette.input.Input;

/**
 *
 * @author Thomas Cashman
 */
public class FileInputConfig extends InputConfig {
    private String filepath;
    private String grokPattern;
    private boolean fromBeginning;
    private long checkDelayInMillis;
    
    public String getFilepath() {
        return filepath;
    }
    
    public void setFilepath(String file) {
        this.filepath = file;
    }
    
    public String getGrokPattern() {
        return grokPattern;
    }

    public void setGrokPattern(String grokPattern) {
        this.grokPattern = grokPattern;
    }

    public boolean isFromBeginning() {
        return fromBeginning;
    }
    
    public void setFromBeginning(boolean fromBeginning) {
        this.fromBeginning = fromBeginning;
    }
    
    public long getCheckDelayInMillis() {
        return checkDelayInMillis;
    }
    
    public void setCheckDelayInMillis(long checkDelayInMillis) {
        this.checkDelayInMillis = checkDelayInMillis;
    }

    @Override
    public Input build(PipetteConfig config) {
        try {
            return new FileInput(this, config);
        } catch (IOException | GrokException e) {
            e.printStackTrace();
        }
        return null;
    }
}
