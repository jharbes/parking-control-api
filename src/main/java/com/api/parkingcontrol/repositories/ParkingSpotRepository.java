package com.api.parkingcontrol.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.api.parkingcontrol.models.ParkingSpotModel;

// JpaRepository possui varios metodos prontos pra serem utilizados pra transacoes com banco de dados, 
// como por exemplo buscar uma listagem de algum recurso, buscar um recurso unico, atualizar, 
// deletar entradas no banco de dados
@Repository // nao obrigatoria a notacao @Repository
public interface ParkingSpotRepository extends JpaRepository<ParkingSpotModel, UUID> {
	
	public boolean existsByLicensePlateCar(String licensePlateCar);
	
	public boolean existsByParkingSpotNumber(String parkingSpotNumber);

	public boolean existsByApartmentAndBlock(String apartment, String block);
}
