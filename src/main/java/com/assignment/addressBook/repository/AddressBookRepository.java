package com.assignment.addressBook.repository;

import com.assignment.addressBook.model.AddressBookEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AddressBookRepository extends JpaRepository<AddressBookEntity, Long> {


    AddressBookEntity findByPhoneNo(String phoneNo);

    AddressBookEntity findByEmail(String email);

    List<AddressBookEntity> findByFirstName(String firstName);

    List<AddressBookEntity> findByLastName(String lastName);

    List<AddressBookEntity> findByAddress(String address);


    AddressBookEntity findByEmailOrPhoneNo(String email, String phoneNo);

    List<AddressBookEntity> findByFirstNameOrLastName(String firstName, String lastName);

    List<AddressBookEntity> findByFirstNameAndLastName(String firstName, String lastName);

}
