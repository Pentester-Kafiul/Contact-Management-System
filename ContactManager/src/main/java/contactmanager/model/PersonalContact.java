package contactmanager.model;

public class PersonalContact extends Contact {
    private static final long serialVersionUID = 1L;
    
    private String relationship;
    private String birthday; // Keep as String for simplicity

    public PersonalContact(String id, String name, String phone, String email, String address, 
                          String relationship, String birthday) {
        super(id, name, phone, email, address);
        this.relationship = relationship;
        this.birthday = birthday;
    }

    public String getRelationship() { return relationship; }
    public void setRelationship(String relationship) { this.relationship = relationship; }

    public String getBirthday() { return birthday; }
    public void setBirthday(String birthday) { this.birthday = birthday; }

    @Override
    public String getType() {
        return "Personal";
    }

    @Override
    public String getAdditionalInfo() {
        return String.format("Relationship: %s, Birthday: %s", relationship, birthday);
    }
}
