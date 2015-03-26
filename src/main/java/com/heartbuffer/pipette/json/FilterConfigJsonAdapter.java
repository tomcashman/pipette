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
import com.heartbuffer.pipette.filter.AddTimestampFilter;
import com.heartbuffer.pipette.filter.EsDetermineDocumentDataFilter;
import com.heartbuffer.pipette.filter.NDiffFilter;
import com.heartbuffer.pipette.filter.config.AddTimestampFilterConfig;
import com.heartbuffer.pipette.filter.config.EsDetermineDocumentDataFilterConfig;
import com.heartbuffer.pipette.filter.config.FilterConfig;
import com.heartbuffer.pipette.input.FileInput;
import com.heartbuffer.pipette.input.config.FileInputConfig;

/**
 *
 * @author Thomas Cashman
 */
public class FilterConfigJsonAdapter implements JsonDeserializer<FilterConfig> {
    private static final String TYPE_FIELD = "type";

    @Override
    public FilterConfig deserialize(JsonElement json, Type typeOfT,
            JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        JsonPrimitive primitive = (JsonPrimitive) jsonObject.get(TYPE_FIELD);
        String filterType = primitive.getAsString();

        Class<?> clazz = null;

        switch (filterType) {
        case AddTimestampFilter.TYPE:
            clazz = AddTimestampFilterConfig.class;
            break;
        case EsDetermineDocumentDataFilter.TYPE:
            clazz = EsDetermineDocumentDataFilterConfig.class;
            break;
        case NDiffFilter.TYPE:
            //TODO: Add NDiffFilterConfig
            break;
        default:
            throw new JsonParseException("No such filter of type '" + filterType + "'");
        }

        return context.deserialize(jsonObject, clazz);
    }

}
