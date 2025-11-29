package contactmanager.model;

public class BusinessContact extends Contact {
    private static final long serialVersionUID = 1L;
    
    private String company;
    private String jobTitle;

    public BusinessContact(String id, String name, String phone, String email, String address, 
                          String company, String jobTitle) {
        super(id, name, phone, email, address);
        this.company = company;
        this.jobTitle = jobTitle;
    }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }

    @Override
    public String getType() {
        return "Business";
    }

    @Override
    public String getAdditionalInfo() {
        return String.format("Company: %s, Job Title: %s", company, jobTitle);
    }
}
