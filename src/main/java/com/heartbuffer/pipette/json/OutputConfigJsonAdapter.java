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
package com.heartbuffer.pipette.json;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.heartbuffer.pipette.output.ElasticsearchHttpOutput;
import com.heartbuffer.pipette.output.ElasticsearchTransportOutput;
import com.heartbuffer.pipette.output.FileOutput;
import com.heartbuffer.pipette.output.config.ElasticsearchHttpOutputConfig;
import com.heartbuffer.pipette.output.config.ElasticsearchTransportOutputConfig;
import com.heartbuffer.pipette.output.config.OutputConfig;

/**
 *
 * @author Thomas Cashman
 */
public class OutputConfigJsonAdapter implements JsonDeserializer<OutputConfig> {
    private static final String TYPE_FIELD = "type";

    @Override
    public OutputConfig deserialize(JsonElement json, Type typeOfT,
            JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        JsonPrimitive primitive = (JsonPrimitive) jsonObject.get(TYPE_FIELD);
        String outputType = primitive.getAsString();

        Class<?> clazz = null;

        switch (outputType) {
        case FileOutput.TYPE:
            //TODO: Add FileOutputConfig
            break;
        case ElasticsearchTransportOutput.TYPE:
            clazz = ElasticsearchTransportOutputConfig.class;
            break;
        case ElasticsearchHttpOutput.TYPE:
            clazz = ElasticsearchHttpOutputConfig.class;
            break;
        default:
            throw new JsonParseException("No such output of type '" + outputType + "'");
        }

        return context.deserialize(jsonObject, clazz);
    }

}
