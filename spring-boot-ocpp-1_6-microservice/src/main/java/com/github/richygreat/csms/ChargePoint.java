package com.github.richygreat.csms;

import eu.chargetime.ocpp.IClientAPI;
import eu.chargetime.ocpp.JSONClient;
import eu.chargetime.ocpp.feature.profile.ClientCoreEventHandler;
import eu.chargetime.ocpp.feature.profile.ClientCoreProfile;
import eu.chargetime.ocpp.model.Request;
import eu.chargetime.ocpp.model.core.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;

@Slf4j
@Service
public class ChargePoint {
    private IClientAPI client;
    private ClientCoreProfile core;

    @EventListener(ApplicationReadyEvent.class)
    public void connect() {

        // The core profile is mandatory
        core = new ClientCoreProfile(new ClientCoreEventHandler() {
            @Override
            public ChangeAvailabilityConfirmation handleChangeAvailabilityRequest(ChangeAvailabilityRequest request) {

                log.info(request.toString());
                // ... handle event

                return new ChangeAvailabilityConfirmation(AvailabilityStatus.Accepted);
            }

            @Override
            public GetConfigurationConfirmation handleGetConfigurationRequest(GetConfigurationRequest request) {

                log.info(request.toString());
                // ... handle event

                return null; // returning null means unsupported feature
            }

            @Override
            public ChangeConfigurationConfirmation handleChangeConfigurationRequest(ChangeConfigurationRequest request) {

                log.info(request.toString());
                // ... handle event

                return null; // returning null means unsupported feature
            }

            @Override
            public ClearCacheConfirmation handleClearCacheRequest(ClearCacheRequest request) {

                log.info(request.toString());
                // ... handle event

                ClearCacheConfirmation clearCacheConfirmation = new ClearCacheConfirmation();
                clearCacheConfirmation.setStatus(ClearCacheStatus.Accepted);
                return clearCacheConfirmation;
            }

            @Override
            public DataTransferConfirmation handleDataTransferRequest(DataTransferRequest request) {

                log.info(request.toString());
                // ... handle event

                return null; // returning null means unsupported feature
            }

            @Override
            public RemoteStartTransactionConfirmation handleRemoteStartTransactionRequest(RemoteStartTransactionRequest request) {

                log.info(request.toString());
                // ... handle event

                return null; // returning null means unsupported feature
            }

            @Override
            public RemoteStopTransactionConfirmation handleRemoteStopTransactionRequest(RemoteStopTransactionRequest request) {

                log.info(request.toString());
                // ... handle event

                return null; // returning null means unsupported feature
            }

            @Override
            public ResetConfirmation handleResetRequest(ResetRequest request) {

                log.info(request.toString());
                // ... handle event

                return null; // returning null means unsupported feature
            }

            @Override
            public UnlockConnectorConfirmation handleUnlockConnectorRequest(UnlockConnectorRequest request) {

                log.info(request.toString());
                // ... handle event

                return null; // returning null means unsupported feature
            }
        });
        client = new JSONClient(core);
        client.connect("ws://localhost:8887", null);
    }

    public void sendBootNotification() throws Exception {

        // Use the feature profile to help create event
        Request request = core.createBootNotificationRequest("some vendor", "some model");

        // Client returns a promise which will be filled once it receives a confirmation.
        client.send(request).whenComplete((s, ex) -> log.info(s.toString()));
    }

    @PreDestroy
    public void disconnect() {
        client.disconnect();
    }
}
