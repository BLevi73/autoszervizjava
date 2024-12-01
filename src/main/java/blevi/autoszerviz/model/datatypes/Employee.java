package blevi.autoszerviz.model.datatypes;

public class Employee extends Person {
    
    private String position;

    public Employee(String idNumber, String name, String phoneNumber, String email, String position) {
        super(idNumber, name, phoneNumber, email);
        this.position = position;
    }

    
    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Employee)) {
            return false;
        }
        return super.equals(obj);
    }
}
