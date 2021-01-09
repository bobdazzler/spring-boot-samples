package com.github.richygreat.csms;

import eu.chargetime.ocpp.JSONServer;
import eu.chargetime.ocpp.ServerEvents;
import eu.chargetime.ocpp.feature.profile.ServerCoreEventHandler;
import eu.chargetime.ocpp.feature.profile.ServerCoreProfile;
import eu.chargetime.ocpp.model.Confirmation;
import eu.chargetime.ocpp.model.SessionInformation;
import eu.chargetime.ocpp.model.core.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class CsmsServer {
    private static final String CHARGE_POINT_ID = "cp101";
    private Map<String, String> deviceSessionMap = new HashMap<>();
    private JSONServer server;
    private ServerCoreProfile core;

    @PostConstruct
    public void started() {
        if (server != null)
            return;

        // The core profile is mandatory
        core = new ServerCoreProfile(new ServerCoreEventHandler() {
            @Override
            public AuthorizeConfirmation handleAuthorizeRequest(UUID sessionIndex, AuthorizeRequest request) {

                log.info(request.toString());
                // ... handle event

                return new AuthorizeConfirmation();
            }

            @Override
            public BootNotificationConfirmation handleBootNotificationRequest(UUID sessionIndex, BootNotificationRequest request) {

                log.info(request.toString());
                // ... handle event

                BootNotificationConfirmation bootNotificationConfirmation = new BootNotificationConfirmation();
                bootNotificationConfirmation.setStatus(RegistrationStatus.Accepted);
                bootNotificationConfirmation.setCurrentTime(Calendar.getInstance());
                bootNotificationConfirmation.setInterval(5000);
                return bootNotificationConfirmation;
            }

            @Override
            public DataTransferConfirmation handleDataTransferRequest(UUID sessionIndex, DataTransferRequest request) {

                log.info(request.toString());
                // ... handle event

                return null; // returning null means unsupported feature
            }

            @Override
            public HeartbeatConfirmation handleHeartbeatRequest(UUID sessionIndex, HeartbeatRequest request) {

                log.info(request.toString());
                // ... handle event

                return null; // returning null means unsupported feature
            }

            @Override
            public MeterValuesConfirmation handleMeterValuesRequest(UUID sessionIndex, MeterValuesRequest request) {

                log.info(request.toString());
                // ... handle event

                return null; // returning null means unsupported feature
            }

            @Override
            public StartTransactionConfirmation handleStartTransactionRequest(UUID sessionIndex, StartTransactionRequest request) {

                log.info(request.toString());
                // ... handle event

                return null; // returning null means unsupported feature
            }

            @Override
            public StatusNotificationConfirmation handleStatusNotificationRequest(UUID sessionIndex, StatusNotificationRequest request) {

                log.info(request.toString());
                // ... handle event

                return null; // returning null means unsupported feature
            }

            @Override
            public StopTransactionConfirmation handleStopTransactionRequest(UUID sessionIndex, StopTransactionRequest request) {

                log.info(request.toString());
                // ... handle event

                return null; // returning null means unsupported feature
            }
        });

        server = new JSONServer(core);
        server.open("localhost", 8887, new ServerEvents() {

            @Override
            public void newSession(UUID sessionIndex, SessionInformation information) {
                deviceSessionMap.put(CHARGE_POINT_ID, sessionIndex.toString());
                // sessionIndex is used to send messages.
                log.info("New session " + sessionIndex + ": " + information.getIdentifier());
            }

            @Override
            public void lostSession(UUID sessionIndex) {
                deviceSessionMap.remove(CHARGE_POINT_ID);
                log.info("Session " + sessionIndex + " lost connection");
            }
        });
    }

    @PreDestroy
    public void destroy() {
        deviceSessionMap.clear();
        server.close();
    }

    public void sendClearCacheRequest(String requestId) throws Exception {

        // Use the feature profile to help create event
        ClearCacheRequest request = core.createClearCacheRequest();

        log.info("Request sent for requestId: {} request: {}", requestId, request.toString());
        // Server returns a promise which will be filled once it receives a confirmation.
        // Select the distination client with the sessionIndex integer.
        server.send(UUID.fromString(deviceSessionMap.get(CHARGE_POINT_ID)), request)
                .whenComplete((confirmation, e) -> this.publishConfirmationToTopic(confirmation, e, requestId));
    }

    private void publishConfirmationToTopic(Confirmation confirmation, Throwable e, String requestId) {
        log.info("Confirmation received for requestId: {} confirmation: {}", requestId, confirmation.toString());
    }
}
