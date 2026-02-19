package com.example.demo.application.usecases;

import com.example.demo.application.execeptions.CupomConcurrencyException;
import com.example.demo.application.execeptions.CupomNotFound;
import com.example.demo.domain.Cupom;
import com.example.demo.domain.CupomRepository;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

@Component
public class DeleteCupomUseCase {
    private final CupomRepository cupomRepository;

    public DeleteCupomUseCase(CupomRepository cupomRepository) {
        this.cupomRepository = cupomRepository;
    }

    @Transactional
    public void execute(Long id){

        Cupom cupom = this.cupomRepository.findById(id).orElseThrow(
                () -> new CupomNotFound("Cupom a ser deletado não foi encontrado")
        );

        try {
            cupom.delete();

            this.cupomRepository.save(cupom);

        } catch (OptimisticLockException ex) {

            throw new CupomConcurrencyException(
                    "Cupom foi modificado por outra transação"
            );
        }
    }
}
