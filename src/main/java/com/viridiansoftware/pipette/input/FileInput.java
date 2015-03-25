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

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import oi.thekraken.grok.api.exception.GrokException;

import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListener;

import com.viridiansoftware.pipette.config.PipetteConfig;
import com.viridiansoftware.pipette.input.config.FileInputConfig;

/**
 *
 * @author Thomas Cashman
 */
public class FileInput extends SingleLineGrokInput implements TailerListener {
    public static final String TYPE = "file";

    private final Tailer fileTailer;
    private final BlockingQueue<String> outputQueue;

    public FileInput(FileInputConfig inputConfig, PipetteConfig config) throws IOException, GrokException {
        super(config, inputConfig.getGrokPattern());
        outputQueue = new LinkedBlockingQueue<String>();
        fileTailer = Tailer.create(new File(inputConfig.getFilepath()), this,
                inputConfig.getCheckDelayInMillis(), !inputConfig.isFromBeginning());
    }

    @Override
    public void init(Tailer tailer) {
    }

    @Override
    public void preDestroy() {
        fileTailer.stop();
    }

    @Override
    protected void writeLines(List<String> lines) {
        outputQueue.drainTo(lines);
    }

    @Override
    public void handle(String line) {
        try {
            outputQueue.put(line);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void fileNotFound() {
    }

    @Override
    public void fileRotated() {
    }

    @Override
    public void handle(Exception ex) {
        ex.printStackTrace();
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
