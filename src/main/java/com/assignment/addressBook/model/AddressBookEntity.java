package com.assignment.addressBook.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "addrBook")
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"})
public class AddressBookEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_addrbook")
    @SequenceGenerator(name = "hibernate_addrbook", sequenceName = "hibernate_addrbook", initialValue = 1, allocationSize = 1)
    private Long id;

    // Need FirstName, LastName, Email, address, PhoneNo(No can have country code thus it'd be string)
    // There are some redundant annotations and can easily be cleaned up but for an assignment, it kinda works :3

    @NotEmpty(message = "P100 : firstName is required and can not be null or empty.")
    @NotNull(message = "P100 : firstName is required and can not be null or empty.")
    @NotBlank(message = "P100 : firstName is required and can not be null or empty.")
    private String firstName;

    @NotEmpty(message = "P101 : lastName is required and can not be null or empty.")
    @NotNull(message = "P101 : lastName is required and can not be null or empty.")
    @NotBlank(message = "P101 : lastName is required and can not be null or empty.")
    private String lastName;

    // address can be optional so...
    @NotEmpty(message = "P104 : address is required and can not be null or empty.")
    @NotNull(message = "P104 : address is required and can not be null or empty.")
    @NotBlank(message = "P104 : address is required and can not be null or empty.")
    private String address;


    @Column(unique = true)
    @NotEmpty(message = "P102 : Email is required and can not be null or empty.")
    @NotNull(message = "P102 : Email is required and can not be null or empty.")
    @NotBlank(message = "P102 : Email is required and can not be null or empty.")
    @Email(message = "P111 : Not a valid email.")
    private String email;


    @Column(unique = true)
    @NotEmpty(message = "P103 : phoneNo is required and can not be null or empty.")
    @NotNull(message = "P103 : phoneNo is required and can not be null or empty.")
    @NotBlank(message = "P103 : phoneNo is required and can not be null or empty.")
    private String phoneNo;


/*   @Override
    public String toString(){
        return firstName + " "
    }
    */

}
