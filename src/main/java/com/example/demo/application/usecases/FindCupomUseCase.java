package com.example.demo.application.usecases;

import com.example.demo.domain.Cupom;
import com.example.demo.domain.CupomRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
@Component
public class FindCupomUseCase {

    private final CupomRepository cupomRepository;

    public FindCupomUseCase(CupomRepository cupomRepository) {
        this.cupomRepository = cupomRepository;
    }

    public Optional<Cupom> execute(Long id){

        return this.cupomRepository.findById(id);

    }
}
