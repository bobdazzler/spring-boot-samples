package com.softwok.csms;

import eu.chargetime.ocpp.JSONServer;
import eu.chargetime.ocpp.ServerEvents;
import eu.chargetime.ocpp.feature.profile.ServerCoreEventHandler;
import eu.chargetime.ocpp.feature.profile.ServerCoreProfile;
import eu.chargetime.ocpp.model.Confirmation;
import eu.chargetime.ocpp.model.SessionInformation;
import eu.chargetime.ocpp.model.core.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class CsmsServer {
    private final Map<String, UUID> deviceSessionMap = new HashMap<>();
    private JSONServer server;
    private ServerCoreProfile core;

    @PostConstruct
    public void started() throws KeyStoreException, IOException, UnrecoverableKeyException,
            NoSuchAlgorithmException, KeyManagementException, CertificateException {
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

        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        File kf = new ClassPathResource("ocpp.jks").getFile();
        ks.load(new FileInputStream(kf), "secret".toCharArray());

        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(ks, "secret".toCharArray());
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        tmf.init(ks);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

        server.enableWSS(sslContext);
        server.open("localhost", 8887, new ServerEvents() {

            @Override
            public void newSession(UUID sessionIndex, SessionInformation information) {
                String chargePointId = information.getIdentifier().replace("/", "");
                deviceSessionMap.put(chargePointId, sessionIndex);
                // sessionIndex is used to send messages.
                log.info("New session sessionIndex: {} chargePointId: {}", sessionIndex, chargePointId);
            }

            @Override
            public void lostSession(UUID sessionIndex) {
                deviceSessionMap.entrySet().removeIf(entry -> sessionIndex.equals(entry.getValue()));
                log.info("lostSession sessionIndex: {} lost connection", sessionIndex);
            }
        });
    }

    @PreDestroy
    public void destroy() {
        log.info("destroy deviceSessionMap: {}", deviceSessionMap);
        deviceSessionMap.clear();
        server.close();
    }

    public void sendClearCacheRequest(String requestId, String chargePointId) throws Exception {

        // Use the feature profile to help create event
        ClearCacheRequest request = core.createClearCacheRequest();

        log.info("Request sent for requestId: {} request: {}", requestId, request.toString());
        // Server returns a promise which will be filled once it receives a confirmation.
        // Select the distination client with the sessionIndex integer.

        UUID sessionId = deviceSessionMap.entrySet().stream().filter(entry -> chargePointId.equals(entry.getKey())).findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)).getValue();

        server.send(sessionId, request)
                .whenComplete((confirmation, e) -> this.publishConfirmationToTopic(confirmation, e, requestId));
    }

    private void publishConfirmationToTopic(Confirmation confirmation, Throwable e, String requestId) {
        log.info("Confirmation received for requestId: {} confirmation: {}", requestId, confirmation.toString());
    }
}
