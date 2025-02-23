/*
 * Copyright (c) Mirth Corporation. All rights reserved.
 * 
 * http://www.mirthcorp.com
 * 
 * The software in this package is published under the terms of the MPL license a copy of which has
 * been included with this distribution in the LICENSE.txt file.
 */

package com.mirth.connect.donkey.model.channel;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;

import com.mirth.connect.donkey.model.message.Status;
import com.mirth.connect.donkey.util.DonkeyElement;
import com.mirth.connect.donkey.util.migration.Migratable;
import com.mirth.connect.donkey.util.purge.Purgable;

public class SourceConnectorProperties implements Serializable, Migratable, Purgable {

    /**
     * "Respond From" key indicating that no response should be sent back
     */
    public static final String RESPONSE_NONE = "None";

    /**
     * "Respond From" key indicating that the response returned by the source connector should be
     * auto-generated by the inbound data type, with a status of SENT
     */
    public static final String RESPONSE_AUTO_BEFORE = "Auto-generate (Before processing)";

    /**
     * "Respond From" key indicating that the response returned by the source connector should be
     * auto-generated by the inbound data type and based on whether or not a message is filtered or
     * errored in the source filter/transformer
     */
    public static final String RESPONSE_SOURCE_TRANSFORMED = "Auto-generate (After source transformer)";

    /**
     * "Respond From" key indicating that the response returned by the source connector should be
     * auto-generated by the inbound data type and based on whether or not all destinations sent or
     * queued the message successfully
     */
    public static final String RESPONSE_DESTINATIONS_COMPLETED = "Auto-generate (Destinations completed)";

    /**
     * Response map key to be used to store the post-processor's custom response
     */
    public static final String RESPONSE_POST_PROCESSOR = "Postprocessor";

    /**
     * When returning a response status based on the statuses of all destinations, use this
     * precedence order in determining which status to use when the destination statuses are
     * different
     */
    public static final Status[] RESPONSE_STATUS_PRECEDENCE = new Status[] { Status.ERROR,
            Status.QUEUED, Status.SENT, Status.FILTERED };

    public static final String[] QUEUE_ON_RESPONSES = new String[] { RESPONSE_NONE,
            RESPONSE_AUTO_BEFORE };

    public static final String[] QUEUE_OFF_RESPONSES = new String[] { RESPONSE_NONE,
            RESPONSE_AUTO_BEFORE, RESPONSE_SOURCE_TRANSFORMED, RESPONSE_DESTINATIONS_COMPLETED,
            RESPONSE_POST_PROCESSOR };

    private String responseVariable;
    private boolean respondAfterProcessing;
    private boolean processBatch;
    private boolean firstResponse;
    private int processingThreads;
    private Map<String, String> resourceIds;
    private int queueBufferSize;

    public SourceConnectorProperties() {
        this(RESPONSE_NONE);
    }

    public SourceConnectorProperties(String defaultResponse) {
        this.responseVariable = defaultResponse;
        this.respondAfterProcessing = true;
        this.processBatch = false;
        this.firstResponse = false;
        this.processingThreads = 1;
        this.resourceIds = new LinkedHashMap<String, String>();
        resourceIds.put("Default Resource", "[Default Resource]");
        this.queueBufferSize = 0;
    }
    
    public SourceConnectorProperties(SourceConnectorProperties props) {
    	responseVariable = props.getResponseVariable();
    	respondAfterProcessing = props.isRespondAfterProcessing();
    	processBatch = props.isProcessBatch();
    	firstResponse = props.isFirstResponse();
    	processingThreads = props.getProcessingThreads();
    	queueBufferSize = props.getQueueBufferSize();
    	
    	resourceIds = new LinkedHashMap<>();
    	for (String resourceIdKey : props.getResourceIds().keySet()) {
    		resourceIds.put(resourceIdKey, props.getResourceIds().get(resourceIdKey));
    	}
    }

    public String getResponseVariable() {
        return responseVariable;
    }

