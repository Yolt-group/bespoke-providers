package com.yolt.providers.rabobank.dto.external;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

/**
 * Remittance Information Structured.
 */
public class RemittanceInformationStructured {
    @JsonProperty("reference")
    private String reference = null;

    @JsonProperty("referenceIssuer")
    private String referenceIssuer = null;

    @JsonProperty("referenceType")
    private String referenceType = null;

    public RemittanceInformationStructured reference(String reference) {
        this.reference = reference;
        return this;
    }

    /**
     * The actual reference.
     *
     * @return reference
     **/
    @NotNull
    @Size(min = 1, max = 35)
    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public RemittanceInformationStructured referenceIssuer(String referenceIssuer) {
        this.referenceIssuer = referenceIssuer;
        return this;
    }

    /**
     * Get referenceIssuer
     *
     * @return referenceIssuer
     **/
    @Size(min = 0, max = 35)
    public String getReferenceIssuer() {
        return referenceIssuer;
    }

    public void setReferenceIssuer(String referenceIssuer) {
        this.referenceIssuer = referenceIssuer;
    }

    public RemittanceInformationStructured referenceType(String referenceType) {
        this.referenceType = referenceType;
        return this;
    }

    /**
     * Get referenceType
     *
     * @return referenceType
     **/
    @Size(min = 0, max = 35)
    public String getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(String referenceType) {
        this.referenceType = referenceType;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RemittanceInformationStructured remittanceInformationStructured = (RemittanceInformationStructured) o;
        return Objects.equals(this.reference, remittanceInformationStructured.reference) &&
                Objects.equals(this.referenceIssuer, remittanceInformationStructured.referenceIssuer) &&
                Objects.equals(this.referenceType, remittanceInformationStructured.referenceType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reference, referenceIssuer, referenceType);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class RemittanceInformationStructured {\n");

        sb.append("    reference: ").append(toIndentedString(reference)).append("\n");
        sb.append("    referenceIssuer: ").append(toIndentedString(referenceIssuer)).append("\n");
        sb.append("    referenceType: ").append(toIndentedString(referenceType)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(java.lang.Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}

