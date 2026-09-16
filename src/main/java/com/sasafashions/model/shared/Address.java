package com.sasafashions.model.shared;

/**
 * Represents address information that can be used by
 * customers, employees and suppliers.
 *
 * @author HP
 * @version 1.0
 */
public class Address {

    private String addressLine;
    private String district;
    private String country;

    /**
     * Creates an empty address.
     */
    public Address() {
        this.country = "Uganda";
    }

    /**
     * Creates a complete address.
     *
     * @param addressLine village, town or street
     * @param district district
     * @param country country
     */
    public Address(
            String addressLine,
            String district,
            String country
    ) {
        this.addressLine = addressLine;
        this.district = district;
        this.country = country;
    }

    public String getAddressLine() {
        return addressLine;
    }

    public void setAddressLine(String addressLine) {
        this.addressLine = addressLine;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * Combines the address fields into one readable address.
     *
     * @return formatted address
     */
    public String getFormattedAddress() {

        StringBuilder formattedAddress =
                new StringBuilder();

        if (addressLine != null
                && !addressLine.isBlank()) {

            formattedAddress.append(
                    addressLine.trim()
            );
        }

        if (district != null
                && !district.isBlank()) {

            if (!formattedAddress.isEmpty()) {
                formattedAddress.append(", ");
            }

            formattedAddress.append(
                    district.trim()
            );
        }

        if (country != null
                && !country.isBlank()) {

            if (!formattedAddress.isEmpty()) {
                formattedAddress.append(", ");
            }

            formattedAddress.append(
                    country.trim()
            );
        }

        return formattedAddress.toString();
    }

    /**
     * Provides a readable representation of the address.
     *
     * @return formatted address
     */
    @Override
    public String toString() {
        return getFormattedAddress();
    }
}