    public void setResponseVariable(String responseVariable) {
        this.responseVariable = responseVariable;
    }

    public boolean isRespondAfterProcessing() {
        return respondAfterProcessing;
    }

    public void setRespondAfterProcessing(boolean respondAfterProcessing) {
        this.respondAfterProcessing = respondAfterProcessing;
    }

    public boolean isProcessBatch() {
        return processBatch;
    }

    public void setProcessBatch(boolean processBatch) {
        this.processBatch = processBatch;
    }

    public boolean isFirstResponse() {
        return firstResponse;
    }

    public void setFirstResponse(boolean firstResponse) {
        this.firstResponse = firstResponse;
    }

    public int getProcessingThreads() {
        return processingThreads;
    }

    public void setProcessingThreads(int processingThreads) {
        this.processingThreads = processingThreads;
    }

    public Map<String, String> getResourceIds() {
        return resourceIds;
    }

    public void setResourceIds(Map<String, String> resourceIds) {
        this.resourceIds = resourceIds;
    }

    public int getQueueBufferSize() {
        return queueBufferSize;
    }

    public void setQueueBufferSize(int queueBufferSize) {
        this.queueBufferSize = queueBufferSize;
    }

    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public void migrate3_0_1(DonkeyElement element) {}

    @Override
    public void migrate3_0_2(DonkeyElement element) {}

    @Override
    public void migrate3_1_0(DonkeyElement element) {
        element.addChildElementIfNotExists("processBatch", "false");
        element.addChildElementIfNotExists("firstResponse", "false");

        element.removeChild("defaultQueueOffResponses");
        element.removeChild("defaultQueueOnResponses");
    }

    @Override
    public void migrate3_2_0(DonkeyElement element) {
        DonkeyElement resourceIdsElement = element.addChildElement("resourceIds");
        resourceIdsElement.setAttribute("class", "linked-hash-set");
        resourceIdsElement.addChildElement("string", "Default Resource");
    }

    @Override
    public void migrate3_3_0(DonkeyElement element) {}

    @Override
    public void migrate3_4_0(DonkeyElement element) {
        element.addChildElementIfNotExists("processingThreads", "1");

        DonkeyElement resourceIdsElement = element.getChildElement("resourceIds");
        List<DonkeyElement> resourceIdsList = resourceIdsElement.getChildElements();
        resourceIdsElement.removeChildren();
        resourceIdsElement.setAttribute("class", "linked-hash-map");

        for (DonkeyElement resourceId : resourceIdsList) {
            DonkeyElement entry = resourceIdsElement.addChildElement("entry");
            String resourceIdText = resourceId.getTextContent();
            entry.addChildElement("string", resourceIdText);
            if (resourceIdText.equals("Default Resource")) {
                entry.addChildElement("string", "[Default Resource]");
            } else {
                entry.addChildElement("string");
            }
        }
    }

    @Override
    public void migrate3_5_0(DonkeyElement element) {}

    @Override
    public void migrate3_6_0(DonkeyElement element) {}

    @Override
    public void migrate3_7_0(DonkeyElement element) {}
    
    @Override
    public void migrate3_9_0(DonkeyElement element) {}
    
    @Override 
    public void migrate3_11_0(DonkeyElement element) {}

    @Override
    public void migrate3_11_1(DonkeyElement element) {}

    @Override
    public void migrate3_12_0(DonkeyElement element) {}
    
    @Override
    public Map<String, Object> getPurgedProperties() {
        Map<String, Object> purgedProperties = new HashMap<String, Object>();
        purgedProperties.put("respondAfterProcessing", respondAfterProcessing);
        purgedProperties.put("processBatch", processBatch);
        purgedProperties.put("firstResponse", firstResponse);
        purgedProperties.put("processingThreads", processingThreads);
        purgedProperties.put("resourceIdsCount", resourceIds.size());
        purgedProperties.put("queueBufferSize", queueBufferSize);
        return purgedProperties;
    }
}
