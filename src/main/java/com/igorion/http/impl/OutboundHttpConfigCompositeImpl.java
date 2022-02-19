package com.igorion.http.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.impl.client.HttpClientBuilder;

import com.igorion.http.IOutboundHttpConfig;
import com.igorion.http.IOutboundHttpConfigEdit;

class OutboundHttpConfigCompositeImpl implements IOutboundHttpConfigEdit {

    private final List<IOutboundHttpConfig> subConfigs;

    public OutboundHttpConfigCompositeImpl() {
        this.subConfigs = new ArrayList<>();
    }

    @Override
    public boolean hasSubConfig(Class<? extends IOutboundHttpConfig> type) {
        for (IOutboundHttpConfig subConfig : this.subConfigs) {
            if (type.isAssignableFrom(subConfig.getClass())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void addSubConfig(IOutboundHttpConfig subConfig) {
        this.subConfigs.add(subConfig);
    }

    @Override
    public IOutboundHttpConfig getOutboundHttpConfig() {
        return this::applyAllTo;
    }

    protected HttpClientBuilder applyAllTo(final HttpClientBuilder builder1) {
        HttpClientBuilder builder = builder1;
        for (IOutboundHttpConfig subConfig : this.subConfigs) {
            builder = subConfig.applyTo(builder);
        }
        return builder;
    }

}
