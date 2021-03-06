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


/**
 *
 * @author Thomas Cashman
 */
public class PipetteConfig {
    private String inputsDirectory;
    private String filtersDirectory;
    private String outputsDirectory;
    private String grokPatternsDirectory;

    public String getGrokPatternsDirectory() {
        return grokPatternsDirectory;
    }

    public void setGrokPatternsDirectory(String grokPatternsPath) {
        this.grokPatternsDirectory = grokPatternsPath;
    }

    public String getInputsDirectory() {
        return inputsDirectory;
    }

    public void setInputsDirectory(String inputsDirectory) {
        this.inputsDirectory = inputsDirectory;
    }

    public String getFiltersDirectory() {
        return filtersDirectory;
    }

    public void setFiltersDirectory(String filtersDirectory) {
        this.filtersDirectory = filtersDirectory;
    }

    public String getOutputsDirectory() {
        return outputsDirectory;
    }

    public void setOutputsDirectory(String outputsDirectory) {
        this.outputsDirectory = outputsDirectory;
    }
}
