package com.api.parkingcontrol.controllers;


import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.parkingcontrol.dtos.ParkingSpotDto;
import com.api.parkingcontrol.models.ParkingSpotModel;
import com.api.parkingcontrol.services.ParkingSpotService;

import jakarta.validation.Valid;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/parking-spot")
public class ParkingSpotController {

	final ParkingSpotService parkingSpotService;

	public ParkingSpotController(ParkingSpotService parkingSpotService) {
		this.parkingSpotService = parkingSpotService;
	}

	// O bean @RequestBody é para que se receba os dados de parkingSpotDto no
	// formato json
	// O bean @Valid é importante pq sem ele nao serão feitas as validacoes
	// presentes na classe ParkingSpotDto para os atributos como por exemplo
	// @NotBlank e @Size
	@PostMapping
	public ResponseEntity<Object> saveParkingSlot(@RequestBody @Valid ParkingSpotDto parkingSpotDto) {
		if (parkingSpotService.existsByLicensePlateCar(parkingSpotDto.getLicensePlateCar()))
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Essa placa de carro já está cadastrada!");

		if (parkingSpotService.existsByParkingSpotNumber(parkingSpotDto.getParkingSpotNumber()))
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Esse número de vaga já está em utilização!");

		if (parkingSpotService.existsByApartmentAndBlock(parkingSpotDto.getApartment(), parkingSpotDto.getBlock()))
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Já existe vaga registrada para esse apartamento!");

		// o var declara a variavel sem que seja necessario especificar o tipo, ele
		// mesmo atribui
		var parkingSpotModel = new ParkingSpotModel();

		// abaixo o metodo converte os dados da classe parkingSpotDto para
		// parkingSpotModel, ou seja o objeto vazio parkingSpotModel recebera as
		// informacoes do objeto parkingSpotDto
		BeanUtils.copyProperties(parkingSpotDto, parkingSpotModel);
		parkingSpotModel.setRegistrationDate(LocalDateTime.now(ZoneId.of("UTC")));

		return ResponseEntity.status(HttpStatus.CREATED).body(parkingSpotService.save(parkingSpotModel));
	}

	// implementacao do metodo get pra buscar toda a lista de vagas
	@GetMapping
	public ResponseEntity<Page<ParkingSpotModel>> getAllParkingSlots(
			@PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
		return ResponseEntity.status(HttpStatus.OK).body(parkingSpotService.findAll(pageable));
	}

	// implementacao do metodo get pra buscar por id
	@GetMapping("/{id}")
	public ResponseEntity<Object> getOneParkingSlot(@PathVariable(value = "id") UUID id) {
		Optional<ParkingSpotModel> parkingSpotModelOptional = parkingSpotService.findById(id);
		if (!parkingSpotModelOptional.isPresent())
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vaga de garagem não encontrada!");

		return ResponseEntity.status(HttpStatus.OK).body(parkingSpotModelOptional.get());
	}

	// implementacao do metodo delete para apagar registro no banco por id
	@DeleteMapping("/{id}")
	public ResponseEntity<Object> deleteParkingSlot(@PathVariable(value = "id") UUID id) {
		Optional<ParkingSpotModel> parkingSpotModelOptional = parkingSpotService.findById(id);
		if (!parkingSpotModelOptional.isPresent())
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vaga de garagem não encontrada!");

		parkingSpotService.delete(parkingSpotModelOptional.get());

		return ResponseEntity.status(HttpStatus.OK).body("Vaga de garagem deletada com sucesso!");
	}

	@PutMapping("/{id}")
	public ResponseEntity<Object> updateParkingSlot(@PathVariable(value = "id") UUID id,
			@RequestBody @Valid ParkingSpotDto parkingSpotDto) {

		Optional<ParkingSpotModel> parkingSpotModelOptional = parkingSpotService.findById(id);
		if (!parkingSpotModelOptional.isPresent())
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vaga de garagem não encontrada!");

		var parkingSpotModel = parkingSpotModelOptional.get();

		// primeiro metodo para atualizacao dos dados em banco de dados
		/*
		 * parkingSpotModel.setParkingSpotNumber(parkingSpotDto.getParkingSpotNumber());
		 * parkingSpotModel.setLicensePlateCar(parkingSpotDto.getLicensePlateCar());
		 * parkingSpotModel.setModelCar(parkingSpotDto.getModelCar());
		 * parkingSpotModel.setBrandCar(parkingSpotDto.getBrandCar());
		 * parkingSpotModel.setColorCar(parkingSpotDto.getColorCar());
		 * parkingSpotModel.setResponsibleName(parkingSpotDto.getResponsibleName());
		 * parkingSpotModel.setApartment(parkingSpotDto.getApartment());
		 * parkingSpotModel.setBlock(parkingSpotDto.getBlock());
		 */

		// segundo metodo para atualizacao dos dados em banco de dados
		BeanUtils.copyProperties(parkingSpotDto, parkingSpotModel);
		parkingSpotModel.setId(parkingSpotModelOptional.get().getId());
		parkingSpotModel.setRegistrationDate(parkingSpotModelOptional.get().getRegistrationDate());

		return ResponseEntity.status(HttpStatus.OK).body(parkingSpotService.save(parkingSpotModel));
	}
}
