/*
 * Copyright (c) Mirth Corporation. All rights reserved.
 * 
 * http://www.mirthcorp.com
 * 
 * The software in this package is published under the terms of the MPL license a copy of which has
 * been included with this distribution in the LICENSE.txt file.
 */

package com.mirth.connect.connectors.http;

import io.github.pixee.security.HostValidator;
import io.github.pixee.security.Urls;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

import org.apache.commons.lang3.StringUtils;

import com.mirth.connect.client.core.api.MirthApiException;
import com.mirth.connect.connectors.core.http.HttpConnectorServletInterface;
import com.mirth.connect.donkey.model.channel.ConnectorProperties;
import com.mirth.connect.donkey.model.channel.RemoteHostConnectorProperties;
import com.mirth.connect.server.api.MirthServlet;
import com.mirth.connect.server.util.ConnectorUtil;
import com.mirth.connect.server.util.TemplateValueReplacer;
import com.mirth.connect.util.ConnectionTestResponse;

public class HttpConnectorServlet extends MirthServlet implements HttpConnectorServletInterface {

    protected static final int TIMEOUT = 5000;
    protected static final TemplateValueReplacer replacer = new TemplateValueReplacer();

    public HttpConnectorServlet(@Context HttpServletRequest request, @Context SecurityContext sc) {
        super(request, sc, PLUGIN_POINT);
    }

    @Override
    public ConnectionTestResponse testConnection(String channelId, String channelName, ConnectorProperties properties) {
        try {
            URL url = Urls.create(replacer.replaceValues(((RemoteHostConnectorProperties) properties).getHost(), channelId, channelName), Urls.HTTP_PROTOCOLS, HostValidator.DENY_COMMON_INFRASTRUCTURE_TARGETS);
            int port = url.getPort();
            // If no port was provided, default to port 80 or 443.
            return ConnectorUtil.testConnection(url.getHost(), (port == -1) ? (StringUtils.equalsIgnoreCase(url.getProtocol(), "https") ? 443 : 80) : port, TIMEOUT);
        } catch (Exception e) {
            throw new MirthApiException(e);
        }
    }
}
