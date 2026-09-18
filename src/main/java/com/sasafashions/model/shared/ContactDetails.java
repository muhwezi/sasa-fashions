package com.sasafashions.model.shared;

/**
 * Stores and validates contact information used by
 * customers, employees and suppliers.
 *
 * @author SASA Group
 * @version 1.0
 */
public class ContactDetails {

    private String telephone;
    private String email;

    /**
     * Creates empty contact details.
     */
    public ContactDetails() {
    }

    /**
     * Creates contact details with a telephone and email.
     *
     * @param telephone telephone number
     * @param email email address
     */
    public ContactDetails(
            String telephone,
            String email
    ) {
        this.telephone = telephone;
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Checks whether a telephone number follows a valid
     * Ugandan mobile-number format.
     *
     * Accepted examples:
     * 0772000000
     * +256772000000
     *
     * @param telephone telephone number to validate
     * @return true when the telephone number is valid
     */
    public static boolean isValidUgandanTelephone(
            String telephone
    ) {

        if (telephone == null) {
            return false;
        }

        String cleanedTelephone =
                telephone.trim().replace(" ", "");

        return cleanedTelephone.matches(
                "^(?:\\+256|0)\\d{9}$"
        );
    }

    /**
     * Checks whether an email address has a basic
     * valid structure.
     *
     * @param email email address to validate
     * @return true when the email is empty or valid
     */
    public static boolean isValidEmail(String email) {

        if (email == null || email.isBlank()) {
            return true;
        }

        return email.trim().matches(
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
        );
    }

    /**
     * Removes spaces from a telephone number.
     *
     * @param telephone telephone number to clean
     * @return cleaned telephone number
     */
    public static String cleanTelephone(String telephone) {

        if (telephone == null) {
            return "";
        }

        return telephone.trim().replace(" ", "");
    }
}