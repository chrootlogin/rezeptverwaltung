package ch.rootlogin.rezeptverwaltung.type;

public enum ReceiptType {
    MARKDOWN(1),  //calls constructor with value 3
    TEXT(0)
    ;

    private final int typeCode;

    ReceiptType(int typeCode) {
        this.typeCode = typeCode;
    }
}

