package org.example.mutantes.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.mutantes.validation.ValidDnaSequence;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request para verificar ADN")
public class DnaRequest {

    @Schema(
            description = "Secuencia de ADN (matriz NxN)",
            example = "[\"ATGCGA\",\"CAGTGC\",\"TTATGT\",\"AGAAGG\",\"CCCCTA\",\"TCACTG\"]"
    )
    @NotNull(message = "El ADN no puede ser null")
    @NotEmpty(message = "El ADN no puede estar vacío")
    @ValidDnaSequence
    private String[] dna;
}