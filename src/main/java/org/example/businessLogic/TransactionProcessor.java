package org.example.businessLogic;

import org.example.modeles.Autorisations;
import org.example.services.AutorisationsService;
import org.example.util.IsoMessagePrinter;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.ISO87APackager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class TransactionProcessor {

    private static final Logger logger = LoggerFactory.getLogger(TransactionProcessor.class);

    private final ISO87APackager packager;
    private final AutorisationsService autorisationsService;
    private final IsoMessageResponse messageHandler;

    @Autowired
    public TransactionProcessor(AutorisationsService autorisationsService,
                                IsoMessageResponse messageHandler) {
        this.packager = new ISO87APackager();
        this.autorisationsService = autorisationsService;
        this.messageHandler = messageHandler;
    }

    public void processTransaction(byte[] isoBytes, Socket clientSocket) {
        try {
            logger.trace("Réception d'un message ISO. Début du traitement...");

            ISOMsg isoMsg = new ISOMsg();
            isoMsg.setPackager(packager);
            isoMsg.unpack(isoBytes);
            IsoMessagePrinter.printISOMessage(isoMsg, "Message Reçu (Processor)");
            logger.trace("MTI reçue = {}", isoMsg.getMTI());

            if (!"0100".equals(isoMsg.getMTI())) {
                logger.debug("MTI ignorée : {}", isoMsg.getMTI());
                return;
            }

            // Construire l’entité Autorisations pour la REQUÊTE
            Autorisations reqAuth = new Autorisations();
            reqAuth.setReference(isoMsg.hasField(37) ? isoMsg.getString(37) : "");
            reqAuth.setType("REQUEST");
            reqAuth.setPan(isoMsg.hasField(2) ? isoMsg.getString(2) : "");
            reqAuth.setCodeTraitement(isoMsg.hasField(3) ? isoMsg.getString(3) : "");

            if (isoMsg.hasField(4)) {
                String montantStr = isoMsg.getString(4);
                try {
                    BigDecimal montant = new BigDecimal(montantStr);
                    reqAuth.setMontant(montant);
                } catch (NumberFormatException nfe) {
                    logger.error("Impossible de parser le montant '{}'", montantStr, nfe);
                    reqAuth.setMontant(BigDecimal.ZERO);
                }
            } else {
                reqAuth.setMontant(null);
            }

            if (isoMsg.hasField(7)) {
                String dtStr = isoMsg.getString(7);
                try {
                    int forcedYear = 2025;
                    MonthDay md = MonthDay.parse(dtStr.substring(0, 4), DateTimeFormatter.ofPattern("MMdd"));
                    LocalTime lt = LocalTime.parse(dtStr.substring(4), DateTimeFormatter.ofPattern("HHmmss"));
                    reqAuth.setDateHeure(LocalDateTime.of(
                            LocalDate.of(forcedYear, md.getMonth(), md.getDayOfMonth()), lt));
                } catch (Exception e) {
                    logger.error("Impossible de parser la dateHeure ISO (champ 7)='{}'", dtStr, e);
                    reqAuth.setDateHeure(null);
                }
            } else {
                reqAuth.setDateHeure(null);
            }

            reqAuth.setStan(isoMsg.hasField(11) ? isoMsg.getString(11) : "");
            reqAuth.setTimeLocal(isoMsg.hasField(12) ? isoMsg.getString(12) : "");
            reqAuth.setDateLocal(isoMsg.hasField(13) ? isoMsg.getString(13) : "");
            reqAuth.setExpiration(isoMsg.hasField(14) ? isoMsg.getString(14) : "");

            if (isoMsg.hasField(17)) {
                String cap = isoMsg.getString(17);
                try {
                    MonthDay mdCap = MonthDay.parse(cap, DateTimeFormatter.ofPattern("MMdd"));
                    reqAuth.setDateCapture(LocalDate.of(
                            LocalDate.now().getYear(), mdCap.getMonth(), mdCap.getDayOfMonth()));
                } catch (Exception e) {
                    logger.error("Impossible de parser dateCapture (champ 17)='{}'", cap, e);
                    reqAuth.setDateCapture(null);
                }
            } else {
                reqAuth.setDateCapture(null);
            }

            reqAuth.setMcc(isoMsg.hasField(18) ? isoMsg.getString(18) : "");
            reqAuth.setPoseEntryMode(isoMsg.hasField(22) ? isoMsg.getString(22) : "");
            reqAuth.setNii(isoMsg.hasField(24) ? isoMsg.getString(24) : "");
            reqAuth.setApprovalCodeLength(isoMsg.hasField(27) ? isoMsg.getString(27) : "");
            reqAuth.setAcquiringId(isoMsg.hasField(32) ? isoMsg.getString(32) : "");
            reqAuth.setTrack2(isoMsg.hasField(35) ? isoMsg.getString(35) : "");
            reqAuth.setTerminalId(isoMsg.hasField(41) ? isoMsg.getString(41) : "");
            reqAuth.setMerchantId(isoMsg.hasField(42) ? isoMsg.getString(42) : "");
            reqAuth.setMerchantName(isoMsg.hasField(43) ? isoMsg.getString(43) : "");
            reqAuth.setAdditionalData(isoMsg.hasField(48) ? isoMsg.getString(48) : "");
            reqAuth.setCurrencyCode(isoMsg.hasField(49) ? isoMsg.getString(49) : "");
            reqAuth.setTerminalType(isoMsg.hasField(60) ? isoMsg.getString(60) : "");
            reqAuth.setCardIssuer(isoMsg.hasField(61) ? isoMsg.getString(61) : "");
            reqAuth.setFreeData(isoMsg.hasField(63) ? isoMsg.getString(63) : "");
            reqAuth.setAuthCharIndicator(isoMsg.hasField(121) ? isoMsg.getString(121) : "");
            reqAuth.setPosDataCode(isoMsg.hasField(123) ? isoMsg.getString(123) : "");
            reqAuth.setPrivateField(isoMsg.hasField(126) ? isoMsg.getString(126) : "");
            reqAuth.setSource("FE1");
            reqAuth.setResponseCode(null);

            autorisationsService.saveAutorisations(reqAuth);
            logger.debug("Requête sauvegardée : REF={}, PAN={}", reqAuth.getReference(), reqAuth.getPan());

            List<String> erreurs = AutorisationValidator.validate(reqAuth);
            if (!erreurs.isEmpty()) {
                String detail = String.join("; ", erreurs);
                logger.warn("Validation échouée pour REF={}. Détail : {}", reqAuth.getReference(), detail);

                Autorisations respRefus = new Autorisations();
                respRefus.setReference(reqAuth.getReference());
                respRefus.setType("RESPONSE");
                respRefus.setPan(reqAuth.getPan());
                respRefus.setCodeTraitement(reqAuth.getCodeTraitement());
                respRefus.setMontant(reqAuth.getMontant());
                if (reqAuth.getDateHeure() != null) {
                    respRefus.setDateHeure(reqAuth.getDateHeure().plusSeconds(1));
                } else {
                    respRefus.setDateHeure(LocalDateTime.now());
                }
                respRefus.setStan(reqAuth.getStan());
                respRefus.setTimeLocal(reqAuth.getTimeLocal());
                respRefus.setDateLocal(reqAuth.getDateLocal());
                respRefus.setExpiration(reqAuth.getExpiration());
                respRefus.setDateCapture(reqAuth.getDateCapture());
                respRefus.setMcc(reqAuth.getMcc());
                respRefus.setPoseEntryMode(reqAuth.getPoseEntryMode());
                respRefus.setNii(reqAuth.getNii());
                respRefus.setApprovalCodeLength(reqAuth.getApprovalCodeLength());
                respRefus.setAcquiringId(reqAuth.getAcquiringId());
                respRefus.setTrack2(reqAuth.getTrack2());
                respRefus.setTerminalId(reqAuth.getTerminalId());
                respRefus.setMerchantId(reqAuth.getMerchantId());
                respRefus.setMerchantName(reqAuth.getMerchantName());
                respRefus.setAdditionalData(reqAuth.getAdditionalData());
                respRefus.setCurrencyCode(reqAuth.getCurrencyCode());
                respRefus.setTerminalType(reqAuth.getTerminalType());
                respRefus.setCardIssuer(reqAuth.getCardIssuer());
                respRefus.setFreeData(reqAuth.getFreeData());
                respRefus.setAuthCharIndicator(reqAuth.getAuthCharIndicator());
                respRefus.setPosDataCode(reqAuth.getPosDataCode());
                respRefus.setPrivateField(reqAuth.getPrivateField());
                respRefus.setSource("FE1");           // ou "FE2" si souhaité
                respRefus.setResponseCode("05");      // code « refus »
                autorisationsService.saveAutorisations(respRefus);

                messageHandler.sendAuthResponse(isoMsg, clientSocket, "05", detail);
                return; // on interrompt le traitement après avoir persisté la réponse refusée
            }

            messageHandler.sendAuthResponse(isoMsg, clientSocket, "00", null);
            logger.info("Réponse ISO (0110, 00) envoyée pour REF={}", reqAuth.getReference());

            Autorisations respOk = new Autorisations();
            respOk.setReference(reqAuth.getReference());
            respOk.setType("RESPONSE");
            respOk.setPan(reqAuth.getPan());
            respOk.setCodeTraitement(reqAuth.getCodeTraitement());
            respOk.setMontant(reqAuth.getMontant());
            if (reqAuth.getDateHeure() != null) {
                respOk.setDateHeure(reqAuth.getDateHeure().plusSeconds(1));
            } else {
                respOk.setDateHeure(LocalDateTime.now());
            }
            respOk.setStan(reqAuth.getStan());
            respOk.setTimeLocal(reqAuth.getTimeLocal());
            respOk.setDateLocal(reqAuth.getDateLocal());
            respOk.setExpiration(reqAuth.getExpiration());
            respOk.setDateCapture(reqAuth.getDateCapture());
            respOk.setMcc(reqAuth.getMcc());
            respOk.setPoseEntryMode(reqAuth.getPoseEntryMode());
            respOk.setNii(reqAuth.getNii());
            respOk.setApprovalCodeLength(reqAuth.getApprovalCodeLength());
            respOk.setAcquiringId(reqAuth.getAcquiringId());
            respOk.setTrack2(reqAuth.getTrack2());
            respOk.setTerminalId(reqAuth.getTerminalId());
            respOk.setMerchantId(reqAuth.getMerchantId());
            respOk.setMerchantName(reqAuth.getMerchantName());
            respOk.setAdditionalData(reqAuth.getAdditionalData());
            respOk.setCurrencyCode(reqAuth.getCurrencyCode());
            respOk.setTerminalType(reqAuth.getTerminalType());
            respOk.setCardIssuer(reqAuth.getCardIssuer());
            respOk.setFreeData(reqAuth.getFreeData());
            respOk.setAuthCharIndicator(reqAuth.getAuthCharIndicator());
            respOk.setPosDataCode(reqAuth.getPosDataCode());
            respOk.setPrivateField(reqAuth.getPrivateField());
            respOk.setSource("FE1");
            respOk.setResponseCode("00");
            autorisationsService.saveAutorisations(respOk);

            logger.info("Réponse Autorisations (00) sauvegardée pour REF={}", reqAuth.getReference());

        } catch (ISOException e) {
            logger.error("ISOException lors du traitement", e);
        } catch (IOException e) {
            logger.error("IOException lors de l’envoi de la réponse", e);
        } catch (Exception e) {
            logger.error("Erreur inattendue dans processTransaction", e);
        }
    }
}
