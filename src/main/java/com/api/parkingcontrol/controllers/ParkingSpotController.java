package com.api.parkingcontrol.controllers;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
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
}
