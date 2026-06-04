package com.example.ecommerce.service;
 
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.ecommerce.model.Address;
import com.example.ecommerce.model.Customer;
import com.example.ecommerce.repository.AddressRepository;
import com.example.ecommerce.repository.CustomerRepository;
import com.example.ecommerce.responseDTO.AddressDTO;

 

@Service
public class AddressService {

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private CustomerRepository customerRepository;

    public AddressDTO createAddress(AddressDTO addressDTO, Customer customer) {
        Address address= modelMapper.map(addressDTO, Address.class);
        List<Address> customerAddress = customer.getAddresses();
        customerAddress.add(address);

        customer.setAddresses(customerAddress);
        address.setCustomer(customer);
        Address savedAddress= addressRepository.save(address);
        return modelMapper.map(savedAddress, AddressDTO.class);
    }

    public List<AddressDTO> getAllAddress() {
         List<Address> address = addressRepository.findAll();
         return address.stream().map( res -> modelMapper.map(res, AddressDTO.class)).toList();
    }

    public AddressDTO getAddressById(Long addressId) {
        Address address=addressRepository.findById(addressId)
        .orElseThrow(()-> new ResponseStatusException(HttpStatus.BAD_REQUEST,"Address this id is not present"));
        return modelMapper.map(address, AddressDTO.class);
    }

    public List<AddressDTO> getUserAddress(Customer customer) {
        List<Address> address = customer.getAddresses();
         return address.stream().map( res -> modelMapper.map(res, AddressDTO.class)).toList();
    }

	public AddressDTO UpdateAddress(Long addressId, AddressDTO addressDTO) {
		Address addressDB = addressRepository.findById(addressId)
         .orElseThrow(()-> new ResponseStatusException(HttpStatus.BAD_REQUEST,"Address this id is not present"));

         addressDB.setBuildingName(addressDTO.getBuildingName());
         addressDB.setStreet(addressDTO.getStreet());
         addressDB.setCity(addressDTO.getCity());
         addressDB.setState(addressDTO.getState());
         addressDB.setCountry(addressDTO.getCountry());
         addressDB.setPincode(addressDTO.getPincode());
         Address updatedResponse= addressRepository.save(addressDB);
         
         Customer customer=addressDB.getCustomer();
         List<Address> customerAddresses=customer.getAddresses();
         customerAddresses.removeIf(res-> res.getAddressId().equals(addressId));
         customerAddresses.add(updatedResponse);
          customerRepository.save(customer);
         return modelMapper.map(updatedResponse, AddressDTO.class);
	}

    public String DeleteAddress(Long addressId) {
        Address addressDB = addressRepository.findById(addressId)
         .orElseThrow(()-> new ResponseStatusException(HttpStatus.BAD_REQUEST,"Address this id is not present"));
        
         Customer customer=addressDB.getCustomer();
         List<Address> customerAddresses=customer.getAddresses();
         customerAddresses.removeIf(res-> res.getAddressId().equals(addressId));
        customerRepository.save(customer);
        addressRepository.delete(addressDB);    
        return "address deleted succesfully.";
    }
    
}
