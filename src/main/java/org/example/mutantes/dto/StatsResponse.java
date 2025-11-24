package org.example.mutantes.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Estadísticas de verificaciones")
public class StatsResponse {

    @Schema(example = "40")
    private long count_mutant_dna;

    @Schema(example = "100")
    private long count_human_dna;

    @Schema(example = "0.4")
    private double ratio;
}