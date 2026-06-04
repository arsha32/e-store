package com.example.ecommerce.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
 
import com.example.ecommerce.config.AuthUtil;
import com.example.ecommerce.model.Customer;
import com.example.ecommerce.responseDTO.AddressDTO;
import com.example.ecommerce.service.AddressService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class AddressController {
    
    @Autowired
    private AuthUtil authUtil;

    @Autowired
    private AddressService addressService;
    
    @PostMapping("address")
    public ResponseEntity<AddressDTO> createAddress(@Valid @RequestBody AddressDTO addressDTO)
    {
        Customer customer = authUtil.loggedInUser();
        AddressDTO response = addressService.createAddress(addressDTO, customer);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    @GetMapping("address")
    public ResponseEntity<List<AddressDTO>> getAllAddress()
    {
        List<AddressDTO> response = addressService.getAllAddress();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("address/{addressId}")
    public ResponseEntity<AddressDTO> getAddressById(@PathVariable Long addressId)
    {
        
        AddressDTO response = addressService.getAddressById(addressId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("users/address")
    public ResponseEntity<List<AddressDTO>> getUserAddress()
    {
        Customer customer = authUtil.loggedInUser();
        List<AddressDTO> response = addressService.getUserAddress(customer);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("address/{addressId}")
    public ResponseEntity<AddressDTO> UpdateAddress(@PathVariable Long addressId, @RequestBody AddressDTO addressDTO)
    {
        AddressDTO response = addressService.UpdateAddress(addressId, addressDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("address/{addressId}")
    public ResponseEntity<String> DeleteAddress(@PathVariable Long addressId)
    {
        String response = addressService.DeleteAddress(addressId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
