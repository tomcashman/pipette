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
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import oi.thekraken.grok.api.Grok;
import oi.thekraken.grok.api.Match;
import oi.thekraken.grok.api.exception.GrokException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.viridiansoftware.pipette.config.PipetteConfig;

/**
 *
 * @author Thomas Cashman
 */
public abstract class SingleLineGrokInput extends Input {
    private final List<String> lines;
    private final JSONParser jsonParser;

    private final Grok grok;

    public SingleLineGrokInput(PipetteConfig config, String pattern) throws IOException, GrokException {
        lines = new ArrayList<String>();
        jsonParser = new JSONParser();
        
        File patternsDir = new File(config.getGrokPatternsPath());
        grok = new Grok();
        if(patternsDir.isDirectory()) {
            for(File file : patternsDir.listFiles()) {
                FileReader fileReader = new FileReader(file);
                grok.addPatternFromReader(fileReader);
                fileReader.close();
            }
        } else {
            FileReader fileReader = new FileReader(patternsDir);
            grok.addPatternFromReader(fileReader);
            fileReader.close();
        }
        grok.compile(pattern);
    }

    @Override
    public void preDestroy() {
    }

    @Override
    public void process(List<JSONObject> output) {
        writeLines(lines);

        for (String line : lines) {
            Match match = grok.match(line);
            match.captures();
            try {
                JSONObject json = (JSONObject) jsonParser.parse(match.toJson());
                output.add(json);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        lines.clear();
    }

    protected abstract void writeLines(List<String> lines);
}
