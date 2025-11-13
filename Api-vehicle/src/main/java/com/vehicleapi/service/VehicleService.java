package com.vehicleapi.service;

import com.vehicleapi.model.Vehicle;
import com.vehicleapi.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    public Optional<Vehicle> getVehicleById(String id) {
        return vehicleRepository.findById(id);
    }

    public Vehicle createVehicle(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }

    public Vehicle updateVehicle(String id, Vehicle vehicleDetails) {
        Optional<Vehicle> vehicle = vehicleRepository.findById(id);
        if (vehicle.isPresent()) {
            Vehicle existingVehicle = vehicle.get();
            existingVehicle.setBrand(vehicleDetails.getBrand());
            existingVehicle.setModel(vehicleDetails.getModel());
            existingVehicle.setYear(vehicleDetails.getYear());
            existingVehicle.setColor(vehicleDetails.getColor());
            existingVehicle.setLicensePlate(vehicleDetails.getLicensePlate());
            return vehicleRepository.save(existingVehicle);
        }
        return null;
    }

    public boolean deleteVehicle(String id) {
        if (vehicleRepository.existsById(id)) {
            vehicleRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
