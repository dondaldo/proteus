/*
 * Apache License
 * Version 2.0, January 2004
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION
 *
 * Copyright (c) 2017 Flipkart Internet Pvt. Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of the
 * License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.flipkart.android.proteus;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v4.util.LruCache;

import com.flipkart.android.proteus.parser.ParseHelper;
import com.flipkart.android.proteus.toolbox.ProteusConstants;

import java.util.HashMap;
import java.util.Map;

/**
 * Style
 *
 * @author aditya.sharat
 */

public class Style extends Value {

    public static final Map<String, Integer> styleMap = new HashMap<>();
    public static final Map<String, Integer> attributeMap = new HashMap<>();

    public static final Style NULL = new Style();

    public final int styleId;
    public final int attributeId;

    private Style() {
        this.styleId = -1;
        this.attributeId = -1;
    }

    private Style(String value) throws IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
        if (!ParseHelper.isLocalResourceAttribute(value)) {
            throw new IllegalArgumentException(value + " is not a valid style attribute");
        }
        String[] dimenArr = value.substring(1, value.length()).split(":");
        String style = dimenArr[0];
        String attr = dimenArr[1];
        Integer styleId = styleMap.get(style);
        if (styleId == null) {
            styleId = R.style.class.getField(style).getInt(null);
            styleMap.put(style, styleId);
        }
        this.styleId = styleId;
        Integer attrId = attributeMap.get(attr);
        if (attrId == null) {
            attrId = R.attr.class.getField(attr).getInt(null);
            attributeMap.put(attr, attrId);
        }
        this.attributeId = attrId;
    }

    public TypedArray apply(Context context) {
        return context.getTheme().obtainStyledAttributes(styleId, new int[]{attributeId});
    }

    @Nullable
    public static Style valueOf(String value) {
        Style style = StyleCache.cache.get(value);
        if (null == style) {
            try {
                style = new Style(value);
                StyleCache.cache.put(value, style);
            } catch (Exception e) {
                if (ProteusConstants.isLoggingEnabled()) {
                    e.printStackTrace();
                }
                StyleCache.cache.put(value, NULL);
            }
        } else if (style == NULL) {
            style = null;
        }
        return style;
    }

    private static class StyleCache {
        private static final LruCache<String, Style> cache = new LruCache<>(64);
    }

    @Override
    Value copy() {
        return this;
    }
}
