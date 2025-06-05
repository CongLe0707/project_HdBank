    package com.example.Hdbank_project.dto;

    import com.example.Hdbank_project.model.CardType;

    import jakarta.validation.constraints.NotBlank;
    import lombok.Data;
    import org.antlr.v4.runtime.misc.NotNull;

    import java.time.LocalDate;

    @Data
    public class CreateCardRequest {

        @NotNull
        private CardType cardType;

        @NotBlank
        private String fullName;

        @NotBlank
        private String idNumber;

        @NotNull
        private LocalDate issuedDate;


        private String notes;

    }
