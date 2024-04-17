/*
 * Copyright (c) Mirth Corporation. All rights reserved.
 * 
 * http://www.mirthcorp.com
 * 
 * The software in this package is published under the terms of the MPL license a copy of which has
 * been included with this distribution in the LICENSE.txt file.
 */
package com.mirth.connect.client.ui.alert;

import java.util.Map;

import com.mirth.connect.model.alert.AlertActionGroup;

public interface IAlertActionPane {
	 public void setActionGroup(AlertActionGroup actionGroup, Map<String, Map<String, String>> protocolOptions);
}