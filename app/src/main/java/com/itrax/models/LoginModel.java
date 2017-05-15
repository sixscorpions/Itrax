package com.itrax.models;

import java.util.List;

/**
 * Created by shankar on 4/30/2017.
 */

public class LoginModel extends Model {

    private int Id;
    private int OrganizationId;
    private int ZoneId;
    private int BranchId;
    private String FirstName;
    private String MiddleName;
    private String LastName;
    private String DateOfBirth;
    private String Email;
    private String LicenceNumber;
    private String LicenceIssuedDate;
    private String LicenceExpiryDate;
    private String LicenceCopy;
    private String HealthRecord;
    private String PoliceRecord;
    private String Mobile;
    private AddressModel addressModel;
    private List<RolesModel> rolesModels;
    private String City;
    private String State;
    private String Country;
    private String Pincode;
    private String CreateDate;
    private List<String> DeviceIds;
    private boolean IsActive;
    private String LastUpdatedOn;
    private String UpdateBy;
    private String VehicleNumber;

    public int getId() {
        return Id;
    }

    public void setId(int Id) {
        this.Id = Id;
    }

    public int getOrganizationId() {
        return OrganizationId;
    }

    public void setOrganizationId(int OrganizationId) {
        this.OrganizationId = OrganizationId;
    }

    public int getZoneId() {
        return ZoneId;
    }

    public void setZoneId(int ZoneId) {
        this.ZoneId = ZoneId;
    }

    public int getBranchId() {
        return BranchId;
    }

    public void setBranchId(int BranchId) {
        this.BranchId = BranchId;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String FirstName) {
        this.FirstName = FirstName;
    }

    public String getMiddleName() {
        return MiddleName;
    }

    public void setMiddleName(String MiddleName) {
        this.MiddleName = MiddleName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String LastName) {
        this.LastName = LastName;
    }

    public String getDateOfBirth() {
        return DateOfBirth;
    }

    public void setDateOfBirth(String DateOfBirth) {
        this.DateOfBirth = DateOfBirth;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String Email) {
        this.Email = Email;
    }

    public String getLicenceNumber() {
        return LicenceNumber;
    }

    public void setLicenceNumber(String LicenceNumber) {
        this.LicenceNumber = LicenceNumber;
    }

    public String getLicenceIssuedDate() {
        return LicenceIssuedDate;
    }

    public void setLicenceIssuedDate(String LicenceIssuedDate) {
        this.LicenceIssuedDate = LicenceIssuedDate;
    }

    public String getLicenceExpiryDate() {
        return LicenceExpiryDate;
    }

    public void setLicenceExpiryDate(String LicenceExpiryDate) {
        this.LicenceExpiryDate = LicenceExpiryDate;
    }

    public String getLicenceCopy() {
        return LicenceCopy;
    }

    public void setLicenceCopy(String LicenceCopy) {
        this.LicenceCopy = LicenceCopy;
    }

    public String getHealthRecord() {
        return HealthRecord;
    }

    public void setHealthRecord(String HealthRecord) {
        this.HealthRecord = HealthRecord;
    }

    public String getPoliceRecord() {
        return PoliceRecord;
    }

    public void setPoliceRecord(String PoliceRecord) {
        this.PoliceRecord = PoliceRecord;
    }

    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String Mobile) {
        this.Mobile = Mobile;
    }

    public AddressModel getAddressModel() {
        return addressModel;
    }

    public void setAddressModel(AddressModel addressModel) {
        this.addressModel = addressModel;
    }

    public List<RolesModel> getRolesModels() {
        return rolesModels;
    }

    public void setRolesModels(List<RolesModel> rolesModels) {
        this.rolesModels = rolesModels;
    }

    public void setDeviceIds(List<String> deviceIds) {
        DeviceIds = deviceIds;
    }

    public boolean isActive() {
        return IsActive;
    }

    public void setActive(boolean active) {
        IsActive = active;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String City) {
        this.City = City;
    }

    public String getState() {
        return State;
    }

    public void setState(String State) {
        this.State = State;
    }

    public String getCountry() {
        return Country;
    }

    public void setCountry(String Country) {
        this.Country = Country;
    }

    public String getPincode() {
        return Pincode;
    }

    public void setPincode(String Pincode) {
        this.Pincode = Pincode;
    }

    public String getCreateDate() {
        return CreateDate;
    }

    public void setCreateDate(String CreateDate) {
        this.CreateDate = CreateDate;
    }

    public boolean getIsActive() {
        return IsActive;
    }

    public void setIsActive(boolean IsActive) {
        this.IsActive = IsActive;
    }

    public String getLastUpdatedOn() {
        return LastUpdatedOn;
    }

    public void setLastUpdatedOn(String LastUpdatedOn) {
        this.LastUpdatedOn = LastUpdatedOn;
    }

    public String getUpdateBy() {
        return UpdateBy;
    }

    public void setUpdateBy(String UpdateBy) {
        this.UpdateBy = UpdateBy;
    }

    public String getVehicleNumber() {
        return VehicleNumber;
    }

    public void setVehicleNumber(String VehicleNumber) {
        this.VehicleNumber = VehicleNumber;
    }
}
