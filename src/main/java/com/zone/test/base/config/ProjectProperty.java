package com.zone.test.base.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by Owen Pan on 2017-07-11.
 */
@Component
@ConfigurationProperties(prefix = "project")
public class ProjectProperty {
    private String indexURI;

    public String getIndexURI() {
        return indexURI;
    }

    public void setIndexURI(String indexURI) {
        this.indexURI = indexURI;
    }
}
