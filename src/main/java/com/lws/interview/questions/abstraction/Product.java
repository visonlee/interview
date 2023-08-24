package com.lws.interview.questions.abstraction;

import java.math.BigDecimal;

public abstract class Product {
    private String name;
    private String assetClass;
    private String currency;
}

class BondProduct extends Product {
    private int maturityDate;
    private BigDecimal couponRate;
    //...
}

class UintTrustProduct extends Product {
    private String fundType;
    private BigDecimal price;
    private String funManager;
    private boolean allowSwitch;

    //..
}
