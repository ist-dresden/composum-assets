package com.composum.assets.commons.config.aspect;

import com.composum.assets.commons.config.ConfigHandle;
import org.apache.commons.lang3.StringUtils;

import static com.composum.assets.commons.config.ConfigHandle.FILE_QUALITY;

public class File {

    public final Double quality;

    public File(ConfigHandle config) {
        Double value = null;
        String string = config.getInherited(FILE_QUALITY, "");
        if (StringUtils.isNotBlank(string)) {
            value = Double.parseDouble(string);
            if (value > 1.0) {
                value = value / 100.0;
            }
        }
        quality = value;
    }
}
