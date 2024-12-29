package sg.edu.nus.iss.vttp5_ssf_mini_project.models;

import java.time.LocalDate;
import java.time.Period;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import sg.edu.nus.iss.vttp5_ssf_mini_project.utilities.Utility;

public class User {
    
    // for login purposes

  @NotBlank(message = "Username is a mandatory field.")
  @Size(min = 7, message = "Username must have a minimum of 7 characters.")
  @Size(max = 20, message = "Username must have a maximum of 20 characters.")
  private String username; // must be unique

  @NotBlank(message = "Password is a mandatory field.")
  @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[" + Utility.PASSWORD_SPECIAL_CHARS + "])(?=\\S+$).{"+ Utility.PASSWORD_MIN_SIZE + ",}$", 
           message = "Password does not meet the necessary requirements.")
  private String password;

  // for profiling purposes

  @NotEmpty(message = "Name is a mandatory field.")
  @Size(min = 3, message = "Name must have a minimum of 3 characters.")
  @Size(max = 25, message = "Name must have a maximum of 25 characters.")
  private String name;

  @NotEmpty(message = "Email is a mandatory field.")
  @Email(message = "Invalid email format. Must comply with the format: <email_name>@<domain_name>.")
  @Size(max = 50, message = "Email must have a maximum of 50 characters.")
  private String email; // must be unique

  @NotNull(message = "Date of birth is a mandatory field.")
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  @Past(message = "Date of birth must be a past date prior to today.")
  private LocalDate birthDate;

  private Integer age; // not in registration form

  private String userID; // not in registration form, used as Redis Key

  // Constructor Methods

  public User() {}

  public User(String username, String password, String name, String email, LocalDate birthDate, Integer age) {
    this.username = username;
    this.password = password;
    this.name = name;
    this.email = email;
    this.birthDate = birthDate;
    this.age = age;
  }

  // Getter Methods

  public String getUsername() {
    return this.username;
  }

  public String getPassword() {
    return this.password;
  }

  public String getName() {
    return this.name;
  }

  public String getEmail() {
    return this.email;
  }

  public LocalDate getBirthDate() {
    return this.birthDate;
  }

  public Integer getAge() {
    return this.age;
  }

  public String getUserID() {
    return this.userID;
  }

  // Setter Methods

  public void setUsername(String username) {
    this.username = username;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setBirthDate(LocalDate birthDate) {
    int calculatedAge = 0;
    LocalDate currentDate = LocalDate.now();

    if ((birthDate != null) && (currentDate != null)) {
      calculatedAge = Period.between(birthDate, currentDate).getYears();
    }

    this.age = calculatedAge;
    this.birthDate = birthDate;
  }

  public void setAge(Integer age) {
    this.age = age;
  }

  public void setUserID(String userID) {
    this.userID = userID;
  }

  @Override
  public String toString() {
    return "User [username=" + username + ", password=" + password + ", name=" + name + ", email=" + email
        + ", birthDate=" + birthDate + ", age=" + age + ", userID=" + userID + "]";
  } 


}