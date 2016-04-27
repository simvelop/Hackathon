package hr.droidcon.conference.hack.timeline;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Hrvoje Kozak on 30/03/16.
 */
public class Speaker {

    private String uid;
    private String image;

    @SerializedName("gn")
    private String firstName;
    @SerializedName("sn")
    private String lastName;
    @SerializedName("org")
    private String company;
    @SerializedName("position")
    private String companyPosition;
    @SerializedName("org_uri")
    private String companyURI;
    @SerializedName("description_short")
    private String description;

    public String getUid() {
        return uid;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getCompanyPosition() {
        return companyPosition;
    }

    public void setCompanyPosition(String companyPosition) {
        this.companyPosition = companyPosition;
    }

    public String getCompanyURI() {
        return companyURI;
    }

    public void setCompanyURI(String companyURI) {
        this.companyURI = companyURI;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
