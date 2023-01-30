package com.api.parkingcontrol.services;

import org.springframework.stereotype.Service;

import com.api.parkingcontrol.models.ParkingSpotModel;
import com.api.parkingcontrol.repositories.ParkingSpotRepository;

import jakarta.transaction.Transactional;

@Service
public class ParkingSpotService {

	// injecao de dependencia de ParkingSpotRepository em ParkingSpotService
	final ParkingSpotRepository parkingSpotRepository;

	public ParkingSpotService(ParkingSpotRepository parkingSpotRepository) {
		this.parkingSpotRepository = parkingSpotRepository;
	}

	// é interessante utilizar o bean transactional para que caso haja algum erro na
	// transacao o proprio sistema desfaça a operação impedindo de se obter
	// inconsistencias e/ou dados quebrados no banco de dados 
	@Transactional
	public Object save(ParkingSpotModel parkingSpotModel) {

		return parkingSpotRepository.save(parkingSpotModel);
	}
}
