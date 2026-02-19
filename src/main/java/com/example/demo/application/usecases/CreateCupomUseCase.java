package com.example.demo.application.usecases;

import com.example.demo.application.dtos.CreateCupomDto;
import com.example.demo.application.dtos.CupomResponse;
import com.example.demo.application.execeptions.CodeAlreadyExists;
import com.example.demo.domain.CodeCupom;
import com.example.demo.domain.Cupom;
import com.example.demo.domain.CupomRepository;
import com.example.demo.domain.exceptions.InvalidCodeException;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CreateCupomUseCase {

    private final CupomRepository cupomRepository;

    public CreateCupomUseCase(CupomRepository cupomRepository){
        this.cupomRepository = cupomRepository;
    }

    @Transactional
    public CupomResponse execute(CreateCupomDto request){
        String normalizedCode = new CodeCupom(request.code()).getValue();

       boolean exists = this.cupomRepository.existsByCode(normalizedCode);

        //Ou exeção genérica se code for sensível
        if(exists){
            throw new CodeAlreadyExists("Cupom com esse código já existe");
        }

        Cupom coupon = new Cupom(
                request.code(),
                request.description(),
                request.discountValue(),
                request.expirationDate(),
                request.published() != null ? request.published() : false
        );

        try{
            // Salva
            Cupom saved = cupomRepository.save(coupon);

            // Retorna DTO
            return CupomResponse.from(saved);
        }catch (DataIntegrityViolationException ex){
            throw new InvalidCodeException("Cupom já existe");
        }


    }


}
