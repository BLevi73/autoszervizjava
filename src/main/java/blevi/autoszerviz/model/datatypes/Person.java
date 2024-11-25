package blevi.autoszerviz.model.datatypes;

import java.io.Serializable;

public class Person implements Serializable {
    private String idNumber;
    private String name;
    private String number;
    private String email;

    public Person(String idNumber, String name, String number, String email) {
        this.idNumber = idNumber;
        this.name = name;
        this.number = number;
        this.email = email;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public String getEmail() {
        return email;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNumber(String number) {
        this.number = number;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
}
