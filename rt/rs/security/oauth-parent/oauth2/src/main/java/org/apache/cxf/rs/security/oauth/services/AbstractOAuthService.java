/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.cxf.rs.security.oauth.services;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.ext.MessageContext;
import org.apache.cxf.rs.security.oauth.common.Client;
import org.apache.cxf.rs.security.oauth.common.OAuthError;
import org.apache.cxf.rs.security.oauth.provider.OAuthDataProvider;
import org.apache.cxf.rs.security.oauth.provider.OAuthServiceException;
import org.apache.cxf.rs.security.oauth.utils.OAuthConstants;

/**
 * Abstract utility class which OAuth services extend
 */
public abstract class AbstractOAuthService {
    private MessageContext mc;
    private OAuthDataProvider dataProvider;
    
    @Context 
    public void setMessageContext(MessageContext context) {
        this.mc = context;    
    }
    
    public MessageContext getMessageContext() {
        return mc;
    }

    public void setDataProvider(OAuthDataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    public OAuthDataProvider getDataProvider() {
        return dataProvider;
    }
    
    protected MultivaluedMap<String, String> getQueryParameters() {
        return getMessageContext().getUriInfo().getQueryParameters();
    }
    
    protected Client getClient(MultivaluedMap<String, String> params) {
        return getClient(params.getFirst(OAuthConstants.CLIENT_ID));
    }
    protected Client getClient(String clientId) {
        Client client = null;
        
        if (clientId != null) {
            try {
                client = dataProvider.getClient(clientId);
            } catch (OAuthServiceException ex) {
                // log it
            }
        }
        if (client == null) {
            reportInvalidRequestError("Client ID is invalid");
        }
        return client;
        
    }
    
    protected void reportInvalidRequestError(String errorDescription) {
        OAuthError error = 
            new OAuthError(OAuthConstants.INVALID_REQUEST, errorDescription);
        throw new WebApplicationException(
                  Response.status(400).type(MediaType.APPLICATION_JSON).entity(error).build());
    }
}
