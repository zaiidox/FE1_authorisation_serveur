package org.example.util;

import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IsoMessagePrinter {

    private static final Logger logger = LoggerFactory.getLogger(IsoMessagePrinter.class);

    public static void printISOMessage(ISOMsg msg, String title) {
        if (msg == null) {
            logger.trace("{} : Message ISO est null", title);
            return;
        }

        try {
            logger.trace("=== {} ===", title);
            logger.trace("MTI              : {}", msg.getMTI());

            // Affichage de tous les champs présents
            for (int i = 2; i <= 128; i++) {
                if (msg.hasField(i)) {
                    String value = safeGet(msg, i);
                    String fieldName = getFieldName(i);
                    logger.trace("Champ {} ({}) : {}", i, fieldName, value);
                }
            }

            logger.trace("==============================");

        } catch (ISOException e) {
            logger.error("Erreur lors de l'affichage du message ISO", e);
        }
    }

    private static String safeGet(ISOMsg msg, int field) {
        return msg.getString(field);
    }

    private static String getFieldName(int fieldNumber) {
        return switch (fieldNumber) {
            case 2 -> "Numéro de carte (PAN)";
            case 3 -> "Code traitement";
            case 4 -> "Montant";
            case 7 -> "Date/Heure";
            case 11 -> "STAN";
            case 12 -> "Heure locale";
            case 13 -> "Date locale";
            case 14 -> "Expiration";
            case 17 -> "Date capture";
            case 18 -> "Code MCC";
            case 22 -> "Mode d'entrée carte";
            case 24 -> "NII (Network Intl. ID)";
            case 27 -> "Longueur code approbation";
            case 32 -> "ID acquéreur";
            case 35 -> "Piste 2";
            case 37 -> "Référence";
            case 39 -> "Code Réponse";
            case 41 -> "ID terminal";
            case 42 -> "ID commerçant";
            case 43 -> "Nom commerçant";
            case 44 -> "Données additionnelles";
            case 49 -> "Code devise";
            case 60 -> "Type terminal";
            case 61 -> "Émetteur carte";
            case 63 -> "Données libres";
            case 121 -> "Indicateur autorisation (AuthCharInd)";
            case 123 -> "Code données POS";
            case 126 -> "Champ privé";
            default -> "";
        };
    }
}
