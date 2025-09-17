import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

/**
 * Comprehensive test class to demonstrate all pattern-based type handlers.
 * This class contains fields that should trigger various pattern-based handlers
 * for generating realistic test data.
 */
public class TestDataPatternDemo {

    // ==================== PERSONAL INFORMATION ====================

    // Name patterns (NameTypeHandler - priority 7)
    private String firstName;
    private String lastName;
    private String fullName;
    private String displayName;
    private String userName;
    private String nickname;

    // Phone patterns (PhoneTypeHandler - priority 14)
    private String phoneNumber;
    private String mobilePhone;
    private String cellPhone;
    private String contactNumber;
    private String telephoneNumber;
    private String faxNumber;

    // Personal info patterns (PersonalInfoTypeHandler - priority 13)
    private int age;
    private String ageString;
    private LocalDate birthDate;
    private LocalDate dateOfBirth;
    private Date birthday;
    private String dob;

    // Gender patterns (GenderTypeHandler - priority 11)
    private String gender;
    private String sex;
    private String genderCode;

    // Email patterns (EmailTypeHandler - priority 10)
    private String email;
    private String emailAddress;
    private String userEmail;
    private String contactEmail;
    private String loginEmail;

    // ==================== FINANCIAL & IDENTITY ====================

    // SSN patterns (SSNTypeHandler - priority 15)
    private String ssn;
    private String socialSecurityNumber;
    private String social_security_number;
    private String taxId;
    private String nationalId;

    // Credit card patterns (CreditCardTypeHandler - priority 16)
    private String creditCard;
    private String creditCardNumber;
    private String cardNumber;
    private String paymentCard;

    // IBAN patterns (IBANTypeHandler - priority 12)
    private String iban;
    private String bankAccount;
    private String bank_account;
    private String accountNumber;
    private String internationalBankAccount;

    // Currency patterns (CurrencyTypeHandler - priority 8)
    private String currency;
    private String currencyCode;
    private BigDecimal price;
    private Double amount;
    private Float cost;
    private Integer fee;
    private Long balance;
    private String money;
    private BigDecimal salary;

    // ==================== PROFESSIONAL & BUSINESS ====================

    // Job title patterns (JobTitleTypeHandler - priority 9)
    private String jobTitle;
    private String position;
    private String role;
    private String occupation;
    private String designation;
    private String techPosition;
    private String businessRole;

    // Company patterns (CompanyTypeHandler - priority 8)
    private String company;
    private String companyName;
    private String employer;
    private String organization;
    private String business;
    private String corporation;
    private String techCompany;

    // Department patterns (DepartmentTypeHandler - priority 7)
    private String department;
    private String departmentName;
    private String division;
    private String team;
    private String businessUnit;

    // Salary patterns (SalaryTypeHandler - priority 9)
    private BigDecimal annualSalary;
    private Integer wage;
    private Double income;
    private Float hourlyWage;
    private Long compensation;
    private String salaryString;

    // ==================== TECHNICAL & INTERNET ====================

    // IP Address patterns (IPAddressTypeHandler - priority 5)
    private String ipAddress;
    private String ip;
    private String serverIp;
    private String hostAddress;
    private String serverAddress;

    // UUID patterns (UuidTypeHandler - priority 4)
    private String uuid;
    private String guid;
    private String identifier;
    private String uniqueId;
    private String recordId;

    // ==================== GEOGRAPHIC & LOCATION ====================

    // Address patterns (AddressTypeHandler - priority 6)
    private String address;
    private String homeAddress;
    private String streetAddress;
    private String street;
    private String city;
    private String state;
    private String zipCode;
    private String postalCode;
    private String country;

    // Coordinate patterns (CoordinateTypeHandler - priority 6)
    private Double latitude;
    private Float longitude;
    private String lat;
    private String lng;
    private Double coordinateLat;
    private Float coordinateLng;

    // ==================== MIXED PATTERN TESTING ====================

    // Fields that could match multiple patterns - priority should determine winner
    private String userContactEmail;      // Should match EmailTypeHandler (priority 10)
    private String customerPhoneNumber;   // Should match PhoneTypeHandler (priority 14)
    private String employeeSsn;          // Should match SSNTypeHandler (priority 15)
    private String businessAddress;      // Should match AddressTypeHandler (priority 6)
    private String companyPhoneNumber;   // Should match PhoneTypeHandler (priority 14)
    private String salaryAmount;         // Should match SalaryTypeHandler (priority 9)

    // ==================== EDGE CASES ====================

    // Fields with common words that should still trigger patterns
    private String primaryEmail;
    private String mobileContactNumber;
    private String workAddress;
    private String emergencyPhoneNumber;
    private String corporateJobTitle;
    private String monthlyIncome;

    // Fields that should NOT match any patterns (fallback to regular type handlers)
    private String randomString;
    private String description;
    private String notes;
    private String comments;
    private Integer count;
    private Boolean active;

    // Standard getters and setters would go here...
    // (omitted for brevity, but would be generated by the supplier)
}