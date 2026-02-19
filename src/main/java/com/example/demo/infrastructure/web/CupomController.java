package com.example.demo.infrastructure.web;

import com.example.demo.application.dtos.CreateCupomDto;
import com.example.demo.application.dtos.CupomResponse;
import com.example.demo.application.usecases.CreateCupomUseCase;
import com.example.demo.application.usecases.DeleteCupomUseCase;
import com.example.demo.application.usecases.FindCupomUseCase;
import com.example.demo.domain.Cupom;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/cupom")
public class CupomController {
    private final CreateCupomUseCase createCouponUseCase;
    private final DeleteCupomUseCase deleteCouponUseCase;
    private final FindCupomUseCase findCouponUseCase;

    public CupomController(CreateCupomUseCase createCouponUseCase, DeleteCupomUseCase deleteCouponUseCase, FindCupomUseCase findCouponUseCase) {
        this.createCouponUseCase = createCouponUseCase;
        this.deleteCouponUseCase = deleteCouponUseCase;
        this.findCouponUseCase = findCouponUseCase;
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CupomResponse create(@Valid @RequestBody CreateCupomDto request) {
        return createCouponUseCase.execute(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        deleteCouponUseCase.execute(id);
    }

    @GetMapping("/{id}")
    public Optional<Cupom> findById(@PathVariable Long id) {
        return findCouponUseCase.execute(id);
    }
}
