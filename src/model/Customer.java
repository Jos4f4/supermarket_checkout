package model;

public class Customer {
	
    private String cpf;
    private String name;
    private long tabCreditLimitInCents;

    public Customer(String cpf, String name, long tabCreditLimitInCents) {
        this.cpf = cpf;
        this.name = name;
        this.tabCreditLimitInCents = tabCreditLimitInCents;
    }

    public String getCpf() {
        return cpf;
    }

    public String getName() {
        return name;
    }

    public long getTabCreditLimitInCents() {
        return tabCreditLimitInCents;
    }

    public void deductTabCredit(long amountInCents) {
        this.tabCreditLimitInCents -= amountInCents;
    }
}