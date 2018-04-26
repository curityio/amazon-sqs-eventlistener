/*
 *  Copyright 2018 Curity AB
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package io.curity.identityserver.plugin.eventlistener.sqs.config;

import se.curity.identityserver.sdk.config.Configuration;
import se.curity.identityserver.sdk.config.annotation.Description;
import se.curity.identityserver.sdk.service.ExceptionFactory;
import se.curity.identityserver.sdk.service.Json;

import java.util.List;

@SuppressWarnings("InterfaceNeverImplemented")
public interface EventListenerConfiguration extends Configuration
{
    @Description("AWS region in which the queue is created. (e.g: us-west-2)")
    String getRegion();

    @Description("This should be severely restricted key that only has access to the resources you want to use.")
    String getAwsAccessKeyID();

    @Description("AWS Secret key.")
    String getAwsSecretAccessKey();

    @Description("Url to Amazon SQS Queue.")
    String getAmazonQueueUrl();

    List<Events> handleEvents();

    enum Events
    {
        AccountDeletedScimEvent,
        CreatedAccountEvent,
        CreatedSsoSessionEvent,
        AuthenticationEvent,
        LogoutAuthenticationEvent
    }

    ExceptionFactory exceptionFactory();

    Json json();
}
