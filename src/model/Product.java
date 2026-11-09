package model;

public class Product {
	
    private String code;
    private String name;
    private long priceInCents;

    public Product(String code, String name, long priceInCents) {
        this.code = code;
        this.name = name;
        this.priceInCents = priceInCents;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public long getPriceInCents() {
        return priceInCents;
    }
}