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
package com.viridiansoftware.pipette.filter.config;

import com.viridiansoftware.pipette.config.PipetteConfig;
import com.viridiansoftware.pipette.filter.ElasticsearchFilter;
import com.viridiansoftware.pipette.filter.Filter;

/**
 *
 * @author Thomas Cashman
 */
public class ElasticsearchFilterConfig extends FilterConfig {
    private String indexSourceField;
    private String typeSourceField;
    private String idSourceField;

    public String getIndexSourceField() {
        return indexSourceField;
    }

    public void setIndexSourceField(String indexSourceField) {
        this.indexSourceField = indexSourceField;
    }

    public String getTypeSourceField() {
        return typeSourceField;
    }

    public void setTypeSourceField(String typeSourceField) {
        this.typeSourceField = typeSourceField;
    }

    public String getIdSourceField() {
        return idSourceField;
    }

    public void setIdSourceField(String idSourceField) {
        this.idSourceField = idSourceField;
    }

    @Override
    public Filter build(PipetteConfig config) {
        if(getBufferSize() > 0) {
            return new ElasticsearchFilter(indexSourceField, typeSourceField, idSourceField, getBufferSize());
        }
        return new ElasticsearchFilter(indexSourceField, typeSourceField, idSourceField);
    }

}