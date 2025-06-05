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

    @Column(name = "REFERENCE")
    private String reference;

    @Column(name = "TYPE")
    private String type;

    @Column(name = "PAN") // FLD002
    private String pan;

    @Column(name = "CODE_TRAITEMENT")
    private String codeTraitement;

    @Column(name = "MONTANT")
    private BigDecimal montant;

    @Column(name = "DATE_HEURE")
    private LocalDateTime dateHeure;

    @Column(name = "STAN")
    private String stan;

    @Column(name = "TIME_LOCAL")
    private String timeLocal;

    @Column(name = "DATE_LOCAL")
    private String dateLocal;

    @Column(name = "EXPIRATION")
    private String expiration;

    @Column(name = "DATE_CAPTURE")
    private LocalDate dateCapture;

    @Column(name = "MCC")
    private String mcc;

    @Column(name = "POSE_ENTRY_MODE")
    private String poseEntryMode;

    @Column(name = "NII")
    private String nii;

    @Column(name = "RESPONSE_CODE")
    private String responseCode;

    @Column(name = "APPROVAL_CODE_LENGTH")
    private String approvalCodeLength;

    @Column(name = "ACQUIRING_ID")
    private String acquiringId;

    @Column(name = "TRACK2")
    private String track2;

    @Column(name = "TERMINAL_ID")
    private String terminalId;

    @Column(name = "MERCHANT_ID")
    private String merchantId;

    @Column(name = "MERCHANT_NAME")
    private String merchantName;

    @Column(name = "ADDITIONAL_DATA")
    private String additionalData;

    @Column(name = "CURRENCY_CODE")
    private String currencyCode;

    @Column(name = "TERMINAL_TYPE")
    private String terminalType;

    @Column(name = "CARD_ISSUER")
    private String cardIssuer;

    @Column(name = "FREE_DATA")
    private String freeData;

    @Column(name = "AUTH_CHAR_INDICATOR")
    private String authCharIndicator;

    @Column(name = "POS_DATA_CODE")
    private String posDataCode;

    @Column(name = "PRIVATE_FIELD")
    private String privateField;

    @Column(name = "SOURCE_DB")
    private String source;
}
