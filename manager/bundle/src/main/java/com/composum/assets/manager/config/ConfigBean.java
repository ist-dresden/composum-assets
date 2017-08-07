package com.composum.assets.manager.config;

import com.composum.assets.commons.config.ConfigHandle;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public interface ConfigBean {

    ConfigHandle getConfig();

    void toJson(JsonWriter writer) throws IOException;
}
