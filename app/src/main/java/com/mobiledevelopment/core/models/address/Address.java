package com.mobiledevelopment.core.models.address;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.PropertyName;

public class Address {
    public static Address undefinedAddress = new Address(
            "Undefined",
            "Undefined",
            "Undefined Province,Undefined Province,Undefined Province,Undefined Province, ",
            "Undefined",
            true);

    @DocumentId
    private final String id;
    private final String idUser;
    private final String detailAddress;
    private final String label;

    //Need to have this or Firestore won't parse fields starting with "is" properly
    @PropertyName("isPrimary")
    private final Boolean isPrimary;

    // Need to have this for Firebase's toObject() method to work
    public Address() {
        this.id = undefinedAddress.id;
        this.idUser = undefinedAddress.idUser;
        this.detailAddress = undefinedAddress.detailAddress;
        this.label = undefinedAddress.label;
        this.isPrimary = undefinedAddress.isPrimary;
    }

    public Address(Address address) {

        this.id = address.id;
        this.idUser = address.idUser;
        this.detailAddress = address.detailAddress;
        this.label = address.label;
        this.isPrimary = address.isPrimary;
    }

    public Address(
            String id,
            String idUser,
            String detailAddress,
            String label,
            Boolean isPrimary) {

        this.id = id;
        this.idUser = idUser;
        this.detailAddress = detailAddress;
        this.label = label;
        this.isPrimary = isPrimary;
    }

    public String getId() {
        return id;
    }

    public String getIdUser() {
        return idUser;
    }

    public String getDetailAddress() {
        return detailAddress;
    }

    public String getLabel() {
        return label;
    }

    //Need to have this or Firestore won't parse fields starting with "is" properly
    @PropertyName("isPrimary")
    public Boolean isPrimary() {
        return isPrimary;
    }

    @NonNull
    @Override
    public String toString() {
        return "id= " + getId() + "; label= " + label + "; idUser= " + idUser + "; detailAddress: " + detailAddress + "; isPrimary: " + isPrimary;
    }

    public static String toNormalizedString(String detailAddressAsString) {
        DetailAddress detailAddress = new DetailAddress(detailAddressAsString);
        return detailAddress.street.trim() + ", " + detailAddress.ward + ", " + detailAddress.district + ", " + detailAddress.province;
    }

    public String toNormalizedString() {
        DetailAddress detailAddress = new DetailAddress(this);
        return detailAddress.street.trim() + ", " + detailAddress.ward + ", " + detailAddress.district + ", " + detailAddress.province;
    }

    public static class DetailAddress extends Address {
        public static DetailAddress undefinedDetailAddress = new DetailAddress(undefinedAddress);
        private final String province;
        private final String district;
        private final String ward;
        private final String street;

        public DetailAddress(Address address) {
            super(address);
            String[] addressTokens = address.detailAddress.split(",");

            assert addressTokens.length >= 4 :
                    "Invalid Address: Address needs to have at least 4 parts, separated by commas" +
                            "Example: ABC Province, ZY District, WH Ward, UI Street";

            province = addressTokens[0].replace("Thành phố", "").replace("Tỉnh", "").trim();
            district = addressTokens[1].trim();
            ward = addressTokens[2].trim();

            StringBuilder streetStringBuilder = new StringBuilder();
            for (int i = 3; i < addressTokens.length; i++) {
                streetStringBuilder.append(addressTokens[i]);
                if (i != addressTokens.length - 1) {
                    streetStringBuilder.append(", ");
                }
            }
            street = streetStringBuilder.toString();
        }

        public DetailAddress(String detailAddress) {
            String[] addressTokens = detailAddress.split(",");

            assert addressTokens.length >= 4 :
                    "Invalid Address: Address needs to have at least 4 parts, separated by commas" +
                            "Example: ABC Province, ZY District, WH Ward, UI Street";

            province = addressTokens[0].replace("Thành phố", "").replace("Tỉnh", "").trim();
            district = addressTokens[1].trim();
            ward = addressTokens[2].trim();

            StringBuilder streetStringBuilder = new StringBuilder();
            for (int i = 3; i < addressTokens.length; i++) {
                streetStringBuilder.append(addressTokens[i]);
                if (i != addressTokens.length - 1) {
                    streetStringBuilder.append(", ");
                }
            }
            street = streetStringBuilder.toString();
        }

        public String getProvince() {
            return province;
        }

        public String getDistrict() {
            return district;
        }

        public String getWard() {
            return ward;
        }

        public String getStreet() {
            return street;
        }
    }
}
