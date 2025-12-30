package com.gms.controller.admin;

import com.gms.dto.request.GeneratorRequest;
import com.gms.dto.response.GeneratorResponse;
import com.gms.entity.Generator;
import com.gms.exception.ResourceNotFoundException;
import com.gms.repository.GeneratorRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/generators")
@RequiredArgsConstructor
public class AdminGeneratorController {

    private final GeneratorRepository generatorRepository;

    @GetMapping
    public ResponseEntity<Page<GeneratorResponse>> getAllGenerators(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Generator> generators = generatorRepository.findAll(pageable);

        Page<GeneratorResponse> response = generators.map(this::toResponse);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GeneratorResponse> getGeneratorById(@PathVariable Long id) {
        Generator generator = generatorRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Generator not found"));
        return ResponseEntity.ok(toResponse(generator));
    }

    @PostMapping
    public ResponseEntity<GeneratorResponse> createGenerator(@Valid @RequestBody GeneratorRequest request) {
        Generator generator = Generator.builder()
            .model(request.getModel())
            .name(request.getName())
            .capacity(request.getCapacity())
            .locationName(request.getLocationName())
            .ownerEmail(request.getOwnerEmail())
            .latitude(request.getLatitude())
            .longitude(request.getLongitude())
            .note(request.getNote())
            .build();

        Generator savedGenerator = generatorRepository.save(generator);
        return ResponseEntity.ok(toResponse(savedGenerator));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GeneratorResponse> updateGenerator(
            @PathVariable Long id,
            @Valid @RequestBody GeneratorRequest request) {
        Generator generator = generatorRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Generator not found"));

        generator.setModel(request.getModel());
        generator.setName(request.getName());
        generator.setCapacity(request.getCapacity());
        generator.setLocationName(request.getLocationName());
        generator.setOwnerEmail(request.getOwnerEmail());
        generator.setLatitude(request.getLatitude());
        generator.setLongitude(request.getLongitude());
        generator.setNote(request.getNote());

        Generator updatedGenerator = generatorRepository.save(generator);
        return ResponseEntity.ok(toResponse(updatedGenerator));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGenerator(@PathVariable Long id) {
        Generator generator = generatorRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Generator not found"));

        generatorRepository.delete(generator);

        return ResponseEntity.ok().build();
    }

    private GeneratorResponse toResponse(Generator generator) {
        return GeneratorResponse.builder()
            .id(generator.getId())
            .model(generator.getModel())
            .name(generator.getName())
            .capacity(generator.getCapacity())
            .locationName(generator.getLocationName())
            .ownerEmail(generator.getOwnerEmail())
            .latitude(generator.getLatitude())
            .longitude(generator.getLongitude())
            .note(generator.getNote())
            .createdAt(generator.getCreatedAt())
            .build();
    }
}
