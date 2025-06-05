package org.example.modeles;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "AUTORISATIONS")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Autorisations {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "REFERENCE") // FLD037
    private String reference;

    @Column(name = "TYPE") // soit "REQUEST" soit "RESPONSE"
    private String type;

    @Column(name = "PAN") // FLD002
    private String pan;

    @Column(name = "CODE_TRAITEMENT") // FLD003
    private String codeTraitement;

    @Column(name = "MONTANT")
    private BigDecimal montant;

    // Timestamp (DATE_HEURE)
    @Column(name = "DATE_HEURE")
    private LocalDateTime dateHeure;

    @Column(name = "STAN") // FLD011
    private String stan;

    @Column(name = "TIME_LOCAL") // FLD012
    private String timeLocal;

    @Column(name = "DATE_LOCAL") // FLD013
    private String dateLocal;

    @Column(name = "EXPIRATION") // FLD014
    private String expiration;

    @Column(name = "DATE_CAPTURE")
    private LocalDate dateCapture;

    @Column(name = "MCC") // FLD018
    private String mcc;

    @Column(name = "POSE_ENTRY_MODE") // FLD022
    private String poseEntryMode;

    @Column(name = "NII") // FLD024
    private String nii;

    @Column(name = "RESPONSE_CODE") // FLD025
    private String responseCode;

    @Column(name = "APPROVAL_CODE_LENGTH") // FLD027
    private String approvalCodeLength;

    @Column(name = "ACQUIRING_ID") // FLD032
    private String acquiringId;

    @Column(name = "TRACK2") // FLD035
    private String track2;

    @Column(name = "TERMINAL_ID") // FLD041
    private String terminalId;

    @Column(name = "MERCHANT_ID") // FLD042
    private String merchantId;

    @Column(name = "MERCHANT_NAME") // FLD043
    private String merchantName;

    @Column(name = "ADDITIONAL_DATA") // FLD048
    private String additionalData;

    @Column(name = "CURRENCY_CODE") // FLD049
    private String currencyCode;

    @Column(name = "TERMINAL_TYPE") // FLD060
    private String terminalType;

    @Column(name = "CARD_ISSUER") // FLD061
    private String cardIssuer;

    @Column(name = "FREE_DATA") // FLD063
    private String freeData;

    @Column(name = "AUTH_CHAR_INDICATOR") // FLD121
    private String authCharIndicator;

    @Column(name = "POS_DATA_CODE") // FLD123
    private String posDataCode;

    @Column(name = "PRIVATE_FIELD") // FLD126
    private String privateField;

    @Column(name = "SOURCE_DB") // champ personnalisé
    private String source;
}
