package org.example.businessLogic;

import org.example.modeles.Autorisations;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class AutorisationValidator {

    // Format "MMyy" pour le champ 14 (Expiration)
    private static final DateTimeFormatter EXPIRY_FORMAT = DateTimeFormatter.ofPattern("MMyy");

    /**
     * Valide l’objet Autorisations et retourne la liste des messages d’erreur.
     * ===> Si la liste n’est pas vide, cela signifie qu’une ou plusieurs validations ont échoué.
     */
    public static List<String> validate(Autorisations a) {
        List<String> errors = new ArrayList<>();


        String exp = a.getExpiration();
        if (exp == null) {
            errors.add("La date d'expiration est manquante.");
        } else {
            try {
                YearMonth expiryYM = YearMonth.parse(exp, EXPIRY_FORMAT);
                YearMonth nowYM = YearMonth.now();
                if (expiryYM.isBefore(nowYM)) {
                    errors.add("La date d'expiration est déjà passée.");
                }
            } catch (DateTimeParseException e) {
                errors.add("Le format de la date d'expiration doit être MMYY (ex : '1225').");
            }
        }

        // (Vous pouvez ajouter d'autres règles métier ici : montant > 0, MCC valide, etc.)

        return errors;
    }
}
