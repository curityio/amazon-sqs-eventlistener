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

package io.curity.identityserver.plugin.eventlistener.sqs;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import io.curity.identityserver.plugin.eventlistener.sqs.config.EventListenerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.curity.identityserver.sdk.data.events.AuditableEvent;
import se.curity.identityserver.sdk.event.EventListener;

public class SQSEventListener implements EventListener<AuditableEvent>
{
    private static final Logger _logger = LoggerFactory.getLogger(SQSEventListener.class);
    private final EventListenerConfiguration _config;
    private final AmazonSQS _sqs;

    public SQSEventListener(EventListenerConfiguration config)
    {
        _config = config;
        AWSCredentialsProvider awsCredentials = new AWSStaticCredentialsProvider(
                new BasicAWSCredentials(_config.getAwsAccessKeyID(), _config.getAwsSecretAccessKey()));
        _sqs = AmazonSQSClientBuilder.standard()
                .withRegion(_config.getRegion())
                .withCredentials(awsCredentials)
                .build();
    }

    @Override
    public Class<AuditableEvent> getEventType()
    {
        return AuditableEvent.class;
    }

    @Override
    public void handle(AuditableEvent event)
    {
        _config.handleEvents().forEach(eventType ->
        {
            Class<? extends AuditableEvent> eventTypeClass = null;
            try
            {
                eventTypeClass = (Class<? extends AuditableEvent>) Class.forName("se.curity.identityserver.sdk.data.events" + eventType.name());
            }
            catch (ClassNotFoundException e)
            {
                _logger.debug("Class not found for event type : {}", eventType.name());
            }
            if (eventTypeClass != null && isEqual(event.getClass(), eventTypeClass))
            {
                _logger.debug("Handling event of type : {} and data : {}", event.getAuditData().getType(), event.getAuditData().asMap());
                _sqs.sendMessage(new SendMessageRequest(_config.getAmazonQueueUrl(),
                        _config.json().toJson(event.getAuditData().asMap())));
            }
        });
    }

    public boolean isEqual(Class<? extends AuditableEvent> event, Class<? extends AuditableEvent> event1)
    {
        return event.getName().equals(event1.getName());
    }
}